package assistant.utils;

import java.util.Calendar;

public class CalendarUtils {
    
    public static String chinese(Calendar calendar) {
        return String.format("%d年%d月%d日星期%s %s%02d:%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE),
                new String[]{"天","一","二","三","四","五","六"}[calendar.get(Calendar.DAY_OF_WEEK) - 1],
                (new String[]{"上午", "下午"}[calendar.get(Calendar.AM_PM)]),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }
	
    public static String formatYMD(Calendar calendar) {
        return String.format("%d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));
    }
    
    public static String formatHM(Calendar calendar) {
        return String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }
    
	public static String formatYMDHM(Calendar calendar) {
	    return String.format("%d-%02d-%02d %02d:%02d",
	            calendar.get(Calendar.YEAR),
	            calendar.get(Calendar.MONTH) + 1,
	            calendar.get(Calendar.DATE),
	            calendar.get(Calendar.HOUR_OF_DAY),
	            calendar.get(Calendar.MINUTE));
	}
	
    public static String formatYMDHMS(Calendar calendar) {
        return String.format("%d-%02d-%02d %02d:%02d:%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.MILLISECOND));
    }
}
