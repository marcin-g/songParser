package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.HistoryBatch;

public class TripletsParser {
	public static void main(String[] args) throws Exception {
		HistoryBatch batch=new  HistoryBatch();
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

		BufferedReader br = new BufferedReader(new FileReader(
				"res/song_train_triplets.csv"));
		BufferedWriter wr = new BufferedWriter(new FileWriter(
				"res/song_train_triplets_users.csv"));
		//kaggle_visible_evaluation_triplets
		//song_train_triplets
		try {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split(",");
				if(!map.containsKey(split[0]) || split.length<2){
					System.out.println(line);
				}
				else{
					wr.write(map.get(split[0]).toString()+","+split[1]+","+split[2]+"\n");
				}
				
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
