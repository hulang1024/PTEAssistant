package assistant.process;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import assistant.UserLogger;
import assistant.domain.UserSetting;
import assistant.request.SeatSearcherSession;

public class SeatSearcher {
    private SeatSearcherSession seatSearcherSession = new SeatSearcherSession();
    private UserSetting userSetting;
    private SearchResult result = new SearchResult();
    private UserLogger userLogger;
    
    public SeatSearcher(UserSetting userSetting, UserLogger userLogger) {
        this.userSetting = userSetting;
        this.userLogger = userLogger;
    }

    public SearchResult search() {
        result.code = 1;
        boolean succeed;
        userLogger.log("开始搜索");
        succeed = seatSearcherSession.startSeatSearch();
        if(!succeed) {
            userLogger.log("开始搜索失败!");
            return result;
        }
        List<Map<String, Object>> exams = seatSearcherSession.getExams();
        userLogger.log("搜索到考试列表:%s", exams);
        Map<String, Object> selectedExam = null;
        for(Map<String, Object> exam : exams) {
            if(userSetting.examCode.equals(exam.get("code"))) {
                selectedExam = exam;
                break;
            }
        }
        result.exam = selectedExam;
        userLogger.log("选择考试代码=%s的考试", userSetting.examCode);
        seatSearcherSession.selectExam(selectedExam);
        userLogger.log("完成考试语言选择动作");
        seatSearcherSession.doneExamLangAction();
        userLogger.log("搜索测试中心根据条件:%s", userSetting.testCentersCriteria);
        seatSearcherSession.searchTestCentersByCriteria(userSetting.testCentersCriteria);
        List<Map<String, Object>> testCenters = seatSearcherSession.getSearchTestCenters();
        userLogger.log("搜索测试中心结果,共%d个:%s", testCenters.size(), testCenters);
        int selectTopNum = testCenters.size() < 4 ? testCenters.size() : 4;
        testCenters = testCenters.subList(0, selectTopNum);
        userLogger.log("选择结果中前%d个", selectTopNum);
        succeed = seatSearcherSession.selectTestCenters(userSetting.testCentersCriteria, testCenters, selectTopNum);
        if(!succeed) {
            userLogger.log("搜索测试中心失败!");
            return result;
        }
        userLogger.log("完成选择测试中心动作");
        succeed = seatSearcherSession.doneTestCenterAction();
        if(!succeed) {
            userLogger.log("完成测试中心动作 失败!");
            return result;
        }
        userLogger.log("开始搜索约会");
        new AppointmentSearcher(userSetting, seatSearcherSession, testCenters, result, userLogger).search();
        return result;
    }
    public static class SearchResult {
        /**
         * 0=成功
         * 1=失败
         */
        public int code = 1;
        public Map<String, Object> exam;
        public Calendar apptTime;
        public Map<String, Object> testCenter;
    }
}
