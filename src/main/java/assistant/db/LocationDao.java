package assistant.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import assistant.domain.Location;


public class LocationDao {
    private SQLiteDBUtil dbUtil = new SQLiteDBUtil();
    
    public LocationDao() {
        createTable();
    }
    
    public void createTable() {
        dbUtil.executeUpdate(
            "CREATE TABLE IF NOT EXISTS location(" +
            "id      INT        PRIMARY KEY NOT NULL, " +
            "pid     INT        NOT NULL, " +
            "name    CHAR(30)	NOT NULL," +
            "code    CHAR(3)    NOT NULL" +
            ")");
    }
	
    public List<Location> queryAllLocationsByPid(Long pid) {
        List<Location> locations = new ArrayList<Location>();
        try {
            dbUtil.open();
            ResultSet rs = dbUtil.statement.executeQuery("SELECT * FROM location WHERE pid=" + pid);
            while(rs.next()) {
                Location loc = new Location();
                loc.id = rs.getLong("id");
                loc.pid = rs.getLong("pid");
                loc.name = rs.getString("name");
                loc.code = rs.getString("code");
                locations.add(loc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbUtil.close();
        }
        return locations;
    }
}
