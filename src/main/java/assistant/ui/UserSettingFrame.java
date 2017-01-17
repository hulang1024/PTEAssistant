package assistant.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;

import com.qt.datapicker.DatePicker;

import assistant.db.LocationDao;
import assistant.domain.Location;
import assistant.domain.Option;
import assistant.domain.Time;
import assistant.domain.User;
import assistant.domain.UserSetting;
import assistant.domain.UserSetting.SearchApptRule;
import assistant.utils.CalendarUtils;


public class UserSettingFrame extends JFrame
    implements ActionListener, ChangeListener, ItemListener{

    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JPanel panelTestCenter;
    private JPanel panelAppt;
    private JPanel panelExam;
    private JPanel panelAccount;
    private JPanel panelApptTime;
    private JPanel panelPayment;
    private JPanel panelCreditCard;
    private JPanel panelOther;
    private JComboBox comboCountry;
    private JComboBox comboState;
    private JTextField txtCity;
    private JTextField txtExamCode;
    private DateObserverTextField fieldEndDate;
    private DateObserverTextField fieldStartDate;
    private JTextField txtUsername;
    private JPasswordField pwdPassword;
    private JTextField txtUid;
    private JList listTimeRange;
    private JButton btnOk;
    private JButton btnCancel;
    private JButton btnNext;
    private JCheckBox chkEchoPwdPlain;
    private JTextField txtPlain;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JRadioButton radiobtnRange;
    private JRadioButton radiobtnFixed;
    private JButton btnOpenStartDatePicker;
    private JButton btnOpenEndDatePicker;
    private JLabel lblState;
    private JLabel lblCountry;
    private JLabel lblCity;
    private JLabel lblStartDate;
    private JLabel lblEndDate;
    private JCheckBox chkEarliest;
    private JTextField txtTime;
    private JLabel lblTime;
    
    private final String timeSeparatorPattern = "([-\\/._=~:]|\\s+|[a-zA-Z]+)";
    private final String datePattern = "^(\\d{2}|\\d{4})" + timeSeparatorPattern + "\\d{1,2}" + timeSeparatorPattern + "\\d{1,2}$";
    private final String timePattern = "\\d{1,2}" + timeSeparatorPattern + "\\d{1,2}$";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); 
    private final Time[][] timeRanges = {
        {new Time(8,00), new Time(11,59)},
        {new Time(12,00), new Time(17,59)},
        {new Time(18,00), new Time(00,00)}
    };
    private LocationDao locationDao = new LocationDao();

    private String mode;
    
    public UserTableModel userTableModel;
    private JRadioButton radiobtnMoney;
    private JRadioButton radiobtnParamVoucherNumber;
    private JTextField txtParamVoucherNumber;
    private JTextField txtCardNumber;
    private JTextField txtCardHoldersName;
    private JComboBox comboCardExpMonth;
    private JComboBox comboCardExpYear;
    private JTextField txtCardSecCode;
    private JComboBox comboPaymentType;
    private JCheckBox chkSearchSeatOnly;
    private JTextField txtLoopRequestIntervalMS;
    
    private class DateObserverTextField extends JTextField implements Observer {
        public void update(Observable o, Object arg) {
            Calendar calendar = (Calendar) arg;
            DatePicker dp = (DatePicker) o;
            setText(dp.formatDate(calendar, dateFormat.toPattern()));
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UserSettingFrame frame = new UserSettingFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public UserSettingFrame() {
        try {
            URL url = getClass().getResource("/res/images/ico.jpg");
            if (url != null) {
                setIconImage(new ImageIcon(url).getImage());
            }
        } catch (Exception e) {}
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(this);
        
        txtUid = new JTextField();
        txtUid.setVisible(false);
        
        JLayeredPane layeredPaneBtns = new JLayeredPane();
        layeredPaneBtns.setLocation(170, 320);
        layeredPaneBtns.setSize(260, 40);
        getContentPane().add(layeredPaneBtns);
        
        btnOk = new JButton("保存");
        btnOk.addActionListener(this);
        btnOk.setActionCommand("btnOk");
        btnOk.setBounds(98, 0, 76, 37);
        layeredPaneBtns.add(btnOk);
        
        btnCancel = new JButton("取消");
        btnCancel.addActionListener(this);
        btnCancel.setActionCommand("btnCancel");
        btnCancel.setBounds(184, 0, 76, 37);
        layeredPaneBtns.add(btnCancel);
        
        btnNext = new JButton(">>");
        btnNext.addActionListener(this);
        btnNext.setActionCommand("btnNext");
        btnNext.setBounds(12, 0, 76, 37);
        layeredPaneBtns.add(btnNext);
        
        panelAccount = new JPanel();
        tabbedPane.addTab("帐号", null, panelAccount, "帐号");
        panelAccount.setLayout(null);
        
        lblUsername = new JLabel("账号");
        lblUsername.setBounds(78, 63, 39, 15);
        panelAccount.add(lblUsername);
        lblPassword = new JLabel("密码");
        lblPassword.setBounds(78, 101, 39, 15);
        panelAccount.add(lblPassword);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(117, 60, 139, 21);
        panelAccount.add(txtUsername);
        txtUsername.setColumns(10);
        
        pwdPassword = new JPasswordField();
        pwdPassword.setEchoChar('*');
        pwdPassword.setBounds(117, 98, 139, 21);
        pwdPassword.addCaretListener(new CaretListener(){
            @Override
            public void caretUpdate(CaretEvent e) {
                boolean echoPlain = chkEchoPwdPlain.isSelected();
                if(echoPlain) {
                    txtPlain.setText(new String(pwdPassword.getPassword()));
                }
                if(txtPlain.isVisible() != echoPlain)
                    txtPlain.setVisible(echoPlain);
            }
        });
        panelAccount.add(pwdPassword);
        
        chkEchoPwdPlain = new JCheckBox("显示");
        chkEchoPwdPlain.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                txtPlain.setVisible(chkEchoPwdPlain.isSelected());
                txtPlain.setText(new String(pwdPassword.getPassword()));
            }
        });
        chkEchoPwdPlain.setBounds(258, 97, 61, 23);
        panelAccount.add(chkEchoPwdPlain);
        
        txtPlain = new JTextField();
        txtPlain.setVisible(false);
        txtPlain.setEditable(false);
        txtPlain.setBounds(117, 120, 139, 21);
        panelAccount.add(txtPlain);
        
        panelExam = new JPanel();
        tabbedPane.addTab("考试", null, panelExam, "考试");
        panelExam.setLayout(null);
        
        JLabel lblExamCode = new JLabel("考试代码");
        lblExamCode.setBounds(53, 43, 54, 15);
        panelExam.add(lblExamCode);
        
        txtExamCode = new JTextField();
        txtExamCode.setText("PTE-A");
        txtExamCode.setBounds(115, 40, 97, 21);
        panelExam.add(txtExamCode);
        
        txtExamCode.setColumns(10);
        panelTestCenter = new JPanel();
        tabbedPane.addTab("测试中心", null, panelTestCenter, "设置查找测试中心的搜索条件");
        panelTestCenter.setLayout(null);
        
        lblCountry = new JLabel("国家");
        lblCountry.setBounds(68, 43, 36, 15);
        panelTestCenter.add(lblCountry);
        
        comboCountry = new JComboBox();
        comboCountry.setActionCommand("comboCountryChanged");
        comboCountry.addItemListener(this);
        comboCountry.setBounds(114, 40, 220, 21);
        if(comboCountry.getModel().getSize() == 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement("请选择...");
            for(Location loc : locationDao.queryAllLocationsByPid(0L))
                model.addElement(loc);
            comboCountry.setModel(model);
        }
        panelTestCenter.add(comboCountry);

        lblCity = new JLabel("城市");
        lblCity.setBounds(68, 68, 36, 15);
        panelTestCenter.add(lblCity);
        
        txtCity = new JTextField();
        txtCity.setBounds(114, 65, 220, 21);
        panelTestCenter.add(txtCity);
        txtCity.setColumns(10);
        
        lblState = new JLabel("州/省");
        lblState.setBounds(63, 93, 36, 15);
        panelTestCenter.add(lblState);
        
        comboState = new JComboBox();
        comboState.setActionCommand("comboStateChanged");
        comboState.setBounds(114, 90, 220, 21);
        panelTestCenter.add(comboState);
        
        panelAppt = new JPanel();
        panelAppt.setLayout(null);
        tabbedPane.addTab("约会", null, panelAppt, "可用约会日期搜索规则");
        
        radiobtnRange = new JRadioButton("范围");
        radiobtnRange.setActionCommand("radiobtnRangeChange");
        radiobtnRange.setBounds(50, 6, 60, 23);
        radiobtnRange.addChangeListener(this);
        panelAppt.add(radiobtnRange);
        radiobtnFixed = new JRadioButton("固定");
        radiobtnFixed.setBounds(112, 6, 60, 23);
        radiobtnFixed.addChangeListener(this);
        panelAppt.add(radiobtnFixed);
        
        ButtonGroup btngroup1 = new ButtonGroup();
        btngroup1.add(radiobtnRange);
        btngroup1.add(radiobtnFixed);
        
        panelApptTime = new JPanel();
        panelApptTime.setBounds(50, 125, 319, 142);
        panelAppt.add(panelApptTime);
        panelApptTime.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u65F6\u95F4", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.activeCaption));
        panelApptTime.setLayout(null);
        
        listTimeRange = new JList();
        listTimeRange.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTimeRange.setBounds(27, 24, 212, 85);
        panelApptTime.add(listTimeRange);
        listTimeRange.setModel(new AbstractListModel() {
            String[] values = new String[] {
                "最早的第一个时间点",
                "最早的上午（08:00 ~ 11:59）",
                "最早的下午（12:00 ~ 17:59）",
                "最早的晚上（18:00 ~ 00:00）"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });

        
        lblTime = new JLabel("时间");
        lblTime.setBounds(27, 25, 54, 15);
        lblTime.setVisible(false);
        panelApptTime.add(lblTime);
        
        chkEarliest = new JCheckBox("如果没有合适时间，选择最早的时间点");
        chkEarliest.setBounds(24,110, 269, 23);
        panelApptTime.add(chkEarliest);
        
        txtTime = new JTextField();
        txtTime.setBounds(88, 22, 120, 21);
        txtTime.setVisible(false);
        panelApptTime.add(txtTime);
        txtTime.setColumns(10);
        
        JPanel panelApptDate = new JPanel();
        panelApptDate.setBounds(50, 35, 320, 85);
        panelAppt.add(panelApptDate);
        panelApptDate.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u65E5\u671F", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "\u65E5\u671F", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.activeCaption));
        
        lblStartDate = new JLabel("开始日期");
        
        lblEndDate = new JLabel("结束日期");
        
        fieldStartDate = new DateObserverTextField();
        fieldStartDate.setToolTipText("日期格式为" + dateFormat.toPattern());
        fieldEndDate = new DateObserverTextField();
        fieldEndDate.setToolTipText(fieldStartDate.getToolTipText());
        
        btnOpenStartDatePicker = new JButton("日历");
        btnOpenStartDatePicker.setActionCommand("btnOpenStartDatePicker");
        btnOpenStartDatePicker.addActionListener(this);
        
        btnOpenEndDatePicker = new JButton("日历");
        btnOpenEndDatePicker.setActionCommand("btnOpenEndDatePicker");
        btnOpenEndDatePicker.addActionListener(this);
        
        GroupLayout gl_panelApptDate = new GroupLayout(panelApptDate);
        gl_panelApptDate.setHorizontalGroup(
            gl_panelApptDate.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelApptDate.createSequentialGroup()
                    .addGroup(gl_panelApptDate.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelApptDate.createSequentialGroup()
                            .addGap(19)
                            .addComponent(lblStartDate, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panelApptDate.createSequentialGroup()
                            .addGap(20)
                            .addComponent(lblEndDate, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelApptDate.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(gl_panelApptDate.createSequentialGroup()
                            .addComponent(fieldEndDate, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnOpenEndDatePicker, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(gl_panelApptDate.createSequentialGroup()
                            .addComponent(fieldStartDate, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnOpenStartDatePicker, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGap(39))
        );
        gl_panelApptDate.setVerticalGroup(
            gl_panelApptDate.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelApptDate.createSequentialGroup()
                    .addGap(1)
                    .addGroup(gl_panelApptDate.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblStartDate)
                        .addComponent(btnOpenStartDatePicker)
                        .addComponent(fieldStartDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelApptDate.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEndDate)
                        .addComponent(btnOpenEndDatePicker)
                        .addComponent(fieldEndDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(27))
        );
        panelApptDate.setLayout(gl_panelApptDate);
        
        panelPayment = new JPanel();
        panelPayment.setLayout(null);
        tabbedPane.addTab("支付", null, panelPayment, "支付");
        
        radiobtnMoney = new JRadioButton("直接付款");

        radiobtnMoney.setBounds(42, 44, 121, 23);
        panelPayment.add(radiobtnMoney);
        
        radiobtnParamVoucherNumber = new JRadioButton("VoucherCode");
        radiobtnParamVoucherNumber.setBounds(42, 81, 127, 23);
        panelPayment.add(radiobtnParamVoucherNumber);
        
        ButtonGroup btngroup2 = new ButtonGroup();
        btngroup2.add(radiobtnMoney);
        btngroup2.add(radiobtnParamVoucherNumber);
        
        txtParamVoucherNumber = new JTextField();
        txtParamVoucherNumber.setBounds(170, 82, 120, 21);
        panelPayment.add(txtParamVoucherNumber);
        txtParamVoucherNumber.setColumns(10);
        
        panelCreditCard = new JPanel();
        panelCreditCard.setLayout(null);
        tabbedPane.addTab("信用卡", null, panelCreditCard, "信用卡");
        
        JLabel label = new JLabel("类型");
        label.setBounds(52, 48, 54, 15);
        panelCreditCard.add(label);
        
        JList listCardType = new JList();
        listCardType.setModel(new AbstractListModel() {
            String[] values = new String[] {"删掉"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
        listCardType.setBounds(105, 62, 1, 1);
        panelCreditCard.add(listCardType);
        
        comboPaymentType = new JComboBox();
        {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new Option("American Express", 5));
        model.addElement(new Option("JCB", 15));
        model.addElement(new Option("MasterCard", 4));
        model.addElement(new Option("VISA", 3));
        comboPaymentType.setModel(model);
        }
        comboPaymentType.setBounds(115, 45, 155, 21);
        panelCreditCard.add(comboPaymentType);
        
        JLabel label_2 = new JLabel("卡号");
        label_2.setBounds(52, 85, 54, 15);
        panelCreditCard.add(label_2);
        
        txtCardNumber = new JTextField();
        txtCardNumber.setBounds(115, 82, 222, 21);
        panelCreditCard.add(txtCardNumber);
        txtCardNumber.setColumns(10);
        
        JLabel label_3 = new JLabel("姓名");
        label_3.setBounds(52, 122, 54, 15);
        panelCreditCard.add(label_3);
        
        txtCardHoldersName = new JTextField();
        txtCardHoldersName.setBounds(115, 119, 92, 21);
        panelCreditCard.add(txtCardHoldersName);
        txtCardHoldersName.setColumns(10);
        
        JLabel label_4 = new JLabel("有效期");
        label_4.setBounds(52, 164, 60, 15);
        panelCreditCard.add(label_4);
        
        comboCardExpMonth = new JComboBox();
        Integer[] months = new Integer[12];
        for(int i = 0; i < months.length; i++) months[i] = i + 1;
        comboCardExpMonth.setModel(new DefaultComboBoxModel(months));
        comboCardExpMonth.setBounds(115, 161, 46, 21);
        panelCreditCard.add(comboCardExpMonth);
        
        JLabel label_1 = new JLabel("/");
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        label_1.setBounds(162, 164, 16, 15);
        panelCreditCard.add(label_1);
        
        comboCardExpYear = new JComboBox();
        Integer[] years = new Integer[20];
        int startYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = 0; i < years.length; i++) years[i] = startYear + i;
        comboCardExpYear.setModel(new DefaultComboBoxModel(years));

        comboCardExpYear.setBounds(179, 161, 66, 21);
        panelCreditCard.add(comboCardExpYear);
        
        JLabel lblCardscode = new JLabel("安全码");
        lblCardscode.setBounds(52, 201, 54, 15);
        panelCreditCard.add(lblCardscode);
        
        txtCardSecCode = new JTextField();
        txtCardSecCode.setBounds(114, 198, 66, 21);
        panelCreditCard.add(txtCardSecCode);
        txtCardSecCode.setColumns(10);
        
        panelOther = new JPanel();
        panelOther.setLayout(null);
        tabbedPane.addTab("其它", null, panelOther, "其它");
        
        chkSearchSeatOnly = new JCheckBox("仅搜索可用日期,不自动报名");
        chkSearchSeatOnly.setBounds(57, 55, 197, 23);
        panelOther.add(chkSearchSeatOnly);
        
        JLabel label_5 = new JLabel("搜索循环中请求时间间隔:");
        label_5.setBounds(57, 106, 151, 15);
        panelOther.add(label_5);
        
        txtLoopRequestIntervalMS = new JTextField();
        txtLoopRequestIntervalMS.setBounds(211, 103, 86, 21);
        panelOther.add(txtLoopRequestIntervalMS);
        txtLoopRequestIntervalMS.setColumns(10);
        
        JLabel label_6 = new JLabel("毫秒");
        label_6.setBounds(300, 106, 44, 15);
        panelOther.add(label_6);
        
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
        );
        contentPane.setLayout(gl_contentPane);
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        if("add".equals(this.mode))
            setTitle("增加帐号设置");
        else if("update".equals(this.mode))
            setTitle("修改帐号设置");
    }
    
    public void loadForUpdate(UserSetting us) {
        setMode("update");
        // 帐号
        txtUid.setText(us.user.uid.toString());
        txtUsername.setText(us.user.username);
        pwdPassword.setText(us.user.password);
        chkEchoPwdPlain.setSelected(false);
        
        // 考试
        txtExamCode.setText("PTE-A");
        
        // 测试中心
        String countryCode = (String) us.testCentersCriteria.get("countryCode");
        DefaultComboBoxModel model = (DefaultComboBoxModel)comboCountry.getModel();
        for(int i = 1; i < model.getSize(); i++) {
            if(((Location)model.getElementAt(i)).code.equals(countryCode)) {
                comboCountry.setSelectedIndex(i);
                break;
            }
        }
        String stateCode = (String) us.testCentersCriteria.get("stateCode");
        if(comboState.getModel().getSize() > 0) {
            model = (DefaultComboBoxModel)comboState.getModel();
            for(int i = 1; i < model.getSize(); i++) {
                if(((Location)model.getElementAt(i)).code.equals(stateCode)) {
                    comboState.setSelectedIndex(i);
                    break;
                }
            }
            lblState.setVisible(true);
            comboState.setVisible(true);
        }
        txtCity.setText((String)us.testCentersCriteria.get("city"));
        
        // 约会
        fieldStartDate.setText(CalendarUtils.formatYMD(us.apptSearchRule.startCalendar));
        if(us.apptSearchRule.type / 10 == 1) {
            txtTime.setText(CalendarUtils.formatHM(us.apptSearchRule.startCalendar));
            radiobtnFixed.setSelected(true);
        }
        else if(us.apptSearchRule.type / 10 == 2) {
            radiobtnRange.setSelected(true);
            fieldEndDate.setText(CalendarUtils.formatYMD(us.apptSearchRule.endCalendar));
            listTimeRange.setSelectedIndex(0);
            Time startTime = new Time(us.apptSearchRule.startCalendar.get(Calendar.HOUR_OF_DAY), us.apptSearchRule.startCalendar.get(Calendar.MINUTE));
            Time endTime = new Time(us.apptSearchRule.endCalendar.get(Calendar.HOUR_OF_DAY), us.apptSearchRule.endCalendar.get(Calendar.MINUTE));
            for(int i = 0; i < timeRanges.length; i++) {
                if(timeRanges[i][0].equals(startTime) && timeRanges[i][1].equals(endTime)) {
                    listTimeRange.setSelectedIndex(i + 1);
                    break;
                }
            }
        }
        chkEarliest.setSelected(us.apptSearchRule.type % 10 == 1);
        
        // 支付
        if(us.applyType == 1) {
            radiobtnMoney.setSelected(true);
            txtParamVoucherNumber.setText("");
        } else {
            radiobtnParamVoucherNumber.setSelected(true);
            txtParamVoucherNumber.setText(us.paramVoucherNumber);
        }
        
        // 信用卡
        comboPaymentType.setSelectedItem(new Option(null, us.creditCard.get("paymentType")));
        txtCardNumber.setText((String)us.creditCard.get("cardNumber"));
        txtCardHoldersName.setText((String)us.creditCard.get("cardHoldersName"));
        comboCardExpMonth.setSelectedItem(us.creditCard.get("cardExpMonth"));
        comboCardExpYear.setSelectedItem(us.creditCard.get("cardExpYear"));
        txtCardSecCode.setText((String)us.creditCard.get("cardSecCode"));
        
        // 其它
        chkSearchSeatOnly.setSelected(us.searchSeatOnly);
        txtLoopRequestIntervalMS.setText(String.valueOf(us.loopRequestIntervalMS));
        
        tabbedPane.setSelectedIndex(3);
    }

    public void startAdd() {
        setMode("add");
        Calendar nowCalendar = Calendar.getInstance();
        
        // 帐号
        txtUsername.setText("");
        pwdPassword.setText("");
        chkEchoPwdPlain.setSelected(true);
        
        // 考试
        txtExamCode.setText("PTE-A");
        
        // 测试中心
        comboCountry.setSelectedIndex(0);
        if(comboState.getModel().getSize() > 0)
            comboState.setSelectedIndex(0);
        lblState.setVisible(false);
        comboState.setVisible(false);
        txtCity.setText("");
        
        // 约会
        fieldStartDate.setText(CalendarUtils.formatYMD(nowCalendar));
        fieldEndDate.setText("");
        radiobtnRange.setSelected(true);
        listTimeRange.setSelectedIndex(0);
        chkEarliest.setSelected(false);

        // 支付
        radiobtnMoney.setSelected(true);
        txtParamVoucherNumber.setText("");
        
        // 信用卡
        comboPaymentType.setSelectedItem(new Option("MasterCard", 4));;
        txtCardNumber.setText("");
        txtCardHoldersName.setText("");
        comboCardExpMonth.setSelectedItem(nowCalendar.get(Calendar.MONTH) + 1);
        comboCardExpYear.setSelectedItem(nowCalendar.get(Calendar.YEAR));
        txtCardSecCode.setText("");
        
        // 其它
        chkSearchSeatOnly.setSelected(true);
        txtLoopRequestIntervalMS.setText("0");

        // tab
        tabbedPane.setSelectedIndex(0);
    }
    
    private boolean validateUserSetting() throws Exception {
        boolean valid = false;
        if(StringUtils.isEmpty(txtUsername.getText().trim()) || StringUtils.isEmpty(new String(pwdPassword.getPassword()).trim())) {
            alertValidate("用户名或密码为空");
            tabbedPane.setSelectedIndex(0);
        }
        else if(StringUtils.isEmpty(txtExamCode.getText().trim())) {
            alertValidate("考试代码为空");
            tabbedPane.setSelectedIndex(1);
        }
        else if(comboCountry.getSelectedIndex() == 0) {
            alertValidate("国家为空");
            tabbedPane.setSelectedIndex(2);
        }
        else {
            return validateDate();
        }
        return valid;
    }
    
    private boolean validateDate() {
        boolean valid = false;
        String startDateStr = fieldStartDate.getText().trim().replaceAll(timeSeparatorPattern, "-");
        String endDateStr = fieldEndDate.getText().trim().replaceAll(timeSeparatorPattern, "-");
        if(startDateStr.indexOf("-") == 2) startDateStr = "20" + startDateStr;
        if(endDateStr.indexOf("-") == 2) endDateStr = "20" + endDateStr;
        if(radiobtnRange.isSelected()) {
            if(StringUtils.isEmpty(fieldStartDate.getText().trim()) || StringUtils.isEmpty(fieldEndDate.getText().trim()))
                alertValidate("区间日期没有填完");
            else if(!(fieldStartDate.getText().trim().matches(datePattern) && fieldEndDate.getText().trim().matches(datePattern)))
                alertValidate("日期格式错误");
            else
                try {
                    if(dateFormat.parse(startDateStr).before(dateFormat.parse(CalendarUtils.formatYMD(Calendar.getInstance()))))
                        alertValidate("开始日期小于当前日期");
                    else if(dateFormat.parse(startDateStr).compareTo(dateFormat.parse(endDateStr)) >= 0)
                        alertValidate("开始日期大于或等于结束日期");
                    else if(listTimeRange.getSelectedIndex() == -1)
                        alertValidate("未选择时间");
                    else {
                        valid = true;
                        fieldStartDate.setText(startDateStr);
                        fieldEndDate.setText(endDateStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            if(!valid)
                tabbedPane.setSelectedIndex(3);
        }
        else if(radiobtnFixed.isSelected()) {
            if(StringUtils.isEmpty(fieldStartDate.getText().trim()))
                alertValidate("日期为空");
            else if(!fieldStartDate.getText().trim().matches(datePattern))
                alertValidate("日期格式错误");
            else {
                valid = false;
                if(!chkEarliest.isSelected())
                    alertValidate("时间为空");
                /*
                else if(!chkEarliest.isSelected() && !txtTime.getText().trim().matches(timePattern))
                    validateAlert("时间格式错误");*/
                else {
                    valid = true;
                    fieldStartDate.setText(startDateStr);
                }
            }
            if(!valid) {
                fieldStartDate.setFocusable(true);
                tabbedPane.setSelectedIndex(3);
            }
        }
        return valid;
    }
    
    private void alertValidate(String message) {
        JOptionPane.showMessageDialog(null, message, "验证结果", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * 事件处理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
        case "btnNext": btnNext_Click(e); break;
        case "btnOk": btnOk_Click(e); break;
        case "btnCancel": btnCancel_Click(e); break;
        case "btnOpenStartDatePicker": btnOpenStartDatePicker_Click(e);break;
        case "btnOpenEndDatePicker": btnOpenEndDatePicker_Click(e);break;
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == tabbedPane) tabbedPane_Change(e);
        else if(e.getSource() == radiobtnRange || e.getSource() == radiobtnFixed) radiobtnRange_Change(e);
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == comboCountry) comboCountry_Change(e);
    }

    private void btnNext_Click(ActionEvent e) {
        tabbedPane.setSelectedIndex((tabbedPane.getSelectedIndex()+1) % tabbedPane.getTabCount());
    }

    private void btnOk_Click(ActionEvent e) {
        try {
            if(!validateUserSetting()) {
                return;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        
        UserSetting userSetting = new UserSetting();
        
        // 帐号
        User user = userSetting.user;
        user.username = txtUsername.getText().trim();
        user.password = new String(pwdPassword.getPassword()).trim();
        userSetting.user = user;
        //考试
        userSetting.examCode = txtExamCode.getText();
        // 测试中心
        Map<String, Object> testCentersCriteria = userSetting.testCentersCriteria;
        testCentersCriteria.put("countryCode", ((Location)comboCountry.getSelectedItem()).code);
        testCentersCriteria.put("city", txtCity.getText().trim());
        if(comboState.getSelectedIndex() > 0)
            testCentersCriteria.put("stateCode", ((Location)comboState.getSelectedItem()).code);
        else
            testCentersCriteria.put("stateCode", "");
        
        // 约会
        SearchApptRule apptSearchRule = userSetting.apptSearchRule;
        apptSearchRule.startCalendar = Calendar.getInstance();
        try {
            apptSearchRule.startCalendar.setTime(dateFormat.parse(fieldStartDate.getText().trim()));
        } catch (ParseException e1) {}
        if(radiobtnRange.isSelected()) {
            apptSearchRule.endCalendar = Calendar.getInstance();
            try {
                apptSearchRule.endCalendar.setTime(dateFormat.parse(fieldEndDate.getText().trim()));
            } catch (ParseException e1) {}
            
            if(listTimeRange.getSelectedIndex() > 0) {
                Time[] selectedTimeRange = timeRanges[listTimeRange.getSelectedIndex() - 1];
                apptSearchRule.startCalendar.set(Calendar.HOUR_OF_DAY, selectedTimeRange[0].getHour());
                apptSearchRule.startCalendar.set(Calendar.MINUTE, selectedTimeRange[0].getMinute());
                apptSearchRule.endCalendar.set(Calendar.HOUR_OF_DAY, selectedTimeRange[1].getHour());
                apptSearchRule.endCalendar.set(Calendar.MINUTE, selectedTimeRange[1].getMinute());
            }
            apptSearchRule.type = 2;
        } else if(radiobtnFixed.isSelected()) {
            /*
            if(StringUtils.isNotEmpty(txtTime.getText())) {
                String[] timePaths = txtTime.getText().split(timeSeparatorPattern);
                apptSearchRule.startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timePaths[0]));
                apptSearchRule.startCalendar.set(Calendar.MINUTE, Integer.parseInt(timePaths[1]));
            }*/
            apptSearchRule.type = 1;
        }
        apptSearchRule.type *= 10;
        if(chkEarliest.isSelected())
            apptSearchRule.type += 1;
        
        // 支付
        userSetting.paramVoucherNumber = "";
        if(radiobtnMoney.isSelected())
            userSetting.applyType = 1;
        else if(radiobtnParamVoucherNumber.isSelected()) {
            userSetting.applyType = 2;
            userSetting.paramVoucherNumber = StringUtils.trimToEmpty(txtParamVoucherNumber.getText());
        }
        
        // 信用卡
        userSetting.creditCard.put("paymentType", ((Option)comboPaymentType.getSelectedItem()).value);
        userSetting.creditCard.put("cardNumber", StringUtils.trimToEmpty(txtCardNumber.getText()));
        userSetting.creditCard.put("cardHoldersName", StringUtils.trimToEmpty(txtCardHoldersName.getText()));
        userSetting.creditCard.put("cardExpMonth", (Integer)comboCardExpMonth.getSelectedItem());
        userSetting.creditCard.put("cardExpYear", (Integer)comboCardExpYear.getSelectedItem());
        userSetting.creditCard.put("cardSecCode", StringUtils.trimToEmpty(txtCardSecCode.getText()));

        // 其它
        userSetting.searchSeatOnly = chkSearchSeatOnly.isSelected();
        userSetting.loopRequestIntervalMS = Integer.parseInt(txtLoopRequestIntervalMS.getText());
        
        if("add".equals(mode))
            userTableModel.addUser(userSetting);
        else if("update".equals(mode)) {
            userSetting.user.uid = Long.parseLong(txtUid.getText());
            boolean succeed = userTableModel.updateUser(userSetting);
            if(succeed)
                JOptionPane.showMessageDialog(this, "修改完成");
        }
        this.dispose();
    }
    
    private void btnCancel_Click(ActionEvent e) {
        this.dispose();
    }

    private void btnOpenStartDatePicker_Click(ActionEvent e) {
        DatePicker dp = new DatePicker(fieldStartDate, Locale.CHINA);
        try {
            dp.setSelectedDate(dateFormat.parse(fieldStartDate.getText()));
        } catch (ParseException e1) {}
        dp.start(fieldStartDate);
    }

    private void btnOpenEndDatePicker_Click(ActionEvent e) {
        DatePicker dp = new DatePicker(fieldEndDate, Locale.CHINA);
        try {
            dp.setSelectedDate(dateFormat.parse(fieldEndDate.getText()));
        } catch (ParseException e1) {}
        dp.start(fieldEndDate);
    }
    
    private void tabbedPane_Change(ChangeEvent e) {
    } 
    
    private void radiobtnRange_Change(ChangeEvent e) {
        if(radiobtnRange.isSelected()) {
            lblStartDate.setText("开始日期");
            lblEndDate.setVisible(true);
            fieldEndDate.setVisible(true);
            btnOpenEndDatePicker.setVisible(true);
            listTimeRange.setVisible(true);
        } else {
            lblStartDate.setText("日期");
            lblEndDate.setVisible(false);
            fieldEndDate.setVisible(false);
            btnOpenEndDatePicker.setVisible(false);
            listTimeRange.setVisible(false);
            chkEarliest.setSelected(true);
        }
    }
    
    private void comboCountry_Change(ItemEvent e) {
        if(comboCountry.getSelectedIndex() > 0) {
            List<Location> locs = locationDao.queryAllLocationsByPid(((Location)comboCountry.getSelectedItem()).id);
            if(locs.isEmpty()) {
                lblState.setVisible(false);
                comboState.setModel(new DefaultComboBoxModel());
                comboState.setVisible(false);
            } else {
                lblState.setVisible(true);
                DefaultComboBoxModel model = new DefaultComboBoxModel();
                model.addElement("请选择...");
                for(Location loc : locs)
                    model.addElement(loc);
                comboState.setModel(model);
                comboState.setVisible(true);
            }
        } else {
            lblState.setVisible(false);
            comboState.setModel(new DefaultComboBoxModel());
            comboState.setVisible(false);
        }
    }
    
}
