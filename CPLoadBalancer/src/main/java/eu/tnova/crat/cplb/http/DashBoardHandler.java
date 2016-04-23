package eu.tnova.crat.cplb.http;

import java.util.Iterator;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.model.CpInstance;
import eu.tnova.crat.cplb.model.MachineMonitoringMetrics;
import eu.tnova.crat.cplb.model.OFSwitch;

public class DashBoardHandler extends HttpHandler {

	@Override
	public void service(Request request, Response response) throws Exception {
		// TODO Auto-generated method stub
				response.setContentType("application/json");
				
				JSONArray jarray = new JSONArray();
					
				Iterator<CpInstance> it = TempData.cpInstances.values().iterator();
				while (it.hasNext()) {
			        CpInstance cp = (CpInstance)it.next();
			        JSONObject tmpjo= new JSONObject();
			        MachineMonitoringMetrics mmm = cp.getMachineMonitoringDataCurrent();
//			        if (mmm != null)
			        //tmpjo.put("metrics", mmm.toJSON());
			        tmpjo.put("instanceIp", cp.getIp());
			        tmpjo.put("switches", OFSwitch.toJSONArray(cp.getSwitches()));
			        //tmpjo.put("logs", cp.getLogs());
			        jarray.put(tmpjo);
					// avoids a ConcurrentModificationException
			    }
			    			
				
				response.getWriter().append(jarray.toString());
								
			}
		
	}

