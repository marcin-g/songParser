package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.HistoryBatch;
import postgres.KaggleSongBatch;

public class KagleSongParser {
	public static void main(String[] args) throws Exception {
		BufferedReader br1 = new BufferedReader(new FileReader(
				"res/kaggle_songs.txt"));
		//train_triplets
		//kaggle_visible_evaluation_triplets
		KaggleSongBatch batch=new KaggleSongBatch();
		long start = System.nanoTime();    
		try {
			String line = br1.readLine();
			while (line != null) {
				String[] split = line.split(" ");
				if(split.length==2){

					batch.addRecord(split);
				}
				//System.out.println(split);
				line = br1.readLine();
			}
			//map.put("3e9f26065c645dd00179ba5016f337d", 0);
			
		} finally {
			System.out.println("wykonano "+(System.nanoTime()-start));
			batch.close();
			br1.close();
		}
	}
}
