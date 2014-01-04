package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.HistoryBatch;

public class TripletsParser {
	public static void main(String[] args) throws Exception {
		//HistoryBatch batch=new  HistoryBatch();
		BufferedReader br1 = new BufferedReader(new FileReader(
				"res/users.csv"));
		//train_triplets
		//kaggle_visible_evaluation_triplets
		HashMap<String, Integer> map=new HashMap<>();
		long start = System.nanoTime();    
		try {
			String line = br1.readLine();
			while (line != null) {
				String[] split = line.split(",");
				map.put(split[1], Integer.parseInt(split[0]));
				line = br1.readLine();
			}
			//map.put("3e9f26065c645dd00179ba5016f337d", 0);
			
		} finally {
			System.out.println("wczytano mape");
			br1.close();
		}
		BufferedReader br2 = new BufferedReader(new FileReader(
				"res/songs.csv"));
		//train_triplets
		//kaggle_visible_evaluation_triplets
		HashMap<String, Integer> songmap=new HashMap<>();
		try {
			String line = br2.readLine();
			while (line != null) {
				String[] split = line.split(",");
				songmap.put(split[1], Integer.parseInt(split[0]));
				line = br2.readLine();
			}
			//map.put("3e9f26065c645dd00179ba5016f337d", 0);
			
		} finally {
			System.out.println("wczytano mape");
			br2.close();
		}

		BufferedReader br = new BufferedReader(new FileReader(
				"res/kaggle_visible_evaluation_triplets.txt"));
		BufferedWriter wr = new BufferedWriter(new FileWriter(
				"res/kaggle_visible_evaluation_triplets_new.csv"));
		//kaggle_visible_evaluation_triplets
		//song_train_triplets
		//long id=48373587;
		try {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				
					wr.write(split[0]+","+songmap.get(split[1])+","+split[2]+"\n");
				//id++;
				
				line = br.readLine();
			}
		} finally {
			long elapsedTime = System.nanoTime() - start;
			System.out.println("wykonano w: "+elapsedTime);
			wr.close();
			br.close();
		}
	}
}
