package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.AbstractBatch;
import postgres.HistoryBatch;
import postgres.ReportMapBatch;

public class RankingParser {
	static long start=0;
	public static void main(String[] args) throws Exception {
		BufferedReader br1 = new BufferedReader(new FileReader(
				"res/100songtriplets.csv"));
		//train_triplets
		//kaggle_visible_evaluation_triplets
		start = System.nanoTime();    
		AbstractBatch batch=new ReportMapBatch();
		
		try {
			String line = br1.readLine();
			while (line != null) {
				String[] split = line.split(";");
				batch.addRecord(split);
				line = br1.readLine();
			}
			//map.put("3e9f26065c645dd00179ba5016f337d", 0);
			
		} finally {
			System.out.println("Wykonano w: "+(System.nanoTime()-start));
			br1.close();
			batch.close();
		}

	}
}
