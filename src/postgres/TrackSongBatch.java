package postgres;

import java.sql.SQLException;

public class TrackSongBatch extends AbstractBatch{

	String sql="INSERT INTO TRACK_TO_SONG(song_id,track_id) values(?,?)";

	public void addRecord(String data[]) throws SQLException {
		if (recordsCounter == 0) {
			statement = connection.prepareStatement(sql);
		}
		if(data.length==1){
			statement.setString(1, data[0]);
			statement.setString(2, "");
			statement.addBatch();
			recordsCounter++;
		}
		else if(data.length==2){
			statement.setString(1, data[0]);
			statement.setString(2, data[1]);
			statement.addBatch();
			recordsCounter++;
		}
		else{
			for(int i=1;i<data.length;i++){
				statement.setString(1, data[0]);
				statement.setString(2, data[i]);
				statement.addBatch();
				recordsCounter++;
			}
		}
		if (recordsCounter >= MAX_COUNTER) {
			statement.executeBatch();
			recordsCounter=0;
			System.out.println((summaryCount++)+"0000");
		}
	}


}
