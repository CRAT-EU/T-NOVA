package eu.tnova.crat.cplb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.http.DashBoardHandler;
import eu.tnova.crat.cplb.model.CpInstance;
import eu.tnova.crat.cplb.services.ODLServices;
import eu.tnova.crat.cplb.task.LoadBalancerThread;
import eu.tnova.crat.cplb.task.WorkerMonitoringThread;
import eu.tnova.crat.cplb.utils.Utils;

public class CPLoadBalancer {

	/*
	 * TODO Externalize configuration loading from file (use some library for
	 * that)
	 */

	public static void main(String[] args) {
		inizializeLogger();
		configInstances();
		// setupAuthenticatedClient();
		setupMonitoringInstanceTasks();
		//doLoadBalancingTask();
		setupHTTPServer();
	}

	private static void setupHTTPServer() {
		// TODO Auto-generated method stub
		final HttpServer server = HttpServer.createSimpleServer("webroot", Constants.HTTP_SERVER_PORT);
		server.getServerConfiguration().addHttpHandler(new DashBoardHandler(), "/lb");
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {
			
			@Override
			public void service(Request request, Response response) throws Exception {
				// TODO Auto-generated method stub
				String cp = request.getParameter("cp");
				String sw = request.getParameter("switches");
				String role = request.getParameter("role");
				if (cp == null || sw == null || role == null){
					response.getWriter().append("ERROR! Please use: setrole?cp=127.0.0.1&switches=3,4&role=master");	
				} else {
					ODLServices.setNodesRole(cp, sw.split(","), Utils.getRole(role));
					response.getWriter().append("SUCCESS!");
				}
			}
		}, "/setrole");
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {
			
			@Override
			public void service(Request request, Response response) throws Exception {
				LoadBalancerThread.balanceNumberMasters();
			}
		}, "/balance");
	    server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            
            @Override
            public void service(Request request, Response response) throws Exception {
                //LoadBalancerThread.balanceNumberMasters();
                LoadBalancerThread.balanceCluster();
            }
        }, "/balancec");
		try {
			server.start();
			TempData.LOGGER.info("HTTPServer running..");
			Thread.currentThread().join();
		} catch (Exception e) {
			TempData.LOGGER.info("There was an error while starting Grizzly HTTP server: " + e.getMessage());
		}
		server.shutdown();
	}

	private static void doLoadBalancingTask() {
		TempData.LOGGER.info("Configure and start Load Balancing...");
		TempData.LOGGER.info("Scheduling LoadBalancer working Thread");
		ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(
				Constants.scheduledThreadPoolExecutorCorePoolSize);
		/*
		 * This will execute the WorkerThread continuously for every
		 * 'TempData.scheduledMonitoringThreadFixedTimeout' seconds with an
		 * initial delay of 'TempData.scheduledMonitoringThreadInitialDelay'
		 * seconds for the first WorkerThread to start execution cycle. In this
		 * case, whether the first WorkerThread is completed or not, the second
		 * WorkerThread will start exactly after 5 seconds hence called schedule
		 * at fixed rate. This continues till 'n' threads are executed.
		 */
		stpe.scheduleAtFixedRate(new LoadBalancerThread(), Constants.scheduledLoadbalancerThreadInitialDelay,
				Constants.scheduledLoadbalancerThreadFixedTimeout, TimeUnit.SECONDS);
	}

	private static void inizializeLogger() {
		TempData.LOGGER = Logger.getLogger("TnovaCPLB");
		TempData.LOGGER.info("LoadBalancer logger initialized");
	}

	private static void setupMonitoringInstanceTasks() {
		TempData.LOGGER.info("Configure and start monitoring instace tasks...");
		for (String iIp : TempData.cpInstances.keySet()) {
			String wmtName = "wmt@" + iIp;
			TempData.LOGGER.info("Started Worker Monitoring Thread " + wmtName + " execution loop...");
			ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(
					Constants.scheduledThreadPoolExecutorCorePoolSize);
			/*
			 * This will execute the WorkerThread continuously for every
			 * 'TempData.scheduledMonitoringThreadFixedTimeout' seconds with an
			 * initial delay of 'TempData.scheduledMonitoringThreadInitialDelay'
			 * seconds for the first WorkerThread to start execution cycle. In
			 * this case, whether the first WorkerThread is completed or not,
			 * the second WorkerThread will start exactly after 5 seconds hence
			 * called schedule at fixed rate. This continues till 'n' threads
			 * are executed.
			 */
			stpe.scheduleAtFixedRate(new WorkerMonitoringThread(wmtName, iIp),
					Constants.scheduledMonitoringThreadInitialDelay, Constants.scheduledMonitoringThreadFixedTimeout,
					TimeUnit.SECONDS);
		}
	}

	/*
	 * private static void setupAuthenticatedClient(){ TempData.client =
	 * Client.create(); TempData.client.addFilter(new
	 * HTTPBasicAuthFilter(Constants.ODL_username, Constants.ODL_password));
	 * for(String instanceIp : TempData.instanceAddresses){ WebResource
	 * webResource =
	 * TempData.client.resource(Constants.getODLNodesPath(instanceIp));
	 * ClientResponse response = webResource.get(ClientResponse.class);
	 * TempData.LOGGER.info("Getting cookies for instance at "+instanceIp);
	 * TempData.odlCookies = response.getCookies(); } //TempData.client =
	 * Client.create(); }
	 */

	public static void configInstances() {
		TempData.LOGGER.info("Loading instances configurations...");                
                
		FileReader fr = null;
		try {
			fr = new FileReader(Constants.configurationFileName);
		} catch (FileNotFoundException e) {
			TempData.LOGGER.severe(e.getMessage());
		}
		BufferedReader textReader = new BufferedReader(fr);
		String numberOfInstances = "0";
		try {
			numberOfInstances = textReader.readLine();

		} catch (IOException e) {
			TempData.LOGGER.severe(e.getMessage());
		}
		String l = "Found " + numberOfInstances + " instance(s) configurations...";
		TempData.LOGGER.info(l);
		int numOfInstances = Integer.parseInt(numberOfInstances);
		// TempData.cpInstances = new ArrayList<CpInstance>();
		Constants.numberOfInstancies = numOfInstances;
		String cp_config = "";
		for (int i = 0; i < numOfInstances; i++) {
			l = "Configuring instace #" + (i + 1) + ", IP:";
			String ip = "0.0.0.0";
			String user = "";
			String pwd = "";
                        String keypath = "";
			int port = 22;
			try {
				cp_config = textReader.readLine();
				String[] settings = cp_config.split(":");
				if (settings.length == 5) {
                                    ip = settings[0];
                                    port = Integer.parseInt(settings[1]);
                                    user = settings[2];
                                    pwd = settings[3];
                                    keypath = settings[4];
                                    l += ip;
                                    CpInstance cpi = new CpInstance(ip, port, user, pwd, keypath);
                                    TempData.instanceAddresses.add(ip);
                                    TempData.cpInstances.put(ip, cpi);
                                    TempData.LOGGER.info(l);
				}
			} catch (IOException e) {
				TempData.LOGGER.severe(e.getMessage());
			}
			
		}
	}
}