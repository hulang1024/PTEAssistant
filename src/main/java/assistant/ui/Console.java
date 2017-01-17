package assistant.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import assistant.utils.CalendarUtils;

public class Console extends JPanel {
    private JTextArea textarea;
    private JScrollPane scrollPane;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Console() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("控制台"));
        textarea = new JTextArea();
        textarea.setCaretPosition(textarea.getText().length());
        textarea.setFont(new Font("新宋体", Font.PLAIN, 12));
        add(textarea);
        DefaultCaret caret = (DefaultCaret) textarea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(textarea);
        add(scrollPane, BorderLayout.CENTER);
        
        welcome();
    }

    public void welcome() {
        log("欢迎使用, 现在是%s（系统时间） ", CalendarUtils.chinese(Calendar.getInstance()));
    }
    
    public void log(String format, Object... args) {
        log(String.format(format, args));
    }
    
    public void log(String s) {
        String time = dateFormat.format(new Date());
        textarea.append(time + ": " + s + "\n");
    }
    
    public void clear() {
        textarea.setText("");
    }
}
