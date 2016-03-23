package eu.tnova.crat.cplb.model;

import java.io.Serializable;

public class OFSwitchMonitoringMetadata implements Serializable {

    @Override
    public String toString() {
        return "OFSwitchMonitoringMetadata [switchID=" + switchID + "]";
    }

    public String switchID = "";
    public double delay = 0;
    
    public String getSwitchID() {
        return switchID;
    }

    public void setSwitchID(String switchID) {
        this.switchID = switchID;
    }

}
