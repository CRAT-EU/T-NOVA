package eu.tnova.crat.cplb.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

import eu.tnova.crat.cplb.model.CpInstance;
import eu.tnova.crat.cplb.model.OFSwitch;

public class TempData {

    public static Logger LOGGER;
    public static List<String> instanceAddresses = new ArrayList<String>();
    public static Map<String, CpInstance> cpInstances = new LinkedHashMap<String, CpInstance>();
    public static Map<String, OFSwitch> switchesStats = new LinkedHashMap<String, OFSwitch>();
    public static HashSet<String> ofSwitches = new HashSet<String>();
    public static List<ScheduledThreadPoolExecutor> scheduledThreadPoolExcecutors = new ArrayList<ScheduledThreadPoolExecutor>();
    //public static Client client;
    //public static List<NewCookie> odlCookies = null;

}