package assistant.ui;

import assistant.domain.User;

public class UserConsole {
    private User user;

    public UserConsole(User user) {
        this.user = user;
    }
    
    public void log(String format, Object... args) {
        MainFrame.console.log(String.format("uid=%d: %s", user.uid, format), args);
    }
}
