package assistant.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import assistant.SkinManager;
import assistant.UserLogger;
import assistant.process.Assistant;
import assistant.ui.UserTableModel.COLUMN;

public class MainFrame extends JFrame implements ActionListener {    
    private JPanel contentPane;
    private JTable tableUser;
    private JButton btnStopAll;
    private JButton btnStartAll;
    private JButton btnAddUserSetting;
    private JButton btnUpdateUserSetting;
    private JButton btnDeleteUserSetting;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmExit;
    
    public static Console console; //全局
    private UserSettingFrame frmUserSetting;
    private UserTableModel userTableModel;
    private Assistant assistant = new Assistant();
    private Logger log = LogManager.getLogger("assistant");
    private JButton btnStop;
    private JMenuItem mntmOpenLogDir;
    private JMenuItem mntmOpenUserTodayLog;
    private JButton btnStart;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainFrame() {
        log.trace("启动");
        try {
            URL url = getClass().getResource("/res/images/ico.jpg");
            if (url != null) {
                setIconImage(new ImageIcon(url).getImage());
            }
        } catch (Exception e) {}
        setTitle("PTE助手");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                new SkinManager().saveCurrent();
                log.trace("退出");
                System.exit(0);
            }
        });
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        mnFile = new JMenu("文件");
        mnFile.setActionCommand("mnFile");
        menuBar.add(mnFile);
        
        mntmOpenUserTodayLog = new JMenuItem("打开帐号今天的日志");
        mntmOpenUserTodayLog.setActionCommand("mntmOpenUserTodayLog");
        mntmOpenUserTodayLog.addActionListener(this);
        mnFile.add(mntmOpenUserTodayLog);
        
        mntmOpenLogDir = new JMenuItem("打开日志主目录");
        mntmOpenLogDir.setActionCommand("mntmOpenLogDir");
        mntmOpenLogDir.addActionListener(this);
        mnFile.add(mntmOpenLogDir);

        
        mntmExit = new JMenuItem("退出");
        mntmExit.setActionCommand("mntmExit");
        mntmExit.addActionListener(this);
        mnFile.add(mntmExit);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        tableUser = new JTable();
        userTableModel = new UserTableModel(tableUser);
        tableUser.setModel(userTableModel);
        tableUser.setRowHeight(20);
        tableUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        tableUser.getColumnModel().getColumn(0).setMaxWidth(30);
        tableUser.getColumnModel().getColumn(1).setMaxWidth(80);
        tableUser.setDefaultRenderer(Object.class, renderer);
        tableUser.getTableHeader().setDefaultRenderer(new UserTableHeaderCellRenderer(tableUser));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(new Dimension(20, 20));
        scrollPane.setViewportView(tableUser);
        
        btnAddUserSetting = new JButton("增加帐号");
        btnAddUserSetting.setIcon(new ImageIcon(getClass().getResource("/res/images/user-add.png")));
        btnAddUserSetting.setActionCommand("btnAddUserSetting");
        btnAddUserSetting.addActionListener(this);
        
        btnUpdateUserSetting = new JButton("查/修帐号");
        btnUpdateUserSetting.setIcon(new ImageIcon(getClass().getResource("/res/images/user-edit.png")));
        btnUpdateUserSetting.setActionCommand("btnUpdateUserSetting");
        btnUpdateUserSetting.addActionListener(this);
        
        btnDeleteUserSetting = new JButton("删除帐号");
        btnDeleteUserSetting.setIcon(new ImageIcon(getClass().getResource("/res/images/user-delete.png")));
        btnDeleteUserSetting.setActionCommand("btnDeleteUserSetting");
        btnDeleteUserSetting.addActionListener(this);
        
        frmUserSetting = new UserSettingFrame();
        frmUserSetting.setLocationRelativeTo(this);

        btnStartAll = new JButton("开始所有");
        btnStartAll.setActionCommand("btnStartAll");
        btnStartAll.setIcon(new ImageIcon(getClass().getResource("/res/images/start.png")));
        btnStartAll.addActionListener(this);

        btnStopAll = new JButton("中止所有");
        btnStopAll.setActionCommand("btnStopAll");
        btnStopAll.setIcon(new ImageIcon(getClass().getResource("/res/images/stop.png")));
        btnStopAll.addActionListener(this);

        btnStart = new JButton("开始");
        btnStart.setActionCommand("btnStart");
        btnStart.setIcon(new ImageIcon(getClass().getResource("/res/images/start.png")));
        btnStart.addActionListener(this);
        
        btnStop = new JButton("中止");
        btnStop.setActionCommand("btnStop");
        btnStop.setIcon(new ImageIcon(getClass().getResource("/res/images/stop.png")));
        btnStop.addActionListener(this);

        console = new Console();
        console.setBorder(null);

        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(console, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(btnAddUserSetting)
                    .addGap(10)
                    .addComponent(btnUpdateUserSetting)
                    .addGap(10)
                    .addComponent(btnDeleteUserSetting)
                    .addContainerGap(341, Short.MAX_VALUE))
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(btnStartAll, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(btnStopAll, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(btnStart)
                    .addGap(10)
                    .addComponent(btnStop)
                    .addContainerGap(280, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(btnDeleteUserSetting, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdateUserSetting, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddUserSetting, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                    .addGap(18)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED))
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(btnStartAll, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                    .addComponent(btnStopAll, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                                .addGap(64))
                            .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED))))
                    .addComponent(console, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }
    
    /**
     * 事件处理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
        case "btnAddUserSetting": btnAddUserSetting_Click(e); break;
        case "btnUpdateUserSetting": btnUpdateUserSetting_Click(e); break;
        case "btnDeleteUserSetting": btnDeleteUserSetting_Click(e); break;
        case "btnStartAll": btnStartAll_Click(e); break;
        case "btnStopAll": btnStopAll_Click(e); break;
        case "btnStart": btnStart_Click(e); break;
        case "btnStop": btnStop_Click(e); break;
        case "mntmOpenUserTodayLog": mntmOpenUserTodayLog_Click(e); break;
        case "mntmOpenLogDir": mntmOpenLogDir_Click(e); break;
        case "mntmExit": mntmExit_Click(e); break;
        }
    }

    private void btnAddUserSetting_Click(ActionEvent e) {
        frmUserSetting.userTableModel = userTableModel;
        frmUserSetting.startAdd();
        frmUserSetting.setVisible(true);
    }
    
    private void btnUpdateUserSetting_Click(ActionEvent e) {
        if (tableUser.getSelectedRowCount() <= 0) {  
            JOptionPane.showMessageDialog(null, "请选择帐号");  
            return;  
        }
        frmUserSetting.userTableModel = userTableModel;
        frmUserSetting.loadForUpdate(userTableModel.getSelectedUserSetting());
        frmUserSetting.setVisible(true);
    }
    
    private void btnDeleteUserSetting_Click(ActionEvent e) {
        if (tableUser.getSelectedRowCount() <= 0) {  
            JOptionPane.showMessageDialog(null, "请选择帐号");  
            return;  
        }
        int ret = JOptionPane.showConfirmDialog(this,
                "你确定要删除所选择的帐号?", null,
                JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.OK_OPTION)
            userTableModel.removeSeletedUserSetting();
    }

    private void btnStartAll_Click(ActionEvent e) {
        List<Long> uids = new ArrayList<Long>();
        for (int i = 0; i < userTableModel.getRowCount(); i++) {
            if ((boolean) userTableModel.getValueAt(i, UserTableModel.COLUMN.CHECK))
                uids.add((long)userTableModel.getValueAt(i, COLUMN.UID));
        }
        if (uids.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未勾选帐号");
            return;
        }
        assistant.start(uids);
    }
    
    private void btnStopAll_Click(ActionEvent e) {
        assistant.stopAll();
    }
    
    private void btnStart_Click(ActionEvent e) {
        if (tableUser.getSelectedRowCount() <= 0) {  
            JOptionPane.showMessageDialog(null, "请选择一个帐号");
            return;  
        }
        Long uid = (long)tableUser.getValueAt(tableUser.getSelectedRow(), COLUMN.UID);
        List<Long> uids = new ArrayList<Long>();
        uids.add(uid);

        assistant.start(uids);
    }
    
    private void btnStop_Click(ActionEvent e) {
        if (tableUser.getSelectedRowCount() <= 0) {  
            JOptionPane.showMessageDialog(null, "请选择一个帐号");
            return;  
        }
        Long uid = (long)tableUser.getValueAt(tableUser.getSelectedRow(), COLUMN.UID);
        assistant.stop(uid);
    }

    private void mntmOpenUserTodayLog_Click(ActionEvent e) {
        if (tableUser.getSelectedRowCount() <= 0) {  
            JOptionPane.showMessageDialog(null, "请选择一个帐号");
            return;  
        }
        Long uid = (long)tableUser.getValueAt(tableUser.getSelectedRow(), COLUMN.UID);
        try {
            File file = new File("assistant.logs/users/uid" + uid + "/" + UserLogger.fileNameDateFormat.format(new Date()) + ".log");
            if(file.exists()) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", " ", file.getPath()});
            } else {
                JOptionPane.showMessageDialog(null, "帐号uid" + uid + "还没有今天的日志");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private void mntmOpenLogDir_Click(ActionEvent e) {
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", " ", "assistant.logs"});
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private void mntmExit_Click(ActionEvent e) {
        log.trace("退出");
        System.exit(0);
    }
    
}
