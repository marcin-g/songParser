package alpha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import alpha.MapReader.ValueComparator;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class UserReader {
	static HashMap<String, LinkedList<Integer>> map = new HashMap<>();
	static HashMap<String, LinkedList<Integer>> bannedMap = new HashMap<>();
	static SortedMultiset<Integer> resultSet=null;

	public static void main(String[] args) throws Exception {
		BufferedReader br1 = new BufferedReader(new FileReader("res/1.csv"));
		BufferedReader br2 = null;
		// train_triplets
		// kaggle_visible_evaluation_triplets
		BufferedWriter wr = null;
		long start = System.currentTimeMillis();
		try {
			String line = br1.readLine();
			int i = 0;
			while (line != null) {
				String[] split = line.split(",");
				LinkedList<Integer> list = map.get(split[1]);
				if (list != null) {
					list.add(new Integer(split[2]));
				} else {
					list = new LinkedList<>();
					list.add(new Integer(split[2]));
					map.put(split[1], list);
				}
				// System.out.println(split);
				i++;
				if (i % 100000 == 0) {
					System.out.println(i);
				}
				line = br1.readLine();
			}
			br1.close();

			br1 = new BufferedReader(new FileReader(
					"res/kaggle_visible_evaluation_triplets.csv"));
			line = br1.readLine();
			i = 0;
			while (line != null) {
				String[] split = line.split(",");
				LinkedList<Integer> list = bannedMap.get(split[0]);
				if (list != null) {
					list.add(new Integer(split[1]));
				} else {
					list = new LinkedList<>();
					list.add(new Integer(split[1]));
					bannedMap.put(split[0], list);
				}
				// System.out.println(split);
				i++;
				if (i % 100000 == 0) {
					System.out.println(i);
				}
				line = br1.readLine();
			}
			br1.close();
			
			complexResults();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * i = 0; wr = new BufferedWriter(new FileWriter(
		 * "res/results/songResultsTest.csv")); StringBuilder builder = new
		 * StringBuilder(); ; for (i = 0; i <= 108000; i += 2000) { br2 = new
		 * BufferedReader(new FileReader("res/results/results" + i + ".csv"));
		 * line = br2.readLine(); while (line != null) { String[] split =
		 * line.split(";"); builder = new StringBuilder();
		 * builder.append(split[0]); builder.append(";"); int k = 0;
		 * LinkedList<Integer> bannedList = bannedMap.get(split[0]); for (int j
		 * = 1; j < split.length; j++) { LinkedList<Integer> songList =
		 * map.get(split[j]); if (songList != null) { for (Integer song :
		 * songList) { if (bannedList == null || !bannedList.contains(song)) {
		 * builder.append(song); builder.append(";"); k++; if (k == 100) {
		 * break; } } } } if (k == 100) { break; } } builder.append("\n");
		 * wr.append(builder.toString()); line = br2.readLine(); }
		 * System.out.println(i); br2.close(); } } catch (Exception e) {
		 * e.printStackTrace(); } finally { wr.close();
		 * System.out.println("koniec"); }
		 */

	}
//bierze pierwsze 100 piosenek
	public static void simpleResults() {
		BufferedWriter wr = null;
		try {
			BufferedReader br2 = null;
			String line = "";
			int i = 0;
			wr = new BufferedWriter(new FileWriter(
					"res/results/songResultsTest.csv"));
			StringBuilder builder = new StringBuilder();
			;
			for (i = 0; i <= 108000; i += 2000) {
				br2 = new BufferedReader(new FileReader("res/results/results"
						+ i + ".csv"));
				line = br2.readLine();
				while (line != null) {
					String[] split = line.split(";");
					builder = new StringBuilder();
					builder.append(split[0]);
					builder.append(";");
					int k = 0;
					LinkedList<Integer> bannedList = bannedMap.get(split[0]);
					for (int j = 1; j < split.length; j++) {
						LinkedList<Integer> songList = map.get(split[j]);
						if (songList != null) {
							for (Integer song : songList) {
								if (bannedList == null
										|| !bannedList.contains(song)) {
									builder.append(song);
									builder.append(";");
									k++;
									if (k == 100) {
										break;
									}
								}
							}
						}
						if (k == 100) {
							break;
						}
					}
					builder.append("\n");
					wr.append(builder.toString());
					line = br2.readLine();
				}
				System.out.println(i);
				br2.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("koniec");
		}
	}
	//bierze najczesciej sluchane
	public static void complexResults() {
		BufferedWriter wr = null;
		try {
			BufferedReader br2 = null;
			String line = "";
			int i = 0;
			LinkedList<Integer> bannedList;
			LinkedList<Integer> songList;
			TreeMultiset<Integer> finalSet;
			wr = new BufferedWriter(new FileWriter(
					"res/results/betterSongResultsTest.csv"));
			StringBuilder builder = new StringBuilder();
			;
			for (i = 0; i <= 108000; i += 2000) {
				br2 = new BufferedReader(new FileReader("res/results/results"
						+ i + ".csv"));
				line = br2.readLine();
				while (line != null) {
					String[] split = line.split(";");
					builder = new StringBuilder();
					builder.append(split[0]);
					builder.append(";");
					int k = 0;
					bannedList = bannedMap.get(split[0]);
					resultSet=TreeMultiset.create();
					for (int j = 1; j < split.length; j++) {
						songList = map.get(split[j]);
						if (songList != null) {
							resultSet.addAll(songList);
							
						}
					}
					finalSet=TreeMultiset.create(new ValueComparator());
					finalSet.addAll(resultSet);
					for (Integer song : finalSet.descendingMultiset()) {
						if (bannedList == null
								|| !bannedList.contains(song)) {
							builder.append(song);
							builder.append(";");
							k++;
							if (k == 100) {
								break;
							}
						}
					}
					builder.append("\n");
					wr.append(builder.toString());
					line = br2.readLine();
				}
				System.out.println(i);
				br2.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("koniec");
		}
	}
	static class ValueComparator implements Comparator<Integer> {
		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(Integer a, Integer b) {
			if (resultSet.count(a)<resultSet.count(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
