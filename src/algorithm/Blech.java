package algorithm;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import postgres.BlechArtistBatch;

public class Blech {
	static BlechArtistBatch batch = new BlechArtistBatch();
	static ArrayList<String> artists = new ArrayList<String>();
	static HashMap<String, Integer> map = new HashMap<String, Integer>();

	static final int ARTISTS_COUNT = 30327;
	static final int TRACKS_COUNT = 384541;
	static final int SONGS_COUNT = 383833;

	// static final double similarArtistWeight = 5.0;
	static final double artistsMbtagsWeight = 5.0;
	static final double artistsSimilarityWeight = 5.0;
	static final double artistsTermsWeight = 5.0;

	static final double maxArtistsSimilarity = 15.0;

	// static int sim[][] = new int[ARTISTS_COUNT][ARTISTS_COUNT];

	static double artistsSimilarity[] = new double[(int) (((double) ARTISTS_COUNT / 2.0) * ((double) ARTISTS_COUNT - 1.0))];
	static ArrayList<String>[] mbtags = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];
	static ArrayList<String>[] terms = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];

	static double compareArtists(int id1, int id2) {
		if (id1 == id2)
			return 1.0;
		if (id1 > id2) {
			int tmp = id1;
			id1 = id2;
			id2 = tmp;
		}
		int index = ((id1 + 1) * id1) / 2 + (id2 - id1 - 1);
		return artistsSimilarity[index] / maxArtistsSimilarity;
	}

	static void loadData() {
		ResultSet rs;
		int id1, id2;

		long startTime, stopTime;

		try {
			startTime = System.currentTimeMillis();
			rs = batch.loadArtists();
			int k = 0;
			while (rs.next()) {
				String artist_id = rs.getString("artist_id");
				artists.add(artist_id);
				map.put(artist_id, k);
				k++;
			}
			stopTime = System.currentTimeMillis();
			System.out.println("Wczytałem artystów..");
			System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");

			startTime = System.currentTimeMillis();
			k = 0;
			rs = batch.loadSimilarArtists();
			while (rs.next()) {
				String artist_id = rs.getString(1);
				String similar_artist_id = rs.getString(2);
				k++;
				if (!map.containsKey(similar_artist_id) || !map.containsKey(artist_id))
					continue;
				id1 = map.get(artist_id);
				id2 = map.get(similar_artist_id);
				// sim[id1][id2] = 1;
				// sim[id2][id1] = 1;
				int idd1 = Math.min(id1, id2);
				int idd2 = Math.max(id1, id2);
				artistsSimilarity[((idd1 + 1) * idd1) / 2 + (idd2 - idd1 - 1)] += artistsSimilarityWeight;
			}
			stopTime = System.currentTimeMillis();
			System.out.println("Wczytałem podobnych artystów..");
			System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");

			startTime = System.currentTimeMillis();
			for (int x = 0; x < ARTISTS_COUNT; x++) {
				mbtags[x] = new ArrayList<String>();
			}

			rs = batch.loadArtistsMbtags();
			while (rs.next()) {
				String artist_id = rs.getString(1);
				if (!map.containsKey(artist_id))
					continue;
				int id = map.get(artist_id);
				String mbtag = rs.getString(2);
				mbtags[id].add(mbtag);
			}

			for (int x = 0; x < ARTISTS_COUNT - 1; x++) {
				for (int y = x + 1; y < ARTISTS_COUNT; y++) {
					int common = 0;
					int c1 = mbtags[x].size();
					int c2 = mbtags[y].size();
					for (int xx = 0; xx < c1; xx++) {
						String mbtag = mbtags[x].get(xx);
						if (mbtags[y].contains(mbtag))
							common++;
					}

					double jaccard = (double) common / (double) (c1 + c2 - common);
					if (Double.isNaN(jaccard))
						jaccard = 0;

					id1 = x;
					id2 = y;
					// if (jaccard != 0)
					// System.out.println(" Jaccard dla " + x + " i " + y + ": "
					// + jaccard);
					artistsSimilarity[((id1 + 1) * id1) / 2 + (id2 - id1 - 1)] += jaccard * artistsMbtagsWeight;
				}
			}
			stopTime = System.currentTimeMillis();
			System.out.println("Obliczyłem podobieństwo mbtagów.");
			System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");

			startTime = System.currentTimeMillis();
			for (int x = 0; x < ARTISTS_COUNT; x++) {
				terms[x] = new ArrayList<String>();
			}
			rs = batch.loadArtistsTerms();
			while (rs.next()) {
				String artist_id = rs.getString(1);
				if (!map.containsKey(artist_id))
					continue;
				int id = map.get(artist_id);
				String term = rs.getString(2);
				double freq = rs.getDouble(3);
				double weight = rs.getDouble(4);
				if (freq >= 0.5 && weight >= 0.5)
					terms[id].add(term);
			}

			for (int x = 0; x < ARTISTS_COUNT - 1; x++) {
				for (int y = x + 1; y < ARTISTS_COUNT; y++) {
					int common = 0;
					int c1 = terms[x].size();
					int c2 = terms[y].size();
					for (int xx = 0; xx < c1; xx++) {
						String term = terms[x].get(xx);
						if (terms[y].contains(term))
							common++;
					}

					double jaccard = (double) common / (double) (c1 + c2 - common);
					if (Double.isNaN(jaccard))
						jaccard = 0;

					id1 = x;
					id2 = y;
					// if (jaccard != 0)
					// System.out.println(" Jaccard dla " + x + " i " + y + ": "
					// + jaccard);
					artistsSimilarity[((id1 + 1) * id1) / 2 + (id2 - id1 - 1)] += jaccard * artistsTermsWeight;
				}
			}
			stopTime = System.currentTimeMillis();
			System.out.println("Obliczyłem podobieństwo termów.");
			System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void main(String[] args) throws Exception {
		loadData();
		System.out.println("Unikalnych artystów: " + artists.size());
		// System.out.println("Artysta nr 6: " + artists.get(6));
		// System.out.println("Czy się zgadza.. : " +
		// int id = map.get("ARPG3UX1187B9ADD76");
		// int sum = 0;
		// System.out.println("Suma: " + sum);
	}
}
