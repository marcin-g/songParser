package parser;

import java.io.BufferedReader;
import java.io.FileReader;

import postgres.AbstractBatch;
import postgres.HistoryBatch;
import postgres.TrackSongBatch;

public class TracksSongParser {
	public static void main(String[] args) throws Exception {
		AbstractBatch batch = new TrackSongBatch();
		BufferedReader br = new BufferedReader(new FileReader(
				"res/taste_profile_song_to_tracks.txt"));
		long start = System.nanoTime();
		try {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split(" ");
				if (split.length > 0) {
					batch.addRecord(split);
				}
				else{
					throw new Exception("dupa");
				}
				line = br.readLine();
			}
		} finally {
			long elapsedTime = System.nanoTime() - start;
			System.out.println("wykonano w: " + elapsedTime);
			batch.close();
			br.close();
		}
	}
}
