package assistant.request;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

import assistant.parser.AppointmentParser;

public class SeatSearcherSession extends BaseSession {
    
    public boolean startSeatSearch() {
        lastPage = Request.get("http://pearsonvue.com/Dispatcher?application=SeatSearch&action=actStartApp&v=W2L&clientCode=PEARSONLANGUAGE", true);
        if(lastPage == null)
            return false;
        Matcher m1 = Pattern.compile("src=\"(\\S+application=RegSched\\S+)\"").matcher(lastPage);
        if(m1.find()) {
            String src1 = "http://pearsonvue.com" + m1.group(1);

            Matcher m = Pattern.compile("wscid=(\\d+).*embedClientID=(\\d+).*wsid=(\\d+)").matcher(src1);
            m.find();
            wscid = m.group(1);
            embedClientID = m.group(2);
            wsid = m.group(3);
            
            lastPage = Request.get(src1, true);
            Matcher m2 = Pattern.compile("SRC=\"(\\S+layer=SelExamPage\\S+)\"").matcher(lastPage);
            if(m2.find()) {
                String src2 = "http://pearsonvue.com" + m2.group(1);
                lastPage = Request.get(src2, true);
                return true;
            }
        }
        return false;
    }
    
    public void selectExam(Map<String, Object> exam) {
        Long examSeriesID = (Long) exam.get("examSeriesID");
        Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&examSeriesID=%d&wscid=%s&layer=SelExamPage&action=selExamAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s&#ES%s&examSeriesID=%d",
            examSeriesID, wscid, embedClientID, wsid, examSeriesID, examSeriesID), false);
    }
    
    public void doneExamLangAction() {
        Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelExamPage&action=doneExamLangAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s",
                wscid, embedClientID, wsid), false);
    }
    
    public boolean searchTestCentersByCriteria(Map<String, Object> params) {
        String url = String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=searchTestCenters&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid);
        lastPage = Request.get(url, params, true);
        return true;
    }

    public boolean selectTestCenters(Map<String, Object> searchParams, List<Map<String, Object>> searchTestCenters, int selectTopNum) {
        for(int ti = 0; ti < selectTopNum; ti++) {
            Long centerID = (Long) searchTestCenters.get(ti).get("centerID");
            String url = String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=selTestCenterAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s&testCenterID=%s",
                    wscid, embedClientID, wsid, centerID);
            Request.get(url, searchParams, false);
        }
        return true;
    }
    
    public boolean doneTestCenterAction() {
        Request.get(String.format("http://pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=doneTestCenterAction&bfp=top.SeatSearchPageFrame.RegSchedPageFrame&embedClientID=%s&bfpapp=top.SeatSearchPageFrame&embed=continue&wsid=%s", wscid, embedClientID, wsid),
                false);
        return true;
    }
    
    public List<Map<String, Object>> getSearchTestCenters() {
        return AppointmentParser.parseSearchTestCenters(lastPage);
    }
    
    public List<Map<String, Object>> getExams() {
        return AppointmentParser.parseExams(lastPage);
    }
}
