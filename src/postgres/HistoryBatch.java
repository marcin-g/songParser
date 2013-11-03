package postgres;

import java.sql.SQLException;

public class HistoryBatch extends AbstractBatch{

	String sql="INSERT INTO TRIPLETS(user_id,song_id,counts) values(?,?,?)";

	public void addRecord(String data[]) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		statement.setString(1, data[0]);
		statement.setString(2, data[1]);
		statement.setInt(3, Integer.parseInt(data[2].trim()));
		statement.addBatch();
		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter=0;
			System.out.println(summaryCount++);
		}
	}

}
