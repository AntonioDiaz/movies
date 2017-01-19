package com.adiaz.movies;

import android.os.AsyncTask;

import com.adiaz.movies.utilities.InternetUtilities;
import com.adiaz.movies.utilities.JSonUtilities;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by toni on 19/01/2017.
 */
public class FetchMovies extends AsyncTask<URL, Void, List<MovieEntity>> {

		private final String TAG = FetchMovies.class.getSimpleName();
		FetchMoviesListener mFecthMoviesListener;


		public interface FetchMoviesListener {
			void onLoadCompleted(List<MovieEntity> movies);
		}


		public FetchMovies(FetchMoviesListener fetchMoviesListener) {
			mFecthMoviesListener = fetchMoviesListener;
		}

		@Override
		protected List<MovieEntity> doInBackground(URL... urls) {
			List<MovieEntity> movies = null;
			try {
				String responseStr = InternetUtilities.getResponseFromHttpUrl(urls[0]);
				movies = JSonUtilities.extractMoviesFromJson(responseStr);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return movies;
		}

		@Override
		protected void onPostExecute(List<MovieEntity> movies) {
			mFecthMoviesListener.onLoadCompleted(movies);
		}
	}
