package assistant.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import assistant.UserLogger;
import assistant.domain.UserSetting;
import assistant.process.SeatSearcher.SearchResult;
import assistant.utils.CalendarUtils;

public class UserSettingRunnable implements Runnable {
    private UserSetting userSetting;
    private UserLogger userLogger;
    private Logger log = LogManager.getFormatterLogger(this.getClass());
    private EamilReporter eamilReporter = new EamilReporter();
    
    public UserSettingRunnable(UserSetting userSetting) {
        this.userSetting = new UserSettingGenerator(userSetting).generate();
        this.userLogger = new UserLogger(userSetting.user);
    }
    
    public void run() {
        log.trace("run开始");
        SeatSearcher seatSearcher = new SeatSearcher(userSetting, userLogger);
        SearchResult searchResult = seatSearcher.search();
        if(searchResult.code == 0) {
            int resultState = -1;
            userLogger.log("已搜索到可用约会: 时间=%s, 地点=%s",
                    CalendarUtils.chinese(searchResult.apptTime), searchResult.testCenter);
            if(!userSetting.searchSeatOnly) {
                userLogger.log("开始自动报名");
                resultState = new LoginToPayment(userSetting, searchResult, userLogger).toPayment() ? 1 : 0;
            } else {
                userLogger.log("检查到已设置仅搜索，因此结束");
            }
            eamilReporter.report(userSetting, searchResult, resultState);
        }

        log.trace("run结束");
    }
}
