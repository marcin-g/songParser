package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Time;
import java.util.Date;

import postgres.HistoryBatch;

public class TripletsParser {
	public static void main(String[] args) throws Exception {
		HistoryBatch batch=new  HistoryBatch();
		BufferedReader br = new BufferedReader(new FileReader(
				"res/train_triplets.txt"));
		long start = System.nanoTime();    
		try {
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				batch.addRecord(split[0],split[1],Integer.parseInt(split[2].trim()));

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
