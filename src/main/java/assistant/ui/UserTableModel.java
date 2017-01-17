package assistant.ui;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import assistant.db.UserSettingDao;
import assistant.domain.UserSetting;

public class UserTableModel extends DefaultTableModel {
    private static final Class<?>[] columnTypes = {Boolean.class, Long.class, Long.class, String.class};
    private JTable table;
    private UserSettingDao userSettingDao = new UserSettingDao();
    private Logger log = LogManager.getFormatterLogger(this.getClass());

    public UserTableModel(JTable table) {
        super(new Object[][] {}, new String[]{"", "序号", "uid", "帐号"});
        this.table = table;
        reloadData();
    }
    
    public void addUser(UserSetting us) {
        boolean succeed = userSettingDao.saveUserSetting(us);
        if(succeed) {
            reloadData();
            log.info("成功增加1个帐号(uid=%d)",us.user.uid);
            //this.addRow(new Object[]{true, this.getRowCount() + 1, us.user.uid, us.user.username});
        }
    }
    
    public boolean updateUser(UserSetting us) {
        boolean succeed = userSettingDao.updateUserSettingByUid(us);
        if(succeed) {
            reloadData();
            log.info("成功修改帐号(uid=%d)", us.user.uid);
        }
        return succeed;
    }
    
    public UserSetting getSelectedUserSetting() {
        long uid = (long)this.getValueAt(table.getSelectedRow(), COLUMN.UID);
        return userSettingDao.getUserSettingByUid(uid);
    }
    
    public void removeSeletedUserSetting() {
        int selectedRow = table.getSelectedRow();
        Long[] uids = new Long[1];
        uids[0] = (long)this.getValueAt(selectedRow, COLUMN.UID);
        boolean succeed = userSettingDao.delUsersSettingByUid(uids);
        if(succeed) {
            removeRow(selectedRow);
            log.info("成功删除帐号(uid=%d)", uids[0]);
        }
    }

    private void reloadData() {
        table.clearSelection();
        getDataVector().clear();       
        List<UserSetting> uss = userSettingDao.queryAllUserSettings();
        for(UserSetting us : uss) {
            this.addRow(new Object[]{false, this.getRowCount() + 1, us.user.uid, us.user.username});
        }
    }
    
    @Override
    public Class<?> getColumnClass(int c) {
        return columnTypes[c];
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        if (c == COLUMN.CHECK) {
            return true;
        } else {
            return false;
        }
    }

    public void selectAll(boolean value) {
        for (int i = 0; i < getRowCount(); i++) {
            this.setValueAt(value, i, 0);
        }
    }

    public interface COLUMN {
        int CHECK = 0;
        int NO = 1;
        int UID = 2;
        int USERNAME = 3;
    };
}