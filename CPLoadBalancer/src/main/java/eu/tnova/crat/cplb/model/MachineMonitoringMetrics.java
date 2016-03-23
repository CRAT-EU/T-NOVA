package eu.tnova.crat.cplb.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.json.JSONObject;

import eu.tnova.crat.cplb.utils.Utils;


public class MachineMonitoringMetrics implements Serializable {

    public Timestamp timestamp;
    public double nSwitch = -1;
    private float freeRam = -1;
    private float usedRam = -1;
    private float totalRam = -1;
    private int nCPU = -1;
    private double loadAvgOneMinute = -1;
    private double loadAvgFiveMinute = -1;
    private double loadAvgFifteenMinute = -1;


    public MachineMonitoringMetrics() {
        timestamp = new Timestamp(new DateTime().getMillis());
    }


    public double getnSwitch() {
        return nSwitch;
    }


    public void setnSwitch(double nSwitch) {
        this.nSwitch = nSwitch;
    }


    public float getFreeRam() {
        return freeRam;
    }


    public void setFreeRam(float freeRam) {
        this.freeRam = freeRam / 1024;
    }


    public float getUsedRam() {
        return usedRam;
    }


    public void setUsedRam(float usedRam) {
        this.usedRam = usedRam / 1024;
    }


    public float getTotalRam() {
        return totalRam;
    }


    public void setTotalRam(float totalRam) {
        this.totalRam = totalRam / 1024;
    }


    public int getnCPU() {
        return nCPU;
    }


    public void setnCPU(int nCPU) {
        this.nCPU = nCPU;
    }


    public double getLoadAvgOneMinute() {
        return Math.min(loadAvgOneMinute, 1);
    }


    public void setLoadAvgOneMinute(double loadAvgOneMinute) {
        this.loadAvgOneMinute = loadAvgOneMinute;
    }


    public double getLoadAvgFiveMinute() {
        return Math.min(loadAvgFiveMinute, 1);
    }


    public void setLoadAvgFiveMinute(double loadAvgFiveMinute) {
        this.loadAvgFiveMinute = loadAvgFiveMinute;
    }


    public double getLoadAvgFifteenMinute() {
        return Math.min(loadAvgFifteenMinute, 1);
    }


    public void setLoadAvgFifteenMinute(double loadAvgFifteenMinute) {
        this.loadAvgFifteenMinute = loadAvgFifteenMinute;
    }


    @Override
    public String toString() {
        return "CpInstanceMonitoringMetadata [timestamp=" + timestamp
                + ", nSwitch=" + nSwitch + ", freeRam=" + freeRam
                + ", usedRam=" + usedRam + ", totalRam=" + totalRam + ", nCPU="
                + nCPU + ", loadAvgOneMinute=" + loadAvgOneMinute
                + ", loadAvgFiveMinute=" + loadAvgFiveMinute
                + ", loadAvgFifteenMinute=" + loadAvgFifteenMinute + "]";
    }
    
    public JSONObject toJSON(){
    	DecimalFormat df = new DecimalFormat("###.##");
    	JSONObject jo = new JSONObject();
    	jo.put("timestamp", timestamp);
    	jo.put("nSwitch", nSwitch);
    	jo.put("freeRam", Utils.round(getFreeRam()));
    	jo.put("usedRam", Utils.round(getUsedRam()));
    	jo.put("totalRam", Utils.round(getTotalRam()));
    	jo.put("loadAvg1m", getLoadAvgOneMinute());
    	jo.put("loadAvg5m", getLoadAvgFiveMinute());
    	jo.put("loadAvg15m", getLoadAvgFifteenMinute());
    	
    	return jo;
    }


}
