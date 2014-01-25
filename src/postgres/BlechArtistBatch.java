package postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BlechArtistBatch extends AbstractBatch {

	public void addRecords(String artist_id, String data[]) throws SQLException {
		//
	}

	public void addRecord(String data[]) throws SQLException {
		//
	}

	public ResultSet loadArtists() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT distinct artist_id FROM tracks");
		return rs;
	}

	public ResultSet loadSimilarArtists() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM similar_artists_truncated");
		return rs;
	}

	public ResultSet loadTracks() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM tracks_truncated");
		return rs;
	}
	
	public ResultSet loadArtistsMbtags() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM artist_mbtags_truncated");
		return rs;
	}
	
	public ResultSet loadArtistsTerms() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM artist_terms_truncated");
		return rs;
	}	
	
}