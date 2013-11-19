package postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JaccardBatch extends AbstractBatch {

	String sql = "SELECT * FROM report_map";

	public void addRecords(String artist_id, String data[]) throws SQLException {
		//
	}

	public void addRecord(String data[]) throws SQLException {
		//
	}
	
	public ResultSet loadRecords() throws SQLException {
		Statement stmt=connection.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM report_map");
		/*while (rs.next()) {
			System.out.println(rs.getInt(1));
		}*/
		return rs;
	}

}