package assistant.process;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import assistant.ConfigManager;
import assistant.domain.UserSetting;
import assistant.process.SeatSearcher.SearchResult;
import assistant.utils.CalendarUtils;

public class EamilReporter {
    private ConfigManager config = new ConfigManager();
    
    public EamilReporter() {
    }
    
    public void report(UserSetting userSetting, SearchResult searchResult, int resultState) {
        if(! (boolean)config.get("enableMailReport"))
            return;
        
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.user", config.get("fromEmailUser"));
        props.put("mail.password", config.get("fromEmailPassword"));

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mail.user"), props.getProperty("mail.password"));
            }
        };
        Session mailSession = Session.getInstance(props, authenticator);
        try {
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(props.getProperty("mail.user")));
        message.setRecipient(RecipientType.TO, new InternetAddress(config.get("toEmailUser").toString()));
        message.setSubject("PTE助手通知");

        String content = String.format("账号 %s 已搜索到可用约会: 时间=%s, 地点=%s",
                userSetting.user.username, CalendarUtils.chinese(searchResult.apptTime), searchResult.testCenter);
        if(resultState > -1) {
            content += "<br>";
            content += resultState == 1 ? "并报名成功" : "但报名失败";
        }
        message.setContent(content, "text/html;charset=UTF-8");
        
        Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
