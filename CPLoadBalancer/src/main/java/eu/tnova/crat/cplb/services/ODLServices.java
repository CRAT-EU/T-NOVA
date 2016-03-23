package eu.tnova.crat.cplb.services;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;
import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.model.OFSwitch;
import eu.tnova.crat.cplb.utils.Utils;

public class ODLServices {

    
	public static void updateAllNodes(String instanceIp) throws ClientProtocolException, IOException {
			String path = Constants.getODLNodesPath(instanceIp);
			/*
			 * WebResource res = TempData.client.resource(path); Builder builder
			 * = res.type(MediaType.APPLICATION_JSON); for(NewCookie nc :
			 * TempData.odlCookies) builder.cookie(nc); String r =
			 * builder.get(String.class);
			 */
			
          TempData.LOGGER.info("Monitoring instance ODL OpenFlow metadata, getting " + path);
          JSONObject response = ODLRESTClient.get(path); 
          JSONArray ja = response.optJSONArray("topology");
          ja = response.optJSONArray("node");
          if (ja!=null){
          for (int i = 0; i < ja.length(); i++) {
              String switchId = ja.getJSONObject(i).getString("node-id").split(":")[1];
              OFSwitch ofs = new OFSwitch(switchId);
              TempData.cpInstances.get(instanceIp).addSwitch(switchId, ofs);
              TempData.ofSwitches.add(switchId);
              if(!TempData.switchesStats.keySet().contains(switchId))
                  TempData.switchesStats.put(switchId, new OFSwitch(switchId));
              }
          } 
          else {
              TempData.cpInstances.get(instanceIp).addLog("updateAllNodes error: " + response.toString());
          }
			
//			TempData.LOGGER.info("Monitoring instance ODL OpenFlow metadata, getting " + path);
//			JSONObject response = ODLRESTClient.get(path);
//
//			JSONObject nodes = response.getJSONObject("nodes");
//			
//			JSONArray ja = nodes.optJSONArray("node");
//			if (ja!=null){
//			for (int i = 0; i < ja.length(); i++) {
//				String switchId = ja.getJSONObject(i).getString("id").split(":")[1];
//				OFSwitch ofs = new OFSwitch(switchId);
//				TempData.cpInstances.get(instanceIp).addSwitch(switchId, ofs);
//				TempData.ofSwitches.add(switchId);
//			}
//			} else {
//				TempData.cpInstances.get(instanceIp).addLog("updateAllNodes error: " + response.toString());
//			}
			
		/*	
		 	OFSwitch ofs = new OFSwitch("1");
			TempData.cpInstances.get(instanceIp).addSwitch("1", ofs);
	
			OFSwitch ofs2 = new OFSwitch("2");
			TempData.cpInstances.get(instanceIp).addSwitch("2", ofs2);
			*/
	
	}

	
	
	public static void updateNodesRole(String instanceIp, String[] switch_ids) throws ClientProtocolException, IOException {
			String path = Constants.getODLGetRolesPath(instanceIp);
			if (switch_ids == null) {
				switch_ids = new String[] {};
			}
			JSONArray jarray = new JSONArray(switch_ids);
			JSONObject body = new JSONObject("{'input':{'switch-ids':" + jarray.toString() + "}}");		
			JSONObject response = ODLRESTClient.post(path, body);
			//JSONObject response = new JSONObject("{'output': { 'response-code': 0, 'response-message': [ '1:2:OFPCRROLEMASTER','2:3:OFPCRROLESLAVE']}}");
			JSONObject output = response.optJSONObject("output");		
			if (output != null) {
				int responsecode = output.getInt("response-code");
				JSONArray responsemessage = output.getJSONArray("response-message");
				for (int i = 0; i < responsemessage.length(); i++) {
					String message = responsemessage.getString(i);
					String[] info = message.split(":");
					String switch_id = info[0];
					String role_code = info[1];
					String role_name = info[2];
					TempData.cpInstances.get(instanceIp).setSwitchRole(switch_id, Integer.parseInt(role_code));
					if(!TempData.switchesStats.keySet().contains(switch_id))
		                  TempData.switchesStats.put(switch_id, new OFSwitch(switch_id));
				    TempData.switchesStats.get(switch_id).role=Integer.parseInt(role_code);
    				}
    			}
			else {
				TempData.cpInstances.get(instanceIp).addLog("updateNodesRole error: " + response.toString());
			    }
	}
	
	
	public static void updateNodesStatistics(String instanceIp, String[] switch_ids) throws ClientProtocolException, IOException {
        String path = Constants.getODLGetStatisticsPath(instanceIp);
        if (switch_ids == null) {
            switch_ids = new String[] {};
        }
        TempData.LOGGER.info("Updating Node's OF metrics for instance "+instanceIp);
        JSONArray jarray = new JSONArray(switch_ids);
        JSONObject body = new JSONObject("{'input':{'switch-ids':" + jarray.toString() + "}}");      
        JSONObject response = ODLRESTClient.post(path, body);
        //JSONObject response = new JSONObject("{'output': { 'response-code': 0, 'response-message': [ '1:2:OFPCRROLEMASTER','2:3:OFPCRROLESLAVE']}}");
        JSONObject output = response.optJSONObject("output");     
        if (output != null) {
            int responsecode = output.getInt("response-code");
            JSONArray responsemessage = output.getJSONArray("response-message");
            for (int i = 0; i < responsemessage.length(); i++) {
                String message = responsemessage.getString(i);
                String[] info = message.split(":");
                String switch_id = info[0];
                String countReceivedFromController = info[1];
                String countReceivedSentController = info[3];
//                String role_name = info[2];
                long crfc = Long.valueOf(countReceivedFromController);
                long cstc = Long.valueOf(countReceivedSentController);
                //TempData.cpInstances.get(instanceIp).getSwitches().get(switch_id).updateOFMetrics(crfc, cstc);
                if(!TempData.switchesStats.keySet().contains(switch_id))
                    TempData.switchesStats.put(switch_id, new OFSwitch(switch_id));
                TempData.switchesStats.get(switch_id).countReceivedFromController=crfc;
            }

        } else {
//                TempData.cpInstances.get(instanceIp).addLog("updateNodesRole error: " + response.toString());
        }
}
	

	public static int setNodesRole(String instanceIp, String[] switch_ids, int role) throws ClientProtocolException, IOException {
			String path = Constants.getODLSetRolesPath(instanceIp);
			if (switch_ids == null) {
				switch_ids = new String[] {};
			}
			JSONArray jarray = new JSONArray(switch_ids);
			JSONObject body = new JSONObject("{'input':{'ofp-role':" + Utils.getRoleCode(role) + ",'switch-ids':" + jarray.toString() + "}}");
			JSONObject response = ODLRESTClient.post(path, body);
		return 0;
	}
}
