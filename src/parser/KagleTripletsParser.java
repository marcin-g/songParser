package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import postgres.HistoryBatch;
import sun.security.util.Length;

public class KagleTripletsParser {
	public static void main(String[] args) throws Exception {
		long start = System.nanoTime();

		BufferedReader br2 = new BufferedReader(new FileReader(
				"res/song_kaggle.csv"));
		// train_triplets
		// kaggle_visible_evaluation_triplets
		HashMap<String, String> songmap = new HashMap<>();
		try {
			String line = br2.readLine();
			while (line != null) {
				String[] split = line.split(";");
				if (split.length == 3) {
					songmap.put(split[0], split[2]);
				} else {
					System.out.println("jestem");
				}
				line = br2.readLine();
			}
			// map.put("3e9f26065c645dd00179ba5016f337d", 0);

		} finally {
			System.out.println("wczytano mape");
			br2.close();
		}

		//BufferedReader br = new BufferedReader(new FileReader("res/results/betterSongResultsTest.csv"));
		//BufferedWriter wr = new BufferedWriter(new FileWriter("res/results/betterSongResultsTestKaggle.csv"));
		BufferedReader br = new BufferedReader(new FileReader("res/results/betterSongResultsTest.csv"));
		BufferedWriter wr = new BufferedWriter(new FileWriter("res/results/betterSongResultsTestSongId.csv"));
		// kaggle_visible_evaluation_triplets
		// song_train_triplets
		long id = 0;
		String currentUser = "";
		int currentId = 0;
		StringBuilder builder = new StringBuilder();
		try {
			String line = br.readLine();
			wr.write("Id,Expected\n");
			line = br.readLine();
			while (line != null) {
				String[] split=line.split(",");
				builder.append(split[0]);
				builder.append(",");
				split=split[1].split(" ");
				for(int j=0;j<split.length;j++){
					if(songmap.get(split[j])==null){
						System.out.println(split[j]);
					}
					else{

						builder.append(songmap.get(split[j]));	
						builder.append(" ");
					}
				}		

				builder.append("\n");
				wr.write(builder.toString());

				line = br.readLine();
			}
		} finally {
			long elapsedTime = System.nanoTime() - start;
			System.out.println("wykonano w: " + elapsedTime);
			wr.close();
			br.close();
		}
	}
}
