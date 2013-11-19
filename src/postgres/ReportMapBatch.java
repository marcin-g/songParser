package postgres;

import java.sql.SQLException;
import java.sql.Statement;

public class ReportMapBatch extends AbstractBatch {

	Statement standardStatement;
	String sql = "INSERT INTO TRACKS(track_id,song_id,title,artist_id,artist_name,artist_location,artist_hotttnesss,artist_familiarity,danceability,duration,energy,release,release_7digitalid, song_hotttnesss,year) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public void addRecord(String userId, String songId)
			throws SQLException {
		if (recordsCounter == 0) {
			standardStatement=connection.createStatement();
		}
		standardStatement.addBatch("UPDATE REPORT_MAP SET ID_"+songId+"=1 WHERE USER_ID="+userId);

		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			standardStatement.executeBatch();
			recordsCounter = 0;
			System.out.println(summaryCount++);
		}

	}

	@Override
	public void addRecord(String[] data) throws SQLException {
		this.addRecord(data[0],data[1]);

	}
	@Override
	public void close() throws SQLException {
		standardStatement.executeBatch();
		recordsCounter=0;
		connection.close();
	}
}
