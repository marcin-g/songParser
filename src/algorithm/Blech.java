package algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import postgres.BlechArtistBatch;

public class Blech {

	public static class Song {
		double danceability;
		double energy;
		int year;
		String album;
		double popularity = 0.0;
		double hotttnesss;
		String artist;
	}

	static final int ARTISTS_COUNT = 30327;
	static final int TRACKS_COUNT = 384541;
	static final int SONGS_COUNT = 383833;
	static final int SONGS_MAX_ID = 386213;

	static BlechArtistBatch batch = new BlechArtistBatch();
	static ArrayList<String> artists = new ArrayList<String>();
	static HashMap<String, Integer> artistsMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> songsMap = new HashMap<String, Integer>();
	static String[] songsHashes = new String[SONGS_MAX_ID + 1];

	static final double artistsTermsMinimumFreq = 0.5;
	static final double artistsTermsMinimumWeight = 0.5;

	static final double artistsMbtagsWeight = 5.0;
	static final double artistsSimilarityWeight = 5.0;
	static final double artistsTermsWeight = 5.0;

	static final double sameAlbumWeight = 5.0;

	static final double maxArtistsSimilarity = 15.0;

	static double artistsSimilarity[] = new double[(int) (((double) ARTISTS_COUNT / 2.0) * ((double) ARTISTS_COUNT - 1.0))];
	static ArrayList<String>[] mbtags = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];
	static ArrayList<String>[] terms = (ArrayList<String>[]) new ArrayList[ARTISTS_COUNT];
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
		System.out.println("Wczytałem artystów..");
		System.out.println("  Czas: " + (stopTime - startTime) / 1000 + " s.");
	}

	static void loadSimilarArtists() throws SQLException {
		ResultSet rs;
		int id1, id2;
		startTime = System.currentTimeMillis();
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
		if (s1 == null || s2 == null)
			return 0;
		double result = 0;
		// album
		if (s1.album.equals(s2.album))
			result += sameAlbumWeight;

		// year
		result += Math.max(0, 5 - Math.abs(s1.year - s2.year) / 3);

		// danceability
		result += Math.max(0, 5 - Math.abs(s1.danceability - s2.danceability) * 10);

		// energy
		result += Math.max(0, 5 - Math.abs(s1.energy - s2.energy) * 10);

		// artist
		result += compareArtists(artistsMap.get(s1.artist), artistsMap.get(s2.artist));

		// hotttnesss
		result += s2.hotttnesss * 5;

		return result;
	}

	static void loadSongs() throws IOException {
		startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader("/home/kailip/EDWD/MSDC/inclass_kaggle_songs.txt"));
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
		System.out.println("Wczytałem songi.");
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
			songs[id].artist = rs.getString("artist_id");
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

	static void loadData() {
		try {
			loadArtists();
			loadSimilarArtists();
			// loadArtistsMbtags();
			// loadArtistsTerms();
			loadSongs();
			loadTracks();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void calculateSongsForSong(String songId, ArrayList<Integer> topSongs) {
		int id = songsMap.get(songId);
		for (int x = 0; x < topSongs.size(); x++) {
			double cmp = compareSongs(songs[id], songs[topSongs.get(x)]);
			cmp += (500 - x) / 20;
			System.out.println("Porównanie " + id + " z " + topSongs.get(x) + ":");
			System.out.println("  wynik: " + cmp);
		}

	}

	public static void main(String[] args) throws Exception {
		loadData();

		ArrayList<Integer> al = new ArrayList<Integer>();
		al.add(91177);
		al.add(12985);

		calculateSongsForSong("SOGPNGN12A8C143969", al);
		// System.out.println("Unikalnych artystów: " + artists.size());
		// System.out.println("Artysta nr 6: " + artists.get(6));
		// System.out.println("Czy się zgadza.. : " +
		// int id = map.get("ARPG3UX1187B9ADD76");
		// int sum = 0;
		// System.out.println("Suma: " + sum);
	}
}
