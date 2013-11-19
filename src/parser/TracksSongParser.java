package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.AbstractBatch;
import postgres.HistoryBatch;
import postgres.TrackSongBatch;

public class TracksSongParser {
	public static void main(String[] args) throws Exception {
		AbstractBatch batch = new TrackSongBatch();
		BufferedReader br = new BufferedReader(new FileReader(
				"res/taste_profile_song_to_tracks.txt"));
		BufferedWriter wr = new BufferedWriter(new FileWriter(
				"res/taste_profile_song_to_tracks.csv"));
		
		BufferedReader br1 = new BufferedReader(new FileReader(
				"res/songs.csv"));
		//train_triplets
		//kaggle_visible_evaluation_triplets
		HashMap<String, Integer> songs=new HashMap<>();
		try {
			String line = br1.readLine();
			while (line != null) {
				String[] split = line.split(",");
				songs.put(split[1], Integer.parseInt(split[0]));
				line = br1.readLine();
			}
			//map.put("3e9f26065c645dd00179ba5016f337d", 0);
			
		} finally {
			System.out.println("wczytano mape");
			br1.close();
		}
		int count=1;
		try {
			String line = br.readLine();
			while (line != null) {
				;
				String[] split = line.split(" ");
				if(split.length==1){
					wr.write(count+","+songs.get(split[0])+",\n");
					count++;
				}
				else{
					for (int i = 1; i < split.length; i++) {
						wr.write(count+","+songs.get(split[0])+","+split[i]+"\n");
						count++;
					}
				}
				

				line = br.readLine();
			}
		} finally {
			System.out.println("wykonano w: ");
			//batch.close();
			br.close();
			wr.close();
		}
	}
}
