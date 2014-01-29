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
				"res/inclass_kaggle_visible_evaluation_triplets.txt"));
		BufferedWriter wr = new BufferedWriter(new FileWriter(
				"res/inclass_kaggle_visible_evaluation_triplets.csv"));

		BufferedReader br1 = new BufferedReader(new FileReader("res/songs.csv"));
		// train_triplets
		// kaggle_visible_evaluation_triplets
		HashMap<String, Integer> songs = new HashMap<>();
		try {
			String line = br1.readLine();
			while (line != null) {
				String[] split = line.split(",");
				songs.put(split[1], Integer.parseInt(split[0]));
				line = br1.readLine();
			}
			// map.put("3e9f26065c645dd00179ba5016f337d", 0);

		} finally {
			System.out.println("wczytano mape");
			br1.close();
		}
		int count = 0;
		String userId="";
		
		try {
			String line = br.readLine();
			while (line != null) {
				;
				String[] split = line.split("\\s+");
				if(!userId.equals(split[0]));{
					count++;
				}
				wr.write(count + "," + songs.get(split[1]) +split[2]+ ",\n");

				line = br.readLine();
			}
		} finally {
			System.out.println("wykonano w: ");
			// batch.close();
			br.close();
			wr.close();
		}
	}
}
