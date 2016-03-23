package eu.tnova.crat.cplb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ODLOpenFlowMonitoringMetrics implements Serializable {

	public ODLOpenFlowMonitoringMetrics(){
	switchesMonitoringMetadata = new ArrayList<OFSwitch>();	
	}
	
    public List<OFSwitch> switchesMonitoringMetadata;

    public List<OFSwitch> getSwitchesMonitoringMetadata() {
        return switchesMonitoringMetadata;
    }

    public void setSwitchesMonitoringMetadata(
            List<OFSwitch> switchesMonitoringMetadata) {
        this.switchesMonitoringMetadata = switchesMonitoringMetadata;
    }

    @Override
    public String toString() {
        return "CpInstanceOdlOpenFlowMonitoringMetadata [switchesMonitoringMetadata="
                + switchesMonitoringMetadata + "]";
    }

}
