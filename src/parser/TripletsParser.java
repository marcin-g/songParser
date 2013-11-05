package parser;

import java.io.BufferedReader;
import java.io.FileReader;

import postgres.HistoryBatch;

public class TripletsParser {
	public static void main(String[] args) throws Exception {
		HistoryBatch batch=new  HistoryBatch();
		BufferedReader br = new BufferedReader(new FileReader(
				"res/kaggle_visible_evaluation_triplets.txt"));
		long start = System.nanoTime();    
		try {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				batch.addRecord(split);

				line = br.readLine();
			}
		} finally {
			long elapsedTime = System.nanoTime() - start;
			System.out.println("wykonano w: "+elapsedTime);
			batch.close();
			br.close();
		}
	}
}