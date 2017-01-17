package assistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import assistant.domain.User;
import assistant.ui.UserConsole;

public class UserLogger {
    private User user;
    public static final DateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private UserConsole userConsole;
    
    public UserLogger(User user) {
        this.user = user;
        this.userConsole = new UserConsole(user);
        
    }
    
    public void log(String format, Object... args) {
        appendToUserConsole(format, args);
        File logDir = new File("assistant.logs/users/uid" + user.uid);
        if(!logDir.exists())
            logDir.mkdirs();
        Date nowDate = new Date();
        File logFile = new File(logDir, fileNameDateFormat.format(nowDate) + ".log");
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(String.format("%s: %s\r\n", timeFormat.format(nowDate), String.format(format, args)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToUserConsole(String format, Object... args) {
        userConsole.log(format, args);
    }
}
