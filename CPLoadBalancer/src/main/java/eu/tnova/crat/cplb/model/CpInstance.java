package eu.tnova.crat.cplb.model;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.common.collect.EvictingQueue;
import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;
import eu.tnova.crat.cplb.utils.Utils;


public class CpInstance {

    private String ip;
    private String sshUser;
    private String sshPassword;
    private int sshPort;
    private String sshKeypath;
    
    private MachineMonitoringMetrics MachineMonitoringMetricsCurrent;
    private EvictingQueue<MachineMonitoringMetrics> MachineMonitoringMetricsHistory;
    
    private ODLOpenFlowMonitoringMetrics ODLOpenFlowMonitoringMetricsCurrent;
    private EvictingQueue<ODLOpenFlowMonitoringMetrics> ODLOpenFlowMonitoringMetricsHistory;
   // public boolean monitoringTaskActive = false;
    
    private HashMap<String, OFSwitch> OFSwitches;
    public EvictingQueue<String> Log;
    
    

    public CpInstance(String ipInstance, int sshPort, String sshUser, String sshPwd, String sshKeypath) {
        this.ip = ipInstance;
        this.sshUser = sshUser;
        this.sshPassword = sshPwd;
        this.sshPort = sshPort;
        this.sshKeypath = sshKeypath;
        
        MachineMonitoringMetricsHistory = EvictingQueue.create(Constants.monitoringDataHistoryLenght);
    //    monitoringTaskActive = false;
        ODLOpenFlowMonitoringMetricsHistory = EvictingQueue.create(Constants.monitoringDataHistoryLenght);
        OFSwitches = new HashMap<String, OFSwitch>();
        Log = EvictingQueue.create(5);
    }

    
    public String getIp() {
        return ip;
    }
    
    
    public int getSSHPort(){
    	return sshPort;
    }
    
    
    public String getSSHUser() {
        return sshUser;
    }

    
    public String getSSHPassword() {
        return sshPassword;
    }
    
    public String getSSHKeypath() {
        return sshPassword;
    }
    
    public void setIp(String ipInstance) {
        this.ip = ipInstance;
    }
    
    
    public void addMachineMonitoringMetadata(MachineMonitoringMetrics mmm){
    	MachineMonitoringMetricsCurrent = mmm;
    	MachineMonitoringMetricsHistory.add(mmm);
    }
    
    
    public void addODLOpenFlowMonitoringMetadata(ODLOpenFlowMonitoringMetrics mmm){
    	ODLOpenFlowMonitoringMetricsCurrent = mmm;
    	ODLOpenFlowMonitoringMetricsHistory.add(mmm);
    }

    
	public ODLOpenFlowMonitoringMetrics getODLOpenFlowMonitoringDataCurrent() {
		// TODO Auto-generated method stub
		return ODLOpenFlowMonitoringMetricsCurrent;
	}
	
	
	public MachineMonitoringMetrics getMachineMonitoringDataCurrent() {
		// TODO Auto-generated method stub
		return MachineMonitoringMetricsCurrent;
	}
	
	
	public HashMap<String, OFSwitch> getSwitches(){
		return OFSwitches;
	}
	
	
	public boolean addSwitch(String id, OFSwitch ofs){
		if (OFSwitches.get(id) == null){
			OFSwitches.put(id, ofs);
			return true;
		}
		return false;
	}
	
	
	public void setSwitchDelay(String id, double delay){
		OFSwitch ofs = OFSwitches.get(id);
		if (ofs == null){
			OFSwitches.put(id, new OFSwitch(id));
		}
		OFSwitches.get(id).delay = delay;
	
	}
	
	
	public void setSwitchRole(String id, int role){
		OFSwitch ofs = OFSwitches.get(id);
		if (ofs == null){
			OFSwitches.put(id, new OFSwitch(id));
		}
		OFSwitches.get(id).role = role;
	
	}
	
	
	public void addLog(String log){
		String date = Utils.ConvertMilliSecondsToFormattedDate(System.currentTimeMillis());
		Log.add(date + ": " + log);
	}
	
	
	public String[] getLogs(){
		return Log.toArray(new String[0]);
	}

	
	public ArrayList<OFSwitch> getSwitchesIsMasterFor(){
		ArrayList<OFSwitch> result = new ArrayList<OFSwitch>();
		for (String contr : OFSwitches.keySet()) {
	            OFSwitch sw = OFSwitches.get(contr);
	            if (sw.role == Constants.OFPCRROLEMASTER)
	            	result.add(sw);
		 }
		return result;
	}
    
    public long getTotalCounterSend(){
        long result = 0;
        for (String contr : OFSwitches.keySet()) {
                OFSwitch sw = OFSwitches.get(contr);
                if (sw.role == Constants.OFPCRROLEMASTER)
                    result = result + TempData.switchesStats.get(sw.getSwitchID()).countReceivedFromController;
         }
        return result;
    }

}