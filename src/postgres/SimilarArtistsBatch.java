package postgres;

import java.sql.SQLException;

public class SimilarArtistsBatch extends AbstractBatch {

	String sql = "INSERT INTO SIMILAR_ARTISTS(artist_id,similar_artist_id) values(?,?)";

	public void addRecords(String artist_id, String data[]) throws SQLException {
		for (int x = 0; x < data.length; x++) {
			addRecord(new String[] { artist_id, data[x] });
		}
	}

	public void addRecord(String data[]) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		statement.setString(1, data[0]);
		statement.setString(2, data[1]);
		statement.addBatch();
		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter = 0;
			//System.out.println(summaryCount++);
		}
	}

}