package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class dupa {
	public static void main(String[] args) throws IOException {
		BufferedReader br = null;
		BufferedWriter wr = null;
		try {
			br = new BufferedReader(new FileReader(
					"res/tmp_ranking.csv"));
			wr = new BufferedWriter(new FileWriter(
					"res/query.sql"));
			// kaggle_visible_evaluation_triplets
			// song_train_triplets
			String[] id=new String[100];
			String line = br.readLine();
			int i=0;
			while (line != null) {
				id[i]=line;
				line = br.readLine();
				i++;
			}
			for(int j=0;j<100;j++){
				for(int k=j;k<100;k++){
					if(j!=k)
					wr.write("SELECT id_"+id[j]+", id_"+id[k]+" from proper_report_map group by id_"+id[j]+", id_"+id[k]+";\n");
				}
			}
		} finally {
			System.out.println("koniec");
			wr.close();
			br.close();
		}
	}
}
