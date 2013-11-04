package postgres;

import java.sql.SQLException;

public class SongBatch extends AbstractBatch {

	String sql = "INSERT INTO TRACKS(track_id,song_id,title,artist_id,artist_name,artist_location,artist_hotttnesss,artist_familiarity,danceability,duration,energy,release,song_hotttnesss,year) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public void addRecord(String track_id, String song_id, String title, String artist_id, String artist_name,
			String artist_location, double artist_hotttnesss, double artist_familiarity, double danceability,
			double duration, double energy, String release, double song_hotttnesss, int year) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		statement.setString(1, track_id);
		statement.setString(2, song_id);
		statement.setString(3, title);
		statement.setString(4, artist_id);
		statement.setString(5, artist_name);
		statement.setString(6, artist_location);
		statement.setDouble(7, artist_hotttnesss);
		statement.setDouble(8, artist_familiarity);
		statement.setDouble(9, danceability);
		statement.setDouble(10, duration);
		statement.setDouble(11, energy);
		statement.setString(12, release);
		statement.setDouble(13, song_hotttnesss);
		statement.setInt(14, year);
		statement.addBatch();
		recordsCounter++;
		if (recordsCounter == MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter = 0;
			System.out.println(summaryCount++);
		}

	}

	@Override
	public void addRecord(String[] data) throws SQLException {
		// TODO Auto-generated method stub

	}

}
