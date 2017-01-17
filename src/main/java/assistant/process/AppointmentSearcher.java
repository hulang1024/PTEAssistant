package assistant.process;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import assistant.UserLogger;
import assistant.domain.Time;
import assistant.domain.UserSetting;
import assistant.process.SeatSearcher.SearchResult;
import assistant.request.AppointmentSearcherSession;
import assistant.request.SeatSearcherSession;
import assistant.utils.CalendarUtils;

public class AppointmentSearcher {
    private AppointmentSearcherSession appointmentSearcherSession = new AppointmentSearcherSession();
    private UserSetting userSetting;
    private SearchResult result;
    private List<Map<String, Object>> searchTestCenters;
    private UserLogger userLogger;
    private Calendar startCalendar;
    private Calendar endCalendar;
    
    
    private class AvailableAppointment {
        public Map<String, Object> testCenter;
        /**
         * 当包含数据时，是已经有序的从小到大。对于时间来说，在前面就是最早的。原理为HashCode
         * 结构:年月 => 一组可用日, 每个可用日 => 一组可用时间
         */
        public Map<Calendar, Map<Integer, List<Time>>> timeMap = new HashMap<Calendar, Map<Integer, List<Time>>>();
    }
    
    
    public AppointmentSearcher(UserSetting userSetting, SeatSearcherSession seatSearcherRequest, List<Map<String, Object>> testCenters, SearchResult result, UserLogger userLogger) {
        this.userSetting = userSetting;
        this.result = result;
        this.searchTestCenters = testCenters;
        this.userLogger = userLogger;
        appointmentSearcherSession.continueSession(seatSearcherRequest);
        startCalendar = userSetting.apptSearchRule.startCalendar;
        endCalendar = userSetting.apptSearchRule.endCalendar;
    }
    
    public void search() {
        int loopTimes = 1;
        boolean searchSucceed = false;
        try {
        List<Map<String, Object>> apptTestCenters = searchTestCenters;//appointmentSearcherSession.getAppointmentTestCenters();
        userLogger.log("获取测试中心:%s", apptTestCenters);
        while(!Thread.currentThread().isInterrupted() && !searchSucceed) {
            userLogger.log("开始第%d轮约会时间搜索", loopTimes);
            List<AvailableAppointment> availableAppointmentList = new ArrayList<AvailableAppointment>();
            for(int testCenterIndex = 0, size = apptTestCenters.size(); testCenterIndex < size; testCenterIndex++) {
                Map<String, Object> testCenter = apptTestCenters.get(testCenterIndex);
                userLogger.log("选择第%d个测试中心:%s", testCenterIndex+1, testCenter);
                appointmentSearcherSession.chooseTestCenter(testCenter);
                Thread.sleep(userSetting.loopRequestIntervalMS);
                
                AvailableAppointment availableAppointment = new AvailableAppointment();
                availableAppointment.testCenter = testCenter;
                availableAppointmentList.add(availableAppointment);
                
                int startYear = startCalendar.get(Calendar.YEAR);
                int startMonth = startCalendar.get(Calendar.MONTH) + 1;
                int startDate = startCalendar.get(Calendar.DAY_OF_MONTH);
                
                switch(userSetting.apptSearchRule.type / 10) {
                case 2: {
                    int endYear = endCalendar.get(Calendar.YEAR);
                    int endMonth = endCalendar.get(Calendar.MONTH) + 1;
                    int endDate = endCalendar.get(Calendar.DAY_OF_MONTH);
                    int searchYear = startYear;
                    int searchMonth = startMonth;
                    int searchStartDate = startDate;
                    int searchEndDate = 31;
                    Calendar dayMaxGetter = new GregorianCalendar();
                    boolean over = false;
                    while(!Thread.currentThread().isInterrupted() && !searchSucceed && !over) {
                        if(searchYear >= endYear && searchMonth >= endMonth) {
                            searchEndDate = endDate;
                            over = true;
                        } else {
                            dayMaxGetter.set(searchYear, searchMonth-1, 1);
                            searchEndDate = dayMaxGetter.getActualMaximum(Calendar.DAY_OF_MONTH);
                        }
                        
                        userLogger.log("选择%d年%d月", searchYear, searchMonth);
                        appointmentSearcherSession.selectYearMonth(searchYear, searchMonth);
                        Calendar yearmonth = Calendar.getInstance();
                        yearmonth.clear();
                        yearmonth.set(Calendar.YEAR, searchYear);
                        yearmonth.set(Calendar.MONTH, searchMonth-1);
                        availableAppointment.timeMap.put(yearmonth, new HashMap<Integer, List<Time>>());
                                                Thread.sleep(userSetting.loopRequestIntervalMS);
                        Map<Integer, String> cacls = appointmentSearcherSession.getDateStates();
                        userLogger.log("获取%d年%d月的日期状态=%s", searchYear, searchMonth, cacls);
                        while(searchStartDate <= searchEndDate) {
                            if("open".equals(cacls.get(searchStartDate))) {
                                int date = searchStartDate;
                                userLogger.log("选择%d年%d月%d日", searchYear, searchMonth, date);
                                appointmentSearcherSession.selectDate(searchYear, searchMonth, date);
                                List<Time> times = appointmentSearcherSession.getSortedAvailableAppointmentTimes();
                                availableAppointment.timeMap.get(yearmonth).put(date, times);
                                if(!times.isEmpty()) {
                                    over = true;
                                    break;
                                }
                            }
                            searchStartDate++;
                        }
                        
                        if(searchYear <= endYear) {
                            searchMonth++;
                        }
                        if(searchMonth > 12) {
                            searchMonth = 1;
                            searchYear++;
                        }
                        searchStartDate = 1;
                    }
                    break;
                }
                    
                case 1: {
                    Calendar yearmonth = Calendar.getInstance();
                    yearmonth.clear();
                    yearmonth.set(Calendar.YEAR, startYear);
                    yearmonth.set(Calendar.MONTH, startMonth-1);
                    availableAppointment.timeMap.put(yearmonth, new HashMap<Integer, List<Time>>());
                    
                    userLogger.log("选择%d年%d月", startYear, startMonth);
                    appointmentSearcherSession.selectYearMonth(startYear, startMonth);
                    Thread.sleep(userSetting.loopRequestIntervalMS);
                    userLogger.log("选择%d年%d月%d日", startYear, startMonth, startDate);
                    appointmentSearcherSession.selectDate(startYear, startMonth, startDate);
                    availableAppointment.timeMap.get(yearmonth).put(startDate, appointmentSearcherSession.getSortedAvailableAppointmentTimes());
                    break;
                }
                
                }
            }// Foreach Test centers end
            // ，查找
            userLogger.log("以时间优先原则排序测试中心");
            switch(userSetting.apptSearchRule.type / 10){
            case 2:
                searchSucceed = searchRangeAppointment(availableAppointmentList);
                break;
            case 1:
                searchSucceed = searchFixedAppointment(availableAppointmentList);
                break;
            }
            if(!searchSucceed) {
                userLogger.log("第%d轮约会时间搜索结束", loopTimes++);
                Thread.sleep(userSetting.loopRequestIntervalMS);
            }
        }
        
        } catch (InterruptedException e) {
        }
    }
    
    private void saveAsSearchResult(Calendar appTime, Map<String, Object> testCenter) {
        result.code = 0;
        result.apptTime = appTime;
        result.testCenter = testCenter;
    }
    
    private boolean searchRangeAppointment(List<AvailableAppointment> availableAppointmentList) {
        if(availableAppointmentList.isEmpty())
            return false;
        Time minTime = new Time(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE));
        Time maxTime = new Time(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE));
        if(minTime.getTime() + maxTime.getTime() == 24 * 60 * 2) {
            return searchNormalEarliestAppointment(availableAppointmentList);
        }
        boolean searchSucceed = searchRangeEarliestAppointment(availableAppointmentList);
        if(searchSucceed) {
            return true;
        } else if(userSetting.apptSearchRule.type % 10 == 1) {
            return searchNormalEarliestAppointment(availableAppointmentList);
        } else {
            return false;
        }
    }
    
    private boolean searchFixedAppointment(List<AvailableAppointment> availableAppointmentList) {
        if(availableAppointmentList.isEmpty())
            return false;
        if(userSetting.apptSearchRule.type % 10 == 1)
            return searchNormalEarliestAppointment(availableAppointmentList);
        else return false;
    }
    
    private boolean searchNormalEarliestAppointment(List<AvailableAppointment> availableAppointmentList) {
        AvailableAppointment earliestAvappt = null;
        Calendar minCalendar = null;
        for(AvailableAppointment avappt : availableAppointmentList) {
            Entry<Calendar, Map<Integer, List<Time>>> earliestYearMonthEntry = avappt.timeMap.entrySet().iterator().next();
            Calendar earliestYearMonth = earliestYearMonthEntry.getKey();
            if(!earliestYearMonthEntry.getValue().entrySet().isEmpty()) {
                Entry<Integer, List<Time>> earliestDateEntry = earliestYearMonthEntry.getValue().entrySet().iterator().next();
                int earliestDate = earliestDateEntry.getKey();
                if(!earliestDateEntry.getValue().isEmpty()) {
                    Time earliestTime = earliestDateEntry.getValue().get(0);
                    Calendar cal = Calendar.getInstance();
                    cal.set(earliestYearMonth.get(Calendar.YEAR), earliestYearMonth.get(Calendar.MONTH), earliestDate,
                            earliestTime.getHour(), earliestTime.getMinute());
                    
                    if(minCalendar == null || cal.compareTo(minCalendar) < 0) {
                        minCalendar = cal;
                        earliestAvappt = avappt;
                    }
                }
            }
        }
        if(minCalendar != null) {
            saveAsSearchResult(minCalendar, earliestAvappt.testCenter);
            return true;
        } else {
            return false;
        }
    }
    
    private boolean searchRangeEarliestAppointment(List<AvailableAppointment> availableAppointmentList) {
        AvailableAppointment earliestAvappt = null;
        Calendar minCalendar = null;
        Time minTime = new Time(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE));
        Time maxTime = new Time(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE));
        for(AvailableAppointment avappt : availableAppointmentList) {
            Entry<Calendar, Map<Integer, List<Time>>> earliestYearMonthEntry = avappt.timeMap.entrySet().iterator().next();
            Calendar earliestYearMonth = earliestYearMonthEntry.getKey();
            if(!earliestYearMonthEntry.getValue().entrySet().isEmpty()) {
                Entry<Integer, List<Time>> earliestDateEntry = earliestYearMonthEntry.getValue().entrySet().iterator().next();
                int earliestDate = earliestDateEntry.getKey();
                if(!earliestDateEntry.getValue().isEmpty()) {
                    Time earliestTime = null;
                    for(Time time : earliestDateEntry.getValue()) {
                        if(minTime.compareTo(time)<1 && time.compareTo(maxTime)<1) {
                            earliestTime = time;
                            break;
                        }
                    }
                    if(earliestTime != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(earliestYearMonth.get(Calendar.YEAR), earliestYearMonth.get(Calendar.MONTH), earliestDate,
                                earliestTime.getHour(), earliestTime.getMinute());
                        if(minCalendar == null || cal.compareTo(minCalendar) < 0) {
                            minCalendar = cal;
                            earliestAvappt = avappt;
                        }
                    }
                }
            }
        }
        if(minCalendar != null) {
            saveAsSearchResult(minCalendar, earliestAvappt.testCenter);
            return true;
        } else {
            return false;
        }
    }
}
