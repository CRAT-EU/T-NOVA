package eu.tnova.crat.cplb.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.model.CpInstance;
import eu.tnova.crat.cplb.model.OFSwitch;
import eu.tnova.crat.cplb.services.ODLServices;

public class LoadBalancerThread implements Runnable{

    
    public void run() {
        TempData.LOGGER.info("Doing Load Balancer...");
        balanceNumberMasters();     
    }
    
    
    public static void balanceCluster() {
        List<Migration> migrations = new LinkedList<Migration>();
        TempData.LOGGER.info("Balance based on the 'weight' of the OF nodes");
        
        List<String> controllersIps = new ArrayList<String>();
        for (String contr : TempData.cpInstances.keySet()) {
            controllersIps.add(contr);
            CpInstance cp = TempData.cpInstances.get(contr);
        }
        
        List<String> switchToBeNotConsidered = new ArrayList<String>();
        for(int i=0; i<TempData.cpInstances.keySet().size(); i++){
            for(int j=0; j<TempData.cpInstances.keySet().size(); j++){
                if(i!=j){
                    for(int k=0; k<3; k++){
                        long offset = getOffesetBetweenController(controllersIps.get(i), controllersIps.get(j));
                        TempData.LOGGER.info("Workload offset between "+controllersIps.get(i)+" and "+controllersIps.get(j)+" = "+offset);
                        if(offset>0 && offset>Constants.OFFSET_TREESHOLD){
                            String sId = getSwitchCandidateToMigration(controllersIps.get(i), offset, switchToBeNotConsidered);
                            if(sId!=null){
                                switchToBeNotConsidered.add(sId);
                                Migration m = new Migration(controllersIps.get(i), controllersIps.get(j), sId);
                                migrations.add(m);
                                TempData.LOGGER.info(m.toString());
                                try {
                                    migrate(migrations);
                                    migrations.clear();
                                    
                                } catch (Exception e) {
                                    TempData.LOGGER.info("Error in migrating switches:" + e.getMessage());
                                }
                            }
                        }
                    }
                }
              } 
            }
        }
    
    
        
    private static String getSwitchCandidateToMigration(String contr, long targetOffset, List<String> switchToBeNotConsidered) {
        String ts = null;
        for(OFSwitch ofs : TempData.cpInstances.get(contr).getSwitchesIsMasterFor()){
            if(ofs.countReceivedFromController<(targetOffset+10) && !switchToBeNotConsidered.contains(ofs.switchID))
                ts = ofs.switchID;
        }
        return ts;
    }


    private static long getOffesetBetweenController(String c1, String c2) { 
        return TempData.cpInstances.get(c1).getTotalCounterSend()-TempData.cpInstances.get(c2).getTotalCounterSend();
    }



    public static void balanceNumberMasters() {
    	List<Migration> migrations = new LinkedList<Migration>();
    	TempData.LOGGER.info("Balance based on the number of nodes currently being cared of by each controller");
    	int mean = TempData.ofSwitches.size()
                / TempData.cpInstances.size();
        int remainder = TempData.ofSwitches.size()
                % TempData.cpInstances.size();
        ArrayList<ArrayList<OFSwitch>> nodesMap = new ArrayList<ArrayList<OFSwitch>>();
        ArrayList<Integer> counter = new ArrayList<Integer>();
        String[] instances = new String[TempData.cpInstances.size()];
        TempData.LOGGER.info("mean="+mean+" remainder=" + remainder);
       
        int i = 0;
        for (String contr : TempData.cpInstances.keySet()) {
            CpInstance cp = TempData.cpInstances.get(contr);
            ArrayList<OFSwitch> switches = cp.getSwitchesIsMasterFor();
            instances[i] = contr;
            counter.add(switches.size());
            nodesMap.add(switches);  
            i++;
        }
        
        
        for (int idx = 0; idx < instances.length; idx++) {
        	ArrayList<OFSwitch> switches = nodesMap.get(idx);
        	while (nodesMap.get(idx).size() > mean + 1){
        		int candidate_idx = counter.indexOf(Collections.min(counter));
        		if (candidate_idx != idx){
        			OFSwitch ofswitch = switches.get(0); 
        			migrations.add(new Migration(instances[idx], instances[candidate_idx], ofswitch.getSwitchID()));
        			OFSwitch ofs = nodesMap.get(idx).remove(0);
        			nodesMap.get(candidate_idx).add(ofs);
        			counter.set(idx, nodesMap.get(idx).size());
         			counter.set(candidate_idx, nodesMap.get(candidate_idx).size());
        		} else {
        			break;
        		}
        	}
        }
        
        try {
			migrate(migrations);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TempData.LOGGER.info("Error in migrating switches:" + e.getMessage());
	    }
 
    }
       
    
    
    private static void migrate(List<Migration> migrations) throws Exception{
    	for (Migration m : migrations){
    		TempData.LOGGER.info(m.toString());  		
    		ODLServices.setNodesRole(m.to, new String[] { m.switch_id }, Constants.OFPCRROLEEQUAL);
    		Thread.sleep(100);
    		ODLServices.setNodesRole(m.from, new String[] { m.switch_id }, Constants.OFPCRROLESLAVE);
    		ODLServices.setNodesRole(m.to, new String[] { m.switch_id }, Constants.OFPCRROLEMASTER);
    		TempData.cpInstances.get(m.from).getSwitches().get(m.switch_id).role = Constants.OFPCRROLESLAVE;
    		TempData.cpInstances.get(m.to).getSwitches().get(m.switch_id).role = Constants.OFPCRROLEMASTER;
    	}
    }

    
    static class Migration{
    	public String from;
    	public String to;
    	public String switch_id;
    	
    	public Migration(String f, String t, String s){
    		from = f;
    		to = t;
    		switch_id = s;
    	}
    	
    	public String toString(){
    		return "From: " + from + " To:" + to + " Switch_id: " + switch_id;
    	}
    }

}
