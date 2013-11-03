package postgres;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.PGConnection;
import org.postgresql.core.ConnectionFactory;

public class HistoryBatch {

	Connection connection = null;
	PreparedStatement statement = null;
	int recordsCounter = 0;
	String sql="INSERT INTO TRIPLETS(user_id,song_id,counts) values(?,?,?)";
	static int MAX_COUNTER = 10000;
	private int summaryCount = 0;

	public HistoryBatch() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", "postgres",
					"abc");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addRecord(String userId, String songId, int count) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		statement.setString(1, userId);
		statement.setString(2, songId);
		statement.setInt(3, count);
		statement.addBatch();
		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter=0;
			System.out.println(summaryCount++);
		}
	}

	public void close() throws SQLException {
		statement.executeBatch();
		recordsCounter=0;
		connection.close();
	}

}
