package assistant.process;

import java.util.Calendar;

import assistant.UserLogger;
import assistant.domain.UserSetting;
import assistant.process.SeatSearcher.SearchResult;
import assistant.request.LoginToPaymentSession;
import assistant.utils.CalendarUtils;

public class LoginToPayment {
    private UserLogger userLogger;
    private UserSetting userSetting;
    private SearchResult seatSearchResult;
    private LoginToPaymentSession session = new LoginToPaymentSession();
    
    public LoginToPayment(UserSetting userSetting, SearchResult searchResult, UserLogger userLogger) {
        this.userSetting = userSetting;
        this.seatSearchResult = searchResult;
        this.userLogger = userLogger;
    }
    
    public boolean toPayment() {
        if(Thread.currentThread().isInterrupted()) return false;

        boolean succeed;
        userLogger.log("登陆账号:%s", userSetting.user.username);
        succeed = session.login(userSetting.user);
        if(!succeed) {
            userLogger.log("登录失败!");
            return false;
        }
        userLogger.log("选择考试:%s", seatSearchResult.exam);
        session.selectExam(seatSearchResult.exam);
        userLogger.log("完成考试语言选择动作");
        session.doneExamLangAction();
        userLogger.log("搜索测试中心根据条件:%s", userSetting.testCentersCriteria);
        session.searchTestCentersByCriteria(userSetting.testCentersCriteria);
        userLogger.log("选择测试中心:%s", seatSearchResult.testCenter);
        session.selectTestCenter(userSetting.testCentersCriteria, seatSearchResult.testCenter);
        userLogger.log("完成测试选择动作");
        session.doneTestCenterAction();
        int year = seatSearchResult.apptTime.get(Calendar.YEAR);
        int month = seatSearchResult.apptTime.get(Calendar.MONTH)+1;
        int date = seatSearchResult.apptTime.get(Calendar.DAY_OF_MONTH);
        userLogger.log("选择%d年%d月", year, month);
        session.selectYearMonth(year, month);
        userLogger.log("选择%d年%d月%d日", year, month, date);
        session.selectDate(year, month, date);
        userLogger.log("选择约会时间:%s", CalendarUtils.formatYMDHM(seatSearchResult.apptTime));
        succeed = session.selectAppointmentTime(seatSearchResult.apptTime);
        if(!succeed) {
            userLogger.log("选择约会时间失败!");
            return false;
        }
        userLogger.log("完成约会选择动作");
        session.doneApptAction();
        userLogger.log("完成问题回答使用答案:%s", userSetting.answers);
        session.doneReviewQuestions(userSetting.answers);
        
        if(Thread.currentThread().isInterrupted()) return false;
        
        if(userSetting.applyType == 1) {
            userLogger.log("开始直接付款方式");
            session.doneReviewOrder();
            userLogger.log("完成支付使用信用卡信息:%s", userSetting.creditCard);
            session.donePayment(userSetting.creditCard);
        }
        else if(userSetting.applyType == 2) {
            userLogger.log("开始VoucherCode支付方式");
            userLogger.log("完成支付使用VoucherCode:%s", userSetting.paramVoucherNumber);
        	session.applyVoucher(userSetting.paramVoucherNumber);
        	session.doneReviewOrder();
        	userLogger.log("最后完成确认订单");
        	session.confirmOrder();
        }
        userLogger.log("***已完成报名!***");
        //userLogger.log(session.lastPage.replace("%", "%%"));
        return true;
    }
}
