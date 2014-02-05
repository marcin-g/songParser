package alpha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;

import postgres.KaggleSongBatch;

public class MapConverter {

	public static void main(String[] args) throws Exception {
		BufferedReader br1 = new BufferedReader(new FileReader(
				"res/report/100ktrain_triplets.csv"));
		// train_triplets
		// kaggle_visible_evaluation_triplets
		HashMap<String, LinkedList<Integer>> map=new HashMap<>();
		BufferedWriter wr = null;
		long start = System.currentTimeMillis();
		try {
			String line = br1.readLine();
			int i=0;
			while (line != null) {
				String[] split = line.split(",");
				LinkedList<Integer> list=map.get(split[2]);
				if(list!=null){
					list.add(new Integer(split[1]));
				}
				else{
					list=new LinkedList<>();
					list.add(new Integer(split[1]));
					map.put(split[2], list);
				}
				// System.out.println(split);
				i++;
				if(i%100000==0){
					System.out.println(i);
				}
				line = br1.readLine();
			}
			

			System.out.println("mape wykonano "+(System.currentTimeMillis()-start));
			wr = new BufferedWriter(new FileWriter(
					"res/report/100knew_map.csv"));
			System.out.println("mape i zapis wykonano "+(System.currentTimeMillis()-start));
			for (String key : map.keySet()) {
				LinkedList<Integer> list=map.get(key);
				StringBuilder builder=new StringBuilder();
				builder.append(key);
				for (Integer integer : list) {
					builder.append(";");
					builder.append(integer.intValue());
				}
				builder.append("\n");
				wr.write(builder.toString());
			}
			
			
		} 
		catch(Exception e){
		 e.printStackTrace();
		}
		finally {
			br1.close();
			wr.close();
		}
	}
}
