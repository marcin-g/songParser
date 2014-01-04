package alpha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class MapReader {

	private static void incrementValue(Map<String, Integer> counters,
			String toAdd) {
		Integer currValue = counters.get(toAdd);
		if (currValue == null)
			counters.put(toAdd, 1);
		else
			counters.put(toAdd, currValue + 1);
	}

	public static void main(String[] args) {
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		// train_triplets
		// kaggle_visible_evaluation_triplets
		HashMap<String, LinkedList<Integer>> map = new HashMap<>();
		BufferedWriter wr = null;
		long start = System.currentTimeMillis();
		try {
			br1 = new BufferedReader(new FileReader("res/new_map.csv"));
			String line = br1.readLine();
			int j = 0;
			while (line != null) {
				String[] split = line.split(";");
				LinkedList<Integer> list = new LinkedList<>();
				for (int i = 1; i < split.length; i++) {
					list.add(new Integer(split[i]));
				}
				map.put(split[0], list);

				// System.out.println(split);
				j++;
				if (j % 100000 == 0) {
					System.out.println(j);
				}
				line = br1.readLine();
			}

			System.out.println("mape wykonano "
					+ (System.currentTimeMillis() - start));
			br1.close();

			br2 = new BufferedReader(new FileReader(
					"res/kaggle_visible_evaluation_triplets_new.csv"));
			line = br2.readLine();
			String currentUser = "";
			
			HashMap<String, Integer> resultMap = null;
			wr = new BufferedWriter(new FileWriter("res/results.csv"));
			j = 0;
			while (line != null) {
				String[] split = line.split(",");
				if (!split[0].equals(currentUser)) {
					if (resultMap != null) {

						ValueComparator bvc = new ValueComparator(resultMap);
						TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(
								bvc);
						sorted_map.putAll(resultMap);
						StringBuilder builder=new StringBuilder();
						builder.append(currentUser);
						builder.append(";");
						int i=0;
						for (String key : sorted_map.descendingKeySet()) {
							builder.append(key);
							builder.append(";");
							if(++i>=50) break;
						}
						wr.append(builder.toString());
						j++;
						System.out.println(j);
						/*System.out.println(currentUser + ";"
								+ resultMap.toString() + "\n");*/
					}
					resultMap = new HashMap<>();
					currentUser = split[0];
				}
				LinkedList<Integer> list = map.get(split[1]);
				if (list != null) {
					for (int i = 1; i < list.size(); i++) {
						//incrementValue(resultMap, list.get(i).toString());
					}

				}
				// System.out.println(split);
				/*j++;
				if (j % 1000 == 0) {
					System.out.println(j);
				}*/
				line = br2.readLine();
			}
			br2.close();
			wr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;

		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
