package algorithm;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import postgres.BlechArtistBatch;

public class Blech {
	static BlechArtistBatch batch = new BlechArtistBatch();
	static ArrayList<String> artists = new ArrayList<String>();
	static HashMap<String, Integer> map = new HashMap<String, Integer>();

	static int sim[][] = new int[30327][30327];

	static void loadData() {
		ResultSet rs, rs2;
		int id1, id2;

		try {
			rs = batch.loadArtists();
			int k = 0;
			while (rs.next()) {
				String artist_id = rs.getString("artist_id");
				artists.add(artist_id);
				map.put(artist_id, k);
				k++;
			}
			System.out.println("Wczytałem artystów..");

			k = 0;
			rs2 = batch.loadSimilarArtists();
			while (rs2.next()) {
				String artist_id = rs2.getString(1);
				String similar_artist_id = rs2.getString(2);
				k++;
				if (!map.containsKey(similar_artist_id) || !map.containsKey(artist_id))
					continue;
				id1 = map.get(artist_id);
				id2 = map.get(similar_artist_id);
				sim[id1][id2] = 1;
				sim[id2][id1] = 1;

			}
			System.out.println("Wczytałem podobnych artystów..");
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
		int id = map.get("ARPG3UX1187B9ADD76");
		int sum = 0;
		for (int x = 0; x < 30327; x++) {
			sum += sim[0][x];
		}
		System.out.println("Suma: " + sum);
	}
}
