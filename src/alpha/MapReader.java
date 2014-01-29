package alpha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.collect.SortedMultiset;

public class MapReader {

	static SortedMultiset<Integer> resultSet = null;
	private static int[] userSongCount = new int[1129319];
	private static List<Integer>[] userSong = (LinkedList<Integer>[]) new LinkedList<?>[385372];
	private static List<Integer>[] input = (LinkedList<Integer>[]) new LinkedList<?>[110000];
	private static String[] inputId=new String[110000];
	private static void incrementValue(Map<Integer, Integer> counters,
			Integer toAdd) {
		Integer currValue = counters.get(toAdd);
		if (currValue == null)
			counters.put(toAdd, 1);
		else
			counters.put(toAdd, currValue + 1);
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br1 = null;
		BufferedReader br2 = null;

		br1 = new BufferedReader(new FileReader("res/kaggle_triplets.csv"));
		// train_triplets
		// kaggle_visible_evaluation_triplets
		int songCount = 0;
		long start = System.currentTimeMillis();
		String line = br1.readLine();
		String currentUser = "0";
		while (line != null) {
			String[] split = line.split(";");
			if (!currentUser.equals(split[1])) {
				userSongCount[Integer.parseInt(currentUser)] = songCount;
				songCount = 1;
				currentUser = split[1];
			} else {
				songCount++;
			}
			line = br1.readLine();
		}
		br1.close();

		// train_triplets
		// kaggle_visible_evaluation_triplets
		BufferedWriter wr = null;
		start = System.currentTimeMillis();
		try {
			br1 = new BufferedReader(new FileReader("res/new_map.csv"));
			line = br1.readLine();
			int j = 0;
			int maxSong = -1;
			while (line != null) {
				String[] split = line.split(";");
				int songid = Integer.parseInt(split[0]);
				List<Integer> list = new LinkedList<>();
				for (int i = 1; i < split.length; i++) {
					list.add(Integer.parseInt(split[i]));
				}
				userSong[songid] = list;
				// System.out.println(split);
				j++;
				if (j % 100000 == 0) {
					System.out.println(j);
				}
				line = br1.readLine();
			}
			System.out.println(maxSong);

			System.out.println("mape wykonano "
					+ (System.currentTimeMillis() - start));
			br1.close();

			br2 = new BufferedReader(new FileReader(
					"res/inclass_kaggle_visible_evaluation_triplets_sorted.csv"));
			line = br2.readLine();
			currentUser = "";

			HashMap<Integer, Integer> resultMap = null;
			j = 0;

			int userTestSongCount = 0;
			wr = new BufferedWriter(new FileWriter("res/jaccardProper80Result.csv"));

			List<Integer> tmpList = new LinkedList<>();
			int counterUser = 0;
			currentUser = line.split(",")[0];
			while (line != null) {
				String[] split = line.split(",");
				if (!currentUser.equals(split[0])) {
					input[counterUser] = tmpList;
					inputId[counterUser]=currentUser;
					tmpList = new LinkedList<>();
					counterUser++;
					currentUser = split[0];
				} else {
					tmpList.add(Integer.parseInt(split[1]));
				}
				line = br2.readLine();
			}
			input[counterUser] = tmpList;
			inputId[counterUser]=currentUser;

			start = System.currentTimeMillis();
			long tmpTime = System.currentTimeMillis();
			for (int i = 0; i < input.length; i++) {
				PriorityQueue<JaccardRecord> queue = new PriorityQueue<JaccardRecord>(
						80, new Comparator<JaccardRecord>() {
							@Override
							public int compare(JaccardRecord o1,
									JaccardRecord o2) {
								if (o1.jaccard < o2.jaccard)
									return 1;
								if (o1.jaccard > o2.jaccard)
									return -1;
								if (o1.id > o2.id)
									return 1;
								if (o1.id < o2.id)
									return -1;
								return 0;
							}
						});
				List<Integer> list = input[i];
				resultMap = new HashMap<>();
				if (list != null) {
					for (Integer integer : list) {
						List<Integer> songs = userSong[integer];
						if (songs != null) {
							for (Integer song : songs) {
								incrementValue(resultMap, song);
							}
						} 
					}

				} else {
					System.out.println(i);
				}

				for (Integer result : resultMap.keySet()) {
					queue.add(new JaccardRecord(
							((double) resultMap.get(result))
									/ (double) (list.size() + userSongCount[result]-resultMap.get(result)),
							result));
				}
				// System.out.println(i);
				StringBuilder builder = new StringBuilder();
				builder.append(inputId[i]);
				builder.append(";");
				for (int k = 0; k < 80; k++) {
					JaccardRecord rec = queue.poll();
					if (rec != null) {
					//	System.out.println(rec.jaccard);
						builder.append(rec.id);
						builder.append(";");
					}
				}
				builder.append("\n");
				wr.write(builder.toString());
				if(i%1000==0){
					long current=System.currentTimeMillis();
					System.out.println(i+" "+(current - start)
							/ (double) 1000+" ostatnie 1000 "+(current - tmpTime)
							/ (double) 1000);
					tmpTime=current;
				}
			}
			

			/*
			 * while (line != null) { String[] split = line.split(","); if
			 * (!split[0].equals(currentUser)) { if (resultMap != null) {
			 * 
			 * /* ValueComparator bvc = new ValueComparator(resultMap);
			 * TreeMap<String, Integer> sorted_map = new TreeMap<String,
			 * Integer>( bvc); sorted_map.putAll(resultMap);
			 */

			/*
			 * PriorityQueue<JaccardRecord> queue = new
			 * PriorityQueue<JaccardRecord>( 500, new
			 * Comparator<JaccardRecord>() {
			 * 
			 * @Override public int compare(JaccardRecord o1, JaccardRecord o2)
			 * { if (o1.jaccard > o2.jaccard) return 1; if (o1.jaccard <
			 * o2.jaccard) return -1; if (o1.id > o2.id) return 1; if (o1.id <
			 * o2.id) return -1; return 0; } });
			 * 
			 * for (Integer result : resultMap.keySet()) { queue.add(new
			 * JaccardRecord( ((double) resultMap.get(result)) / (double)
			 * (userTestSongCount + userSongCount[result]), result)); }
			 * 
			 * StringBuilder builder = new StringBuilder();
			 * builder.append(currentUser); builder.append(";"); int i = 0; int
			 * currentKey = -1; int count = -1;
			 * 
			 * for (JaccardRecord key : queue) { builder.append(key.id);
			 * builder.append(";"); } builder.append("\n");
			 * wr.append(builder.toString()); j++; if(j%1000==0){
			 * System.out.println
			 * ((System.currentTimeMillis()-start)/(double)1000); }
			 * 
			 * } userTestSongCount = 0; resultMap = new HashMap<>(); currentUser
			 * = split[0]; } userTestSongCount++; List<Integer> list =
			 * userSong[Integer.parseInt(split[1])]; if (list != null) { for
			 * (Integer integer : list) { incrementValue(resultMap, integer); }
			 * }
			 * 
			 * line = br2.readLine(); }
			 */
			wr.close();
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ValueComparator implements Comparator<Integer> {
		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Integer a, Integer b) {
			if (resultSet.count(a) < resultSet.count(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
