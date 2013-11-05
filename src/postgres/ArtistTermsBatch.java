package postgres;

import java.sql.SQLException;

public class ArtistTermsBatch extends AbstractBatch {

	String sql = "INSERT INTO ARTIST_TERMS(artist_id,term,term_freq,term_weight) values(?,?,?,?)";

	public void addRecords(String artist_id, String terms[], double[] terms_freq, double[] terms_weight)
			throws SQLException {
		for (int x = 0; x < terms.length; x++) {
			addRecord(artist_id, terms[x], terms_freq[x], terms_weight[x]);
		}
	}

	public void addRecord(String data[]) throws SQLException {

	}

	public void addRecord(String artist_id, String term, double term_freq, double term_weight) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		statement.setString(1, artist_id);
		statement.setString(2, term);
		statement.setDouble(3, term_freq);
		statement.setDouble(4, term_weight);
		statement.addBatch();
		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter = 0;
			// System.out.println(summaryCount++);
		}
	}

}