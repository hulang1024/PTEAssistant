package assistant.request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import assistant.domain.Time;
import assistant.parser.AppointmentParser;

public class AppointmentSearcherSession extends BaseSession {
    
    public void continueSession(BaseSession session) {
        this.lastPage = session.lastPage;
        this.wscid = session.wscid;
        this.wsid = session.wsid;
        this.embedClientID = session.embedClientID;
    }
    
    public void selApptTimeFmt(int timeFmt) {
        String params = String.format("&timeFmt=%d", timeFmt);
        lastPage = Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptTimeFmt&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid) + params, true);
    }

    public void selectYearMonth(int year, int month) {
        String params = String.format("&apptMonth=%s/%s", month, year);
        lastPage = Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptMonthAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid) + params, true);
    }
    
    public void chooseTestCenter(Map<String, Object> testCenter) {
        String params = "&apptTestCenterID=" + testCenter.get("centerID");
        Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptCenterAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid) + params, false);
    }
    
    public boolean selectDate(int year, int month, int dayOfMonth) {
        String params = String.format("&apptDate=%d/%d/%d", month, dayOfMonth, year);
        lastPage = Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptDateAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid) + params, true);
        return true;                                                     
    }

    public Map<Integer, String> getDateStates() {
        return AppointmentParser.parseDateStatesMap(lastPage);
    }
    
    /**
     * 注:同一天的24小时制的
     * @return
     */
    public List<Time> getSortedAvailableAppointmentTimes() {
        List<Time> times = new ArrayList<Time>();
        for(Calendar cal : AppointmentParser.parseAvailableAppointmentTimes(lastPage)) {
            times.add(new Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        }
        return times;
    }
    
    public List<Map<String, Object>> getAppointmentTestCenters() {
        return AppointmentParser.parseAppointmentTestCenters(lastPage);
    }
}
