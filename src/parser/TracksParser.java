package parser;

import java.io.BufferedReader;
import java.io.FileReader;

import ncsa.hdf.object.h5.H5File;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.GetterOnlyReflection;

import postgres.ArtistMbtagsBatch;
import postgres.ArtistTermsBatch;
import postgres.SimilarArtistsBatch;
import postgres.SongBatch;

public class TracksParser {
	public static hdf5_getters getter = new hdf5_getters();
	public static int ile = 0;
	public static long elapsedH5 = 0;
	public static long elapsedSQL = 0;
	public static int ile_all = 0;
	public static SongBatch batch = new SongBatch();
	public static SimilarArtistsBatch SAbatch = new SimilarArtistsBatch();
	public static ArtistMbtagsBatch AMbatch = new ArtistMbtagsBatch();
	public static ArtistTermsBatch ATbatch = new ArtistTermsBatch();

	public static void main(String[] args) throws Exception {
		long start = System.nanoTime();
		// BufferedReader br = new BufferedReader(new
		// FileReader("/home/kailip/EDWD/MSDC/taste_profile_subset.txt"));
		BufferedReader br = new BufferedReader(
				new FileReader("/home/kailip/EDWD/MSDC/taste_profile_song_to_tracks.txt"));
		String DataPath = "/home/kailip/EDWD/MSDC/millionsong/data/";
		try {
			String line = br.readLine();
			// while (line != null && ile_all < 1000) {
			while (line != null) {
				// System.out.println("Line..");
				String[] split = line.split(" ");
				if (split.length > 1) {
					ile_all++;
					for (int x = 1; x < split.length; x++) {
						// System.out.println(split[x]);
						String path = getPath(split[x]);
						// System.out.println(path);
						if (path.charAt(0) == 'A')
							doSomething(DataPath + path);
					}
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			long startSQL = System.nanoTime();
			batch.close();
			SAbatch.close();
			AMbatch.close();
			ATbatch.close();
			long stopSQL = System.nanoTime();
			elapsedSQL += (stopSQL - startSQL);
			br.close();
			long stop = System.nanoTime();
			long elapsedTime = stop - start;
			System.out.println("ALL: " + (elapsedTime / 1000000) + "ms");
			System.out.println("SQL: " + (elapsedSQL / 1000000) + "ms");
			System.out.println("H5:  " + (elapsedH5 / 1000000) + "ms");
		}
	}

	private static double parseDouble(double dbl) {
		if (Double.isNaN(dbl))
			return 0;
		else
			return dbl;
	}

	private static void doSomething(String string) throws Exception {
		long startH5 = System.nanoTime();
		ile++;
		if (ile % 10 == 0)
			System.out.println(ile + " / " + ile_all);
		// TODO Auto-generated method stub
		H5File h5 = getter.hdf5_open_readonly(string);

		String title = hdf5_getters.get_title(h5);
		String artist_name = hdf5_getters.get_artist_name(h5);
		String track_id = hdf5_getters.get_track_id(h5);
		String song_id = hdf5_getters.get_song_id(h5);
		double artist_familiarity = parseDouble(hdf5_getters.get_artist_familiarity(h5));
		double artist_hotttnesss = parseDouble(hdf5_getters.get_artist_hotttnesss(h5));
		String artist_id = hdf5_getters.get_artist_id(h5);
		String artist_location = hdf5_getters.get_artist_location(h5);
		double danceability = parseDouble(hdf5_getters.get_danceability(h5));
		double duration = parseDouble(hdf5_getters.get_duration(h5));
		double energy = parseDouble(hdf5_getters.get_energy(h5));
		String release = hdf5_getters.get_release(h5);
		int release_7digitalid = hdf5_getters.get_release_7digitalid(h5);
		double song_hotttnesss = parseDouble(hdf5_getters.get_song_hotttnesss(h5));
		int year = hdf5_getters.get_year(h5);

		String[] similar_artists = {};
		try {
			similar_artists = hdf5_getters.get_similar_artists(h5);
		} catch (Exception e) {
		}

		String[] artist_mbtags = {};
		try {
			artist_mbtags = hdf5_getters.get_artist_mbtags(h5);
		} catch (Exception e) {
		}

		String[] artist_terms = {};
		double[] artist_terms_freq = {};
		double[] artist_terms_weight = {};

		try {
			artist_terms = hdf5_getters.get_artist_terms(h5);
			artist_terms_freq = hdf5_getters.get_artist_terms_freq(h5);
			artist_terms_weight = hdf5_getters.get_artist_terms_weight(h5);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// System.out.println(artist_name + " - " + title);
		// System.out.println();
		getter.hdf5_close(h5);
		long stopH5 = System.nanoTime();
		elapsedH5 += (stopH5 - startH5);

		long startSQL = System.nanoTime();
		batch.addRecord(track_id, song_id, title, artist_id, artist_name, artist_location, artist_hotttnesss,
				artist_familiarity, danceability, duration, energy, release, release_7digitalid, song_hotttnesss, year);
		SAbatch.addRecords(artist_id, similar_artists);
		AMbatch.addRecords(artist_id, artist_mbtags);
		ATbatch.addRecords(artist_id, artist_terms, artist_terms_freq, artist_terms_weight);
		long stopSQL = System.nanoTime();
		elapsedSQL += (stopSQL - startSQL);

	}

	private static String getPath(String string) {
		// TODO Auto-generated method stub
		return "" + string.charAt(2) + "/" + string.charAt(3) + "/" + string.charAt(4) + "/" + string + ".h5";
	}
}
