package assistant.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import assistant.db.UserSettingDao;
import assistant.domain.UserSetting;
import assistant.ui.MainFrame;

public class Assistant {
    private UserSettingDao userSettingDao = new UserSettingDao();
    private Map<Long, Thread> userThreadMap = new HashMap<Long, Thread>();
    
    public void start(List<Long> uids) {
        for(Long uid : uids) {
            UserSetting us = userSettingDao.getUserSettingByUid(uid);
            Thread thread = userThreadMap.get(uid);
            if(thread == null || !thread.isAlive()) {
                thread = new Thread(new UserSettingRunnable(us), "uid=" + us.user.uid);
                userThreadMap.put(us.user.uid, thread);
                thread.start();
                MainFrame.console.log("开始线程%s", thread.getName());
            } else {
                MainFrame.console.log("%s正在运行", thread.getName());
            }
        }
    }
    
    public void stopAll() {
        MainFrame.console.log("已接受中止请求,请等待线程中断");
        for(Entry<Long, Thread> entry : userThreadMap.entrySet()) {
            Thread t = entry.getValue();
            t.interrupt();
            MainFrame.console.log("线程%s已中止", t.getName());
        }
    }
    
    public void stop(Long uid) {
        MainFrame.console.log("已接受中止请求,请等待线程中断");
        Thread t = userThreadMap.get(uid);
        if(t != null) {
            t.interrupt();
            MainFrame.console.log("线程%s已中止", t.getName());
        }
    }
}
