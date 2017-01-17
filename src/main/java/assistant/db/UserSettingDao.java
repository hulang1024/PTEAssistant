package assistant.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import assistant.domain.UserSetting;
import assistant.utils.CalendarUtils;



public class UserSettingDao {
    private SQLiteDBUtil dbUtil = new SQLiteDBUtil();
	
    public UserSettingDao() {
        createTable();
    }
	
    public void createTable() {
        dbUtil.executeUpdate(
            "CREATE TABLE IF NOT EXISTS usersetting(" +
            "uid            INTEGER		PRIMARY KEY AUTOINCREMENT," +
            "username       CHAR(20)	NOT NULL," +
            "password       CHAR(20)," +
		    
            "examCode       CHAR(30)    NOT NULL," +
            
            "countryCode    CHAR(3)     NOT NULL," +
            "city           CHAR(30)    NOT NULL," +
            "stateCode      CHAR(3)     NOT NULL," +

            "type           INTEGER     NOT NULL," +
            "startCalendar  CHAR(16)    NOT NULL," +
            "endCalendar    CHAR(16)    NOT NULL," +
		        
            "applyType INTEGER," +
            "paramVoucherNumber CHAR(100)," +
                
            "paymentType INTEGER," +
            "cardNumber CHAR(50)," +
            "cardHoldersName CHAR(100)," +
            "cardExpYear INTEGER," +
            "cardExpMonth INTEGER," +
            "cardSecCode CHAR(4)," +
            
            "searchSeatOnly BOOLEAN," +
            "loopRequestIntervalMS BIGINT" + 
        ")");
    }

    public boolean saveUserSetting(UserSetting us) {
        String startCalendarStr = CalendarUtils.formatYMDHM(us.apptSearchRule.startCalendar);
        String endCalendarStr = us.apptSearchRule.endCalendar != null ? CalendarUtils.formatYMDHM(us.apptSearchRule.endCalendar) : "";
        String format = "INSERT INTO usersetting("
            + "username,password,examCode,countryCode,city,stateCode,type,startCalendar,endCalendar,"
            + "applyType,paramVoucherNumber,"
            + "paymentType,cardNumber,cardHoldersName,cardExpYear,cardExpMonth,cardSecCode,"
            + "searchSeatOnly,loopRequestIntervalMS)"
            + "VALUES('%s','%s','%s','%s','%s','%s',%d,'%s','%s',%d,'%s',%d,'%s','%s',%d,%d,'%s',%d,%d)";
        int rows = dbUtil.executeUpdate(
            String.format(format,
                us.user.username,
                us.user.password,
                us.examCode,
                us.testCentersCriteria.get("countryCode"),
                us.testCentersCriteria.get("city"),
                us.testCentersCriteria.get("stateCode"),
                us.apptSearchRule.type,
                startCalendarStr,
                endCalendarStr,
                us.applyType,
                us.paramVoucherNumber,
                us.creditCard.get("paymentType"),
                us.creditCard.get("cardNumber"),
                us.creditCard.get("cardHoldersName"),
                us.creditCard.get("cardExpYear"),
                us.creditCard.get("cardExpMonth"),
                us.creditCard.get("cardSecCode"),
                BooleanUtils.toInteger(us.searchSeatOnly),
                us.loopRequestIntervalMS));
        return rows > 0;
    }
    
    public boolean updateUserSettingByUid(UserSetting us) {
        String startCalendarStr = CalendarUtils.formatYMDHM(us.apptSearchRule.startCalendar);
        String endCalendarStr = us.apptSearchRule.endCalendar != null ? CalendarUtils.formatYMDHM(us.apptSearchRule.endCalendar) : "";
        String format = "UPDATE usersetting SET "
            + "username='%s',password='%s',"
            + "examCode='%s',countryCode='%s',city='%s',stateCode='%s',"
            + "type=%d,startCalendar='%s',endCalendar='%s',"
            + "applyType=%d,paramVoucherNumber='%s',"
            + "paymentType=%d,cardNumber='%s',cardHoldersName='%s',cardExpYear=%d,cardExpMonth=%d,cardSecCode='%s',"
            + "searchSeatOnly=%d,loopRequestIntervalMS=%d"
            + " WHERE uid=%d";
        return dbUtil.executeUpdate(
            String.format(format,
                us.user.username,
                us.user.password,
                us.examCode,
                us.testCentersCriteria.get("countryCode"),
                us.testCentersCriteria.get("city"),
                us.testCentersCriteria.get("stateCode"),
                us.apptSearchRule.type,
                startCalendarStr,
                endCalendarStr,
                us.applyType,
                us.paramVoucherNumber,
                us.creditCard.get("paymentType"),
                us.creditCard.get("cardNumber"),
                us.creditCard.get("cardHoldersName"),
                us.creditCard.get("cardExpYear"),
                us.creditCard.get("cardExpMonth"),
                us.creditCard.get("cardSecCode"),
                BooleanUtils.toInteger(us.searchSeatOnly),
                us.loopRequestIntervalMS,
                us.user.uid)) > 0;
    }
    
    public boolean delUsersSettingByUid(Long[] uids) {
        return dbUtil.executeUpdate(
            String.format("DELETE FROM usersetting WHERE uid IN(%s)", StringUtils.join(uids), ",")) >= 0;
    }
    
    public UserSetting getUserSettingByUid(long uid) {
        try {
            dbUtil.open();
            ResultSet rs = dbUtil.statement.executeQuery("SELECT * FROM usersetting WHERE uid=" + uid);
            if(rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.close();
        }
        return null;
    }
    
    public List<UserSetting> queryAllUserSettings(List<Long> uids) {
        return queryUserSettings(String.format("SELECT * FROM usersetting WHERE uid IN(%s)", StringUtils.join(uids, ",")));
    }
    
    public List<UserSetting> queryAllUserSettings() {
        return queryUserSettings("SELECT * FROM usersetting");
    }
    
    public List<UserSetting> queryUserSettings(String sql) {
        List<UserSetting> userSettings = new ArrayList<UserSetting>();
        try {
            dbUtil.open();
            ResultSet rs = dbUtil.statement.executeQuery(sql);
            while(rs.next()) {
                userSettings.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.close();
        }
        return userSettings;
    }
    
    private UserSetting mapRow(ResultSet rs) throws SQLException, ParseException {
        UserSetting us = new UserSetting();
        us.user.uid = rs.getLong("uid");
        us.user.username = rs.getString("username");
        us.user.password = rs.getString("password");
        us.examCode = rs.getString("examCode");
        us.testCentersCriteria.put("countryCode", rs.getString("countryCode"));
        us.testCentersCriteria.put("city", rs.getString("city"));
        us.testCentersCriteria.put("stateCode", rs.getString("stateCode"));
        
        us.apptSearchRule.type = rs.getInt("type");
        us.apptSearchRule.startCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        us.apptSearchRule.startCalendar.setTime(sdf.parse(rs.getString("startCalendar")));
        String endCalendar = rs.getString("endCalendar");
        if(StringUtils.isNotEmpty(endCalendar)) {
            us.apptSearchRule.endCalendar = Calendar.getInstance();
            us.apptSearchRule.endCalendar.setTime(sdf.parse(endCalendar));
        }
        
        us.applyType = rs.getInt("applyType");
        us.paramVoucherNumber = rs.getString("paramVoucherNumber");

        us.creditCard.put("paymentType", rs.getInt("paymentType"));
        us.creditCard.put("cardNumber", rs.getString("cardNumber"));
        us.creditCard.put("cardHoldersName", rs.getString("cardHoldersName"));
        us.creditCard.put("cardExpYear", rs.getInt("cardExpYear"));
        us.creditCard.put("cardExpMonth", rs.getInt("cardExpMonth"));
        us.creditCard.put("cardSecCode", rs.getString("cardSecCode"));
        
        us.searchSeatOnly = rs.getBoolean("searchSeatOnly");
        us.loopRequestIntervalMS = rs.getLong("loopRequestIntervalMS");
        return us;
    }
}
