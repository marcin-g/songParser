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

import com.google.common.collect.Multisets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class MapReader {

	static SortedMultiset<Integer> resultSet=null;
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
					list.add(Integer.parseInt(split[i]));
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
					"res/one.csv"));
			line = br2.readLine();
			String currentUser = "";
			
			HashMap<String, Integer> resultMap = null;
			j = 0;
			wr = new BufferedWriter(new FileWriter("res/resultsOne"+j+".csv"));
			start=System.currentTimeMillis();
			while (line != null) {
				String[] split = line.split(",");
				if (!split[0].equals(currentUser)) {
					if (resultSet != null) {

						/*ValueComparator bvc = new ValueComparator(resultMap);
						TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(
								bvc);
						sorted_map.putAll(resultMap);*/
						StringBuilder builder=new StringBuilder();
						builder.append(currentUser);
						builder.append(";");
						int i=0;
						TreeMultiset<Integer> finalSet=TreeMultiset.create(new ValueComparator());
						finalSet.addAll(resultSet);
						int currentKey=-1;
						int count=-1;
						for (Integer key : finalSet.descendingMultiset()) {
							if(key!=currentKey){
								builder.append(key);
							/*	builder.append("-");
								builder.append(count);*/
								builder.append(";");
								i++;
								currentKey=key;
								count=0;
							}
							if(i>=100) break;
							count++;
						}
						builder.append("\n");
						wr.append(builder.toString());
						j++;
						if(j%2000==0){

							System.out.println("Obliczono "+j+" w "+(System.currentTimeMillis()-start));
							
							wr.close();
							wr = new BufferedWriter(new FileWriter("res/results"+j+".csv"));
							//break;
						}
						/*System.out.println(currentUser + ";"
								+ resultMap.toString() + "\n");*/
					}
					//resultMap = new HashMap<>();
					resultSet=TreeMultiset.create();
					currentUser = split[0];
				}
				LinkedList<Integer> list = map.get(split[1]);
				if (list != null) {
					resultSet.addAll(list);
				/*	for (int i = 1; i < list.size(); i++) {
						//incrementValue(resultMap, list.get(i).toString());
					}
*/
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
