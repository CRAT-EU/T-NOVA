package eu.tnova.crat.cplb.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;

public class Utils {


	public static String getRoleCode(int role) {
		// TODO Auto-generated method stub
		
		switch(role){
		case Constants.OFPCRROLEMASTER:
			return "BECOMEMASTER";
		case Constants.OFPCRROLESLAVE:
			return "BECOMESLAVE";
		default:
			return "BECOMEEQUAL";
		}
	}
	
	public static String getRoleString(int role) {
		// TODO Auto-generated method stub
		
		switch(role){
		case Constants.OFPCRROLEMASTER:
			return "BECOMEMASTER";
		case Constants.OFPCRROLESLAVE:
			return "BECOMESLAVE";
		case Constants.OFPCRROLEEQUAL:
			return "BECOMEEQUAL";
		default:
			return "NULL";
		}
	}
	
	public static int getRole(String s){
		if (s.equals("master"))
			return Constants.OFPCRROLEMASTER;
		if (s.equals("slave"))
			return Constants.OFPCRROLESLAVE;
		return Constants.OFPCRROLEEQUAL;		
	}

	
	public static double[] parseUptimeResult(String uptimeCmdResult) {
		
		
		Pattern crunchifyUptimePattern = Pattern
				.compile("(.*)load\\s+average:\\s+(.*),\\s+(.*),\\s+(.*)$");
	 
		Matcher m = crunchifyUptimePattern.matcher(uptimeCmdResult);

		if (m.matches()) {
			double oneMinuteLoadAvg = Double.parseDouble(m.group(2).replace(",", "."));
			double fiveMinuteloadAvg = Double.parseDouble(m.group(3).replace(",", "."));
			double fifteenMinuteLoadAvg = Double.parseDouble(m.group(4).replace(",", "."));

			return new double[]{oneMinuteLoadAvg, fiveMinuteloadAvg, fifteenMinuteLoadAvg};
		} else {
			return null;
		}
	}


    public static String ConvertMilliSecondsToFormattedDate(long milliSeconds){
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

	public static double round(double r) {
		// TODO Auto-generated method stub
		return Math.round(r * 100.0) / 100.0;
	}

}