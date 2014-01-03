package postgres;

import java.sql.SQLException;

public class KaggleSongBatch extends AbstractBatch {

	String sql = "UPDATE SONGS SET kaggle_id= ? where song_id= ? ";
	
	@Override
	public void addRecord(String[] data) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}

		statement.setInt(1, Integer.parseInt(data[1]));
		statement.setString(2, data[0]);
		statement.addBatch();
		recordsCounter++;
		
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter = 0;
			System.out.println(++summaryCount);
		}
	}

}
