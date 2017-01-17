package assistant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLiteDBUtil {
	public Connection connection;
	public Statement statement;

	public final String Filename = "assistant.db";
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public SQLiteDBUtil() {
	}
	
	public void open() {
	    try {
	    	connection = DriverManager.getConnection("jdbc:sqlite:" + Filename);
	    	statement = this.connection.createStatement();
	    } catch ( Exception e ) {
	    	System.exit(0);
	    }
	}
	
	public void close() {
		try {
		    statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int executeUpdate(String sql) {
		try {
		    open();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    close();
		}
		return -1;
	}
	
    public int[] executeBatchUpdate(List<String> sqlList) {
        try {
            open();
            for(String sql : sqlList)
                statement.addBatch(sql);
            return statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }
}
