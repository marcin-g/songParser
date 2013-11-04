package parser;

import java.io.BufferedReader;
import java.io.FileReader;

import ncsa.hdf.object.h5.H5File;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.GetterOnlyReflection;

import postgres.SongBatch;

public class TracksParser {
	public static hdf5_getters getter = new hdf5_getters();
	public static int ile = 0;
	public static int ile_all = 0;
	public static SongBatch batch = new SongBatch();

	public static void main(String[] args) throws Exception {

		// BufferedReader br = new BufferedReader(new
		// FileReader("/home/kailip/EDWD/MSDC/taste_profile_subset.txt"));
		BufferedReader br = new BufferedReader(
				new FileReader("/home/kailip/EDWD/MSDC/taste_profile_song_to_tracks.txt"));
		String DataPath = "/home/kailip/EDWD/MSDC/millionsong/data/";
		long start = System.nanoTime();
		try {
			String line = br.readLine();
			while (line != null && ile_all < 1000) {
				// System.out.println("Line..");
				String[] split = line.split(" ");
				if (split.length > 1) {
					ile_all++;
					for (int x = 1; x < split.length; x++) {
						// System.out.println(split[x]);
						String path = getPath(split[x]);
						// System.out.println(path);
						if (path.charAt(0) == 'A' || path.charAt(0) == 'B' || path.charAt(0) == 'C' || path.charAt(0) == 'K' || path.charAt(0) == 'F')
							doSomething(DataPath + path);
					}
				}
				line = br.readLine();
			}
		} finally {
			long elapsedTime = System.nanoTime() - start;
			System.out.println("wykonano w: " + elapsedTime);
			batch.close();
			br.close();
		}
	}

	private static void doSomething(String string) throws Exception {
		ile++;
		if (ile % 10 == 0)
			System.out.println(ile + " / " + ile_all);
		// TODO Auto-generated method stub
		H5File h5 = getter.hdf5_open_readonly(string);

		String title = hdf5_getters.get_title(h5);
		String artist_name = hdf5_getters.get_artist_name(h5);
		String track_id = hdf5_getters.get_track_id(h5);
		String song_id = hdf5_getters.get_song_id(h5);
		double artist_familiarity = hdf5_getters.get_artist_familiarity(h5);
		double artist_hotttnesss = hdf5_getters.get_artist_hotttnesss(h5);
		String artist_id = hdf5_getters.get_artist_id(h5);
		String artist_location = hdf5_getters.get_artist_location(h5);
		double danceability = hdf5_getters.get_danceability(h5);
		double duration = hdf5_getters.get_duration(h5);
		double energy = hdf5_getters.get_energy(h5);
		String release = hdf5_getters.get_release(h5);
		double song_hotttnesss = hdf5_getters.get_song_hotttnesss(h5);
		int year = hdf5_getters.get_year(h5);

		// System.out.println(artist_name + " - " + title);
		// System.out.println();
		getter.hdf5_close(h5);

		batch.addRecord(track_id, song_id, title, artist_id, artist_name, artist_location, artist_hotttnesss,
				artist_familiarity, danceability, duration, energy, release, song_hotttnesss, year);

	}

	private static String getPath(String string) {
		// TODO Auto-generated method stub
		return "" + string.charAt(2) + "/" + string.charAt(3) + "/" + string.charAt(4) + "/" + string + ".h5";
	}
}
