package algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import algorithm.Jaccard.Pair;
import postgres.BlechArtistBatch;

public class Blech {

	public static class Song {
		double danceability;
		double energy;
		int year;
		String album;
		double popularity = 0.0;
		double hotttnesss;
		int artist;
	}

	public static class Pair {
		int index;
		double value;

		Pair(int i, double v) {
			index = i;
			value = v;
		}
	}

	static boolean OLD = false;

	static int timesCompared = 0;
	static int timesNull = 0;
	static double sumaSuma = 0;

	static final int ARTISTS_COUNT = 30327;
	static final int TRACKS_COUNT = 384541;
	static final int SONGS_COUNT = 383833;
	static final int SONGS_MAX_ID = 386213;

	static BlechArtistBatch batch = new BlechArtistBatch();
	static ArrayList<String> artists = new ArrayList<String>();
	static HashMap<String, Integer> artistsMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> songsMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> usersMap = new HashMap<String, Integer>();
	static String[] songsHashes = new String[SONGS_MAX_ID + 1];
	static double[] songRating = new double[500];

	/*
	 * PARAMETRY - tu można zmieniać i się bawić
	 */

	static int minimumTripletListens = 1;

	static int stableSongsNumber = 0;

	static double tripletsImportanceRatio = 1.0;

	static double nullCompareValue = 0.0;

	static final double artistsTermsMinimumFreq = 0.80;
	static final double artistsTermsMinimumWeight = 0.80;

	static final double artistsMbtagsWeight = 1.0;
	static final double artistsTermsWeight = 1.0;
	static final double artistsSimilarityWeight = 1.0;

	static final double maxAlbumBonus = 2.0;
	static final double maxYearBonus = 1.0;
	static final double maxEnergyBonus = 1.0;
	static final double maxDanceabilityBonus = 1.0;
	static double maxArtistBonus = 4.0;
	static double maxHotttnesssBonus = 2.0;

	/*
	 * KONIEC PARAMETRÓW
	 */

	static final double maxFinalSongBonus = 1.0;
	static double maxStartPositionBonus = tripletsImportanceRatio * maxFinalSongBonus;

	static double maxArtistsSimilarity = artistsMbtagsWeight + artistsSimilarityWeight + artistsTermsWeight;
	static double maxSongBonus = maxAlbumBonus + maxYearBonus + maxEnergyBonus + maxDanceabilityBonus + maxArtistBonus
			+ maxHotttnesssBonus;

	static double artistsSimilarity[] = new double[(int) (((double) ARTISTS_COUNT / 2.0) * ((double) ARTISTS_COUNT - 1.0))];
	static ArrayList<String>[] mbtags = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];
	static ArrayList<String>[] terms = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];
	static ArrayList<Integer>[] userSongs = (ArrayList<Integer>[]) new ArrayList[110000];
	static ArrayList<Integer>[] userEvaluationSongs = (ArrayList<Integer>[]) new ArrayList[110000];
	static Song[] songs = new Song[SONGS_MAX_ID + 1];
	static long startTime, stopTime;

	static void loadArtists() throws SQLException {
		ResultSet rs;
		startTime = System.currentTimeMillis();
		rs = batch.loadArtists();
		int k = 0;
		while (rs.next()) {
			String artist_id = rs.getString("artist_id");
			artists.add(artist_id);
			artistsMap.put(artist_id, k);
			k++;
		}
		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem listę artystów..");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void calcParameters() throws IOException {
		maxStartPositionBonus = tripletsImportanceRatio * maxFinalSongBonus;
		maxArtistsSimilarity = artistsMbtagsWeight + artistsSimilarityWeight + artistsTermsWeight;
		maxSongBonus = maxAlbumBonus + maxYearBonus + maxEnergyBonus + maxDanceabilityBonus + maxArtistBonus
				+ maxHotttnesssBonus;
		loadTriplets();
	}

	static void loadSimilarArtists() throws SQLException {
		startTime = System.currentTimeMillis();
		ResultSet rs;
		int id1, id2;
		int k = 0;
		rs = batch.loadSimilarArtists();
		while (rs.next()) {
			String artist_id = rs.getString(1);
			String similar_artist_id = rs.getString(2);
			k++;
			if (!artistsMap.containsKey(similar_artist_id) || !artistsMap.containsKey(artist_id))
				continue;
			id1 = artistsMap.get(artist_id);
			id2 = artistsMap.get(similar_artist_id);
			int idd1 = Math.min(id1, id2);
			int idd2 = Math.max(id1, id2);
			artistsSimilarity[((idd1 + 1) * idd1) / 2 + (idd2 - idd1 - 1)] += artistsSimilarityWeight;
		}
		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem podobnych artystów..");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	private static void loadArtistsMbtags() throws SQLException {
		ResultSet rs;
		int id1, id2;
		startTime = System.currentTimeMillis();
		for (int x = 0; x < ARTISTS_COUNT; x++) {
			mbtags[x] = new ArrayList<String>();
		}

		rs = batch.loadArtistsMbtags();
		while (rs.next()) {
			String artist_id = rs.getString(1);
			if (!artistsMap.containsKey(artist_id))
				continue;
			int id = artistsMap.get(artist_id);
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

	}

	private static void loadArtistsTerms() throws SQLException {
		ResultSet rs;
		int id1, id2;
		startTime = System.currentTimeMillis();
		for (int x = 0; x < ARTISTS_COUNT; x++) {
			terms[x] = new ArrayList<String>();
		}
		rs = batch.loadArtistsTerms();
		while (rs.next()) {
			String artist_id = rs.getString(1);
			if (!artistsMap.containsKey(artist_id))
				continue;
			int id = artistsMap.get(artist_id);
			String term = rs.getString(2);
			double freq = rs.getDouble(3);
			double weight = rs.getDouble(4);
			if (freq >= artistsTermsMinimumFreq && weight >= artistsTermsMinimumWeight)
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
	}

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

	static double compareSongs(Song s1, Song s2) {
		timesCompared++;
		if (s1 == null || s2 == null) {
			timesNull++;
			return nullCompareValue;
		}

		double result = 0;
		// album
		if (s1.album.equals(s2.album))
			result += maxAlbumBonus;

		// year
		result += Math.max(0, maxYearBonus - (maxYearBonus / 5.0) * Math.abs(s1.year - s2.year) / 3);

		// danceability
		result += Math.max(0,
				maxDanceabilityBonus - (maxDanceabilityBonus / 5.0) * Math.abs(s1.danceability - s2.danceability) * 10);

		// energy
		result += Math.max(0, maxEnergyBonus - (maxEnergyBonus / 5.0) * Math.abs(s1.energy - s2.energy) * 10);

		// artist
		result += compareArtists(s1.artist, s2.artist) * maxArtistBonus;

		// hotttnesss
		result += s2.hotttnesss * maxHotttnesssBonus;

		sumaSuma += result / maxSongBonus;
		// System.out.println("Zwracam: " + result / maxSongBonus);
		return result / maxSongBonus;
	}

	static void loadSongs() throws IOException {
		startTime = System.currentTimeMillis();
		String path = "/home/kailip/EDWD/MSDC/inclass_kaggle_songs.txt";
		if (OLD)
			path = "/home/kailip/EDWD/MSDC/kaggle_songs.txt";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		while ((line = br.readLine()) != null) {
			String[] strings = line.split("\\s+");
			String hash = strings[0];
			int id = Integer.parseInt(strings[1]);
			songsHashes[id] = hash;
			songsMap.put(hash, id);
		}
		br.close();
		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem listę songów.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void loadTracks() throws SQLException {
		// for(int x=0; x<)
		ResultSet rs;
		int id1, id2;
		startTime = System.currentTimeMillis();
		rs = batch.loadTracks();
		while (rs.next()) {
			String song_id = rs.getString("song_id");
			int id = songsMap.get(song_id);
			if (songs[id] == null)
				songs[id] = new Song();
			songs[id].artist = artistsMap.get(rs.getString("artist_id"));
			songs[id].danceability = rs.getDouble("danceability");
			songs[id].energy = rs.getDouble("energy");
			songs[id].album = rs.getString("release");
			songs[id].year = rs.getInt("year");
			songs[id].hotttnesss = rs.getDouble("song_hotttnesss");

		}
		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem tracki.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void saveArtistsSimilarity() throws FileNotFoundException {
		startTime = System.currentTimeMillis();

		PrintWriter out = new PrintWriter("/home/kailip/EDWD/MSDC/artistsSimilarity.txt");
		for (int id1 = 0; id1 < ARTISTS_COUNT; id1++) {
			for (int id2 = id1 + 1; id2 < ARTISTS_COUNT; id2++) {
				int index = ((id1 + 1) * id1) / 2 + (id2 - id1 - 1);
				if (artistsSimilarity[index] != 0) {
					out.println(index + " " + artistsSimilarity[index]);
				}
			}

		}

		stopTime = System.currentTimeMillis();
		System.out.println("Zapisałem podobieństwo artystów do pliku.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");

	}

	static void loadArtistsSimilarity() throws IOException {
		startTime = System.currentTimeMillis();

		BufferedReader br = new BufferedReader(new FileReader("/home/kailip/EDWD/MSDC/artistsSimilarity.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] strings = line.split("\\s+");
			int index = Integer.parseInt(strings[0]);
			double similarity = Double.parseDouble(strings[1]);
			artistsSimilarity[index] = similarity;
		}

		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem podobieństwo artystów z pliku.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void loadUsers() throws IOException {
		startTime = System.currentTimeMillis();
		String path = "/home/kailip/EDWD/MSDC/inclass_kaggle_users.txt";
		if (OLD)
			path = "/home/kailip/EDWD/MSDC/kaggle_users.txt";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		int x = 0;
		while ((line = br.readLine()) != null) {
			userSongs[x] = new ArrayList<Integer>();
			userEvaluationSongs[x] = new ArrayList<Integer>();
			usersMap.put(line, x);
			x++;
		}

		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem listę userów z pliku.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void loadTriplets() throws IOException {
		for (int x = 0; x < 110000; x++) {
			userSongs[x].clear();
		}
		startTime = System.currentTimeMillis();
		String path = "/home/kailip/EDWD/MSDC/inclass_kaggle_visible_evaluation_triplets.txt";
		if (OLD)
			path = "/home/kailip/EDWD/MSDC/kaggle_visible_evaluation_triplets.txt";

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		int ll = 0;
		while ((line = br.readLine()) != null) {
			String[] strings = line.split("\\s+");
			String user = strings[0];
			String song = strings[1];
			int ile = Integer.parseInt(strings[2]);
			if (ile >= minimumTripletListens) {
				int userId = usersMap.get(user);
				int songId = songsMap.get(song);
				userSongs[userId].add(songId);
				ll++;
			}

		}
		// System.out.println("Wczytałem " + ll + " tripletów.");

		stopTime = System.currentTimeMillis();
		// System.out.println("Wczytałem triplety z pliku.");
		// System.out.println("  Czas: " + (stopTime - startTime) / 1000 +
		// " s.");
	}

	static void loadEvaluationData() throws IOException {
		startTime = System.currentTimeMillis();

		BufferedReader br = new BufferedReader(new FileReader("/home/kailip/EDWD/MSDC/kaggle_triplets_hidden.txt"));
		String line;
		int ll = 0;
		while ((line = br.readLine()) != null) {
			String[] strings = line.split("\\s+");
			String user = strings[0];
			String song = strings[1];
			int userId = usersMap.get(user);
			int songId = songsMap.get(song);
			userEvaluationSongs[userId].add(songId);
			ll++;
		}
		System.out.println("Wczytałem " + ll + " tripletów.");

		stopTime = System.currentTimeMillis();
		System.out.println("Wczytałem ewaluacyjne triplety z pliku.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void loadData() {
		try {
			loadArtists();
			loadSimilarArtists();
			// loadArtistsMbtags();
			// loadArtistsTerms();
			// saveArtistsSimilarity();
			// loadArtistsSimilarity();
			loadSongs();
			loadTracks();
			loadUsers();
			loadEvaluationData();
			loadTriplets();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static double countAP(int userId, ArrayList<Integer> topSongs) {
		double result = 0;
		int j = 0;
		for (int x = 0; x < topSongs.size(); x++) {
			int song = topSongs.get(x);
			if (userEvaluationSongs[userId].contains(song)) {
				j++;
				result += (double) j / (double) (x + 1.0);
			}

		}
		return result / (double) userEvaluationSongs[userId].size();
	}

	private static void doEverything() throws IOException {
		startTime = System.currentTimeMillis();
		PrintWriter out = new PrintWriter("/home/kailip/EDWD/MSDC/new.csv");
		// out.println("Id,Expected");
		String path = "/home/kailip/EDWD/MSDC/currentBest.csv";
		if (OLD)
			path = "/home/kailip/EDWD/MSDC/betterSongResultsTestKaggle.csv";
		// path = "/home/kailip/EDWD/MSDC/output2.csv";
		BufferedReader br = new BufferedReader(new FileReader(path));
		// BufferedReader br = new BufferedReader(new
		// FileReader("/home/kailip/EDWD/MSDC/inclass.csv"));
		String line;
		br.readLine();
		double sumaDlaMapa = 0;
		int ll = 0;
		while ((line = br.readLine()) != null) {
			ll++;
			line = line.replace(",", " ");
			ArrayList<Integer> topSongs = new ArrayList<Integer>();
			String[] strings = line.split("\\s+");
			int userId = usersMap.get(strings[0]);
			// out.print(strings[0] + ",");
			int topSongsCount = strings.length - 1;
			for (int x = 0; x < topSongsCount; x++) {
				songRating[x] = maxStartPositionBonus
						* (((double) topSongsCount - (double) x) / (double) topSongsCount);
				if (x < stableSongsNumber) {
					// System.out.println("Zwiększam!");
					songRating[x] = songRating[x] * 100 + 10000;
				}
				topSongs.add(Integer.parseInt(strings[x + 1]));
			}
			for (int x = 0; x < userSongs[userId].size(); x++) {
				calculateSongsForSong(userSongs[userId].get(x), topSongs);
			}
			PriorityQueue<Pair> Q = new PriorityQueue<Pair>(500, new Comparator<Pair>() {
				@Override
				public int compare(Pair o1, Pair o2) {
					return ((Double) o2.value).compareTo((double) o1.value);
				}
			});

			for (int x = 0; x < topSongsCount; x++) {
				Q.add(new Pair(topSongs.get(x), songRating[x]));
			}

			ArrayList<Integer> topSongsForMAP = new ArrayList<Integer>();

			for (int x = 0; x < topSongsCount; x++) {
				Pair pair = Q.poll();
				topSongsForMAP.add(pair.index);
				// if (x < 5)
				// System.out.println("Best score: " + pair.value);
				// out.print(pair.index);
				// if (x < topSongsCount - 1)
				// out.print(" ");
			}

			sumaDlaMapa += countAP(userId, topSongsForMAP);
			out.println();
		}
		out.close();
		double MAP = sumaDlaMapa / 110000.0;

		// System.out.println("TimesCompared: " + timesCompared);
		// System.out.println("TimesNull:     " + timesNull);
		// System.out.println("Średnia:       " + (sumaSuma) / (double)
		// (timesCompared - timesNull));
		stopTime = System.currentTimeMillis();
		System.out.println("[" + tripletsImportanceRatio + ", " + minimumTripletListens + ", " + stableSongsNumber
				+ "]: " + MAP + "    | " + (stopTime - startTime) / 1000 + " s");
		// System.out.println("Skończyłem robić wszystko.");
		// System.out.println("  Czas: " + (stopTime - startTime) / 1000 +
		// " s.");
	}

	public static void doSomethingElse() throws IOException {
		startTime = System.currentTimeMillis();
		PrintWriter out = new PrintWriter("/home/kailip/EDWD/MSDC/newnew.csv");
		out.println("Id,Expected");
		BufferedReader br = new BufferedReader(new FileReader("/home/kailip/EDWD/MSDC/jaccardSongResultsTestSong.csv"));
		BufferedReader br2 = new BufferedReader(new FileReader("/home/kailip/EDWD/MSDC/inclass.csv"));
		String line, line2;
		br.readLine();
		br2.readLine();
		int ll = 0;
		while ((line = br.readLine()) != null) {
			ll++;
			line2 = br2.readLine();
			line = line.replace(",", " ");
			line2 = line2.replace(",", " ");

			ArrayList<Integer> topSongs = new ArrayList<Integer>();
			String[] strings = line.split("\\s+");
			int userId = usersMap.get(strings[0]);
			out.print(strings[0] + ",");
			int topSongsCount = strings.length - 1;
			for (int x = 0; x < topSongsCount; x++) {
				topSongs.add(Integer.parseInt(strings[x + 1]));
			}
			if (topSongs.size() < 500) {
				String[] strings2 = line2.split("\\s+");
				int xx = 0;
				while (topSongs.size() < 500) {
					xx++;
					int song = Integer.parseInt(strings2[xx]);
					if (topSongs.contains(song))
						continue;
					topSongs.add(song);
				}
			}

			for (int x = 0; x < topSongs.size(); x++) {
				out.print(topSongs.get(x));
				if (x < topSongs.size() - 1)
					out.print(" ");
			}
			out.println();
		}
		out.close();

		System.out.println("TimesCompared: " + timesCompared);
		System.out.println("TimesNull:     " + timesNull);
		System.out.println("Średnia:       " + (sumaSuma) / (double) (timesCompared - timesNull));
		stopTime = System.currentTimeMillis();
		System.out.println("Skończyłem robić wszystko.");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	private static void calculateSongsForSong(int songId, ArrayList<Integer> topSongs) {
		for (int x = 0; x < topSongs.size(); x++) {
			double cmp = compareSongs(songs[songId], songs[topSongs.get(x)]) * maxFinalSongBonus;
			cmp += maxStartPositionBonus * (((double) topSongs.size() - (double) x) / (double) topSongs.size());
			if (cmp > songRating[x])
				songRating[x] = cmp;
			// System.out.println("Porównanie " + songId + " z " +
			// topSongs.get(x) + ":");
			// System.out.println("  wynik: " + cmp);
		}

	}

	public static void main(String[] args) throws Exception {
		loadData();
		System.out.println();
		for (double x = 1.0; x <= 1; x += 1.0) {
			tripletsImportanceRatio = 3.0;
			stableSongsNumber = 12;// * 20;
			minimumTripletListens = 1;
			nullCompareValue = 0.5;
			// maxArtistBonus = (double) x * 0.4;
			// maxHotttnesssBonus = x;
			calcParameters();
			doEverything();

		}
		// doEverything();
		// doSomethingElse();

		// calculateSongsForSong("SOGPNGN12A8C143969", al);
		// System.out.println("Unikalnych artystów: " + artists.size());
		// System.out.println("Artysta nr 6: " + artists.get(6));
		// System.out.println("Czy się zgadza.. : " +
		// int id = map.get("ARPG3UX1187B9ADD76");
		// int sum = 0;
		// System.out.println("Suma: " + sum);
	}
}
