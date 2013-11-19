package algorithm;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.PriorityQueue;

import postgres.JaccardBatch;

public class Jaccard {
	static int USER_COUNT = 100;
	static int[][] M = new int[USER_COUNT][100];
	static double[] J = new double[USER_COUNT];
	static JaccardBatch batch = new JaccardBatch();

	// loads data, fills table M
	static void loadData() {
		ResultSet rs;
		try {
			rs = batch.loadRecords();
			// System.out.println("Ilość rekordów: " + rs.getRow());
			int k = 0;
			while (rs.next()) {
				// System.out.println(rs.getInt(1));
				for (int x = 0; x < 100; x++) {
					M[k][x] = rs.getInt(x + 2);
					// System.out.println("Robię: "+M[k][x]);
				}

				k++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		loadData();
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		System.out.println("Wczytywanie danych: " + (elapsedTime / 1000000) + "ms");

		startTime = System.nanoTime();
		countJaccard();
		stopTime = System.nanoTime();
		elapsedTime = stopTime - startTime;
		System.out.println("Obliczanie Jaccarda: " + (elapsedTime / 1000000) + "ms");

		// printResults();
	}

	private static void printResults() {
		// TODO Auto-generated method stub
	}

	private static void countJaccard() {
		// TODO Auto-generated method stub
		for (int x = 0; x < 100; x++) {
			countJaccard(x);
		}
	}

	public static class Pair {
		int index;
		double value;

		Pair(int i, double v) {
			index = i;
			value = v;
		}
	}

	private static void countJaccard(int i) {
		int myCount = 0;
		for (int x = 0; x < 100; x++) {
			if (M[i][x] == 1)
				myCount++;
		}
		// System.out.println("myCount: " + myCount);

		PriorityQueue<Pair> Q = new PriorityQueue<Pair>(USER_COUNT, new Comparator<Pair>() {
			@Override
			public int compare(Pair o1, Pair o2) {
				return ((Double) o2.value).compareTo((double) o1.value);
			}
		});

		for (int j = 0; j < 100; j++) {

			int hisCount = 0;
			int ourCount = 0;
			for (int x = 0; x < 100; x++) {
				if (M[j][x] == 1) {
					hisCount++;
					if (M[i][x] == 1)
						ourCount++;
				}
			}
			J[j] = (double) ourCount / (double) (myCount + hisCount - ourCount);
			// System.out.println("Współczynnik Jaccarda = " + J[j]);
			Q.add(new Pair(j, J[j]));
		}

		Pair pair;
		pair = Q.poll();
		pair = Q.poll();
		Q.clear();
		// System.out.println("Najbliższy sąsiad dla " + i + ": " + pair.index +
		// " (" + pair.value + ")");
		// while ((pair = Q.poll()) != null) {
		// System.out.println(pair.index+" ... "+pair.value);
		// }

	}
}
