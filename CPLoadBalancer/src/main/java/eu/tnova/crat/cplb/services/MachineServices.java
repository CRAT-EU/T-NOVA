package eu.tnova.crat.cplb.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.model.CpInstance;
import eu.tnova.crat.cplb.model.MachineMonitoringMetrics;
import eu.tnova.crat.cplb.utils.Utils;

public class MachineServices {

	public static void updateMachineMetrics(String ip) throws JSchException, IOException, NumberFormatException{
		
		CpInstance cp = TempData.cpInstances.get(ip);
		if (cp != null) {
		
		JSch jsch = new JSch();
		Session session;
		MachineMonitoringMetrics cpimm = new MachineMonitoringMetrics();
			session = jsch.getSession(cp.getSSHUser(), cp.getIp(), cp.getSSHPort());
			session.setPassword(cp.getSSHPassword());
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			channel.setCommand("" + "free | awk 'FNR == 3 {print $3 + $4}';" // total RAM
					+ "free | awk 'FNR == 3 {print $3}';" // used RAM
					+ "free | awk 'FNR == 3 {print $4}';" // free RAM
					+ "nproc;" // num CPUs
					+ "uptime;" + "");
			channel.connect();
			String aux = in.readLine();
			cpimm.setTotalRam(Float.parseFloat(aux));
			aux = in.readLine();
			cpimm.setUsedRam(Float.parseFloat(aux));
			aux = in.readLine();
			cpimm.setFreeRam(Float.parseFloat(aux));
			aux = in.readLine();
			cpimm.setnCPU(new Integer(aux));
			aux = in.readLine();
			double[] avg = Utils.parseUptimeResult(aux);
			if (avg != null) {
			cpimm.setLoadAvgOneMinute(avg[0]);
			cpimm.setLoadAvgFiveMinute(avg[1]);
			cpimm.setLoadAvgFifteenMinute(avg[2]);
			}
			TempData.LOGGER.info("Monitoring instance's machine metadata for instance " + ip + ": " + cpimm.toString());
			// while((msg=in.readLine())!=null){
			// TempData.LOGGER.info(msg);
			// }
			in.close();
			channel.disconnect();
			session.disconnect();
			
			// UPDATE CP INSTANCE
			cp.addMachineMonitoringMetadata(cpimm);	
		}
	}
}
