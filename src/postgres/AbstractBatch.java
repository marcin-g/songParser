package postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractBatch {

	protected Connection connection = null;
	protected PreparedStatement statement = null;
	protected int recordsCounter = 0;
	protected static int MAX_COUNTER = 1000;
	protected int summaryCount = 0;

	public AbstractBatch() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", "postgres",
					"abc");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	abstract public  void addRecord(String data[])throws SQLException;

	public void close() throws SQLException {
		statement.executeBatch();
		recordsCounter=0;
		connection.close();
	}

}
