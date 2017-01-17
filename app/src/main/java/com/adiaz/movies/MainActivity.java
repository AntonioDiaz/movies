package com.adiaz.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adiaz.movies.utilities.InternetUtilities;
import com.adiaz.movies.utilities.JSonUtilities;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	/* public static final String THEMOVIEDB_URL = "popular?api_key="; */
	public static final String THEMOVIEDB_URL_POPULARITY = "http://api.themoviedb.org/3/movie/popular";
	public static final String THEMOVIEDB_URL_RATING = "http://api.themoviedb.org/3/movie/top_rated";
	public static final String PARAM_API_KEY = "api_key";
	public static final String PARAM_API_PAGE = "page";

	private Context mContext;
	private SharedPreferences mPreferences;
	private MenuItem mMenuItemPopularity;
	private MenuItem mMenuItemRating;
	private MoviesAdapter mMoviesAdapter;
	private RecyclerView mRecyclerViewMovies;
	private int mPage;
	private LinearLayoutManager mLayoutManager;
	private boolean mLoading = true;
	private int previousItemCount = 0;
	private ProgressBar mProgressBarMovies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		mProgressBarMovies = (ProgressBar)findViewById(R.id.pb_loading_indicator);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		mMoviesAdapter = new MoviesAdapter();

		mLayoutManager = new LinearLayoutManager(this);
		mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mLayoutManager.setReverseLayout(false);

		mRecyclerViewMovies = (RecyclerView)findViewById(R.id.rv_movies);
		mRecyclerViewMovies.setLayoutManager(mLayoutManager);
		mRecyclerViewMovies.setHasFixedSize(true);
		mRecyclerViewMovies.setAdapter(mMoviesAdapter);

		boolean sortByPopularity = mPreferences.getBoolean(getString(R.string.pref_sort_key), true);
		mPage = 1;
		refreshMovies(sortByPopularity);
		showLoadingBar();
		mRecyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int visibleItemCount = mRecyclerViewMovies.getChildCount();
				int totalItemCount = mLayoutManager.getItemCount();
				int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
				int visibleThreshold = 15;
				if (mLoading) {
					if (totalItemCount>previousItemCount) {
						mLoading = false;
						previousItemCount = totalItemCount;
					}
				} else {
					if ((firstVisibleItem + visibleItemCount) >= (totalItemCount - visibleThreshold)) {
						mLoading = true;
						boolean sortByPopularity = mPreferences.getBoolean(getString(R.string.pref_sort_key), true);
						refreshMovies(sortByPopularity);
					}
				}
			}
		});
	}

	private void showLoadingBar() {
		mProgressBarMovies.setVisibility(View.VISIBLE);
		mRecyclerViewMovies.setVisibility(View.INVISIBLE);
	}

	private void hideLoadingBar() {
		mProgressBarMovies.setVisibility(View.INVISIBLE);
		mRecyclerViewMovies.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		mMenuItemPopularity = menu.findItem(R.id.action_sort_popularity);
		mMenuItemRating = menu.findItem(R.id.action_sort_rating);
		boolean sortByPopularity = mPreferences.getBoolean(getString(R.string.pref_sort_key), true);
		mMenuItemPopularity.setEnabled(!sortByPopularity);
		mMenuItemRating.setEnabled(sortByPopularity);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch (itemId) {
			case R.id.action_sort_popularity:
				showLoadingBar();
				mPage = 1;
				mMoviesAdapter.setmMoviesList(new ArrayList<MovieEntity>());
				previousItemCount = 0;
				mLoading = true;
				refreshMovies(true);
				break;
			case R.id.action_sort_rating:
				showLoadingBar();
				mPage = 1;
				mMoviesAdapter.setmMoviesList(new ArrayList<MovieEntity>());
				previousItemCount = 0;
				mLoading = true;
				refreshMovies(false);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshMovies(boolean popularity) {
		Log.d(TAG, "sortPreference [" + popularity + "]");
		mPreferences.edit().putBoolean(getString(R.string.pref_sort_key), popularity);
		if (mMenuItemPopularity!=null) {
			mMenuItemPopularity.setEnabled(!popularity);
		}
		if (mMenuItemRating!=null) {
			mMenuItemRating.setEnabled(popularity);
		}
		String strUrl = popularity ? THEMOVIEDB_URL_POPULARITY : THEMOVIEDB_URL_RATING;
		Uri uri = Uri.parse(strUrl).buildUpon()
				.appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIES_API_KEY)
				.appendQueryParameter(PARAM_API_PAGE, Integer.toString(mPage))
				.build();
		try {
			Log.d(TAG, uri.toString());
			URL url = new URL(uri.toString());
			FetchMovies fetchMovies = new FetchMovies();
			fetchMovies.execute(url);
			mPage++;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	class FetchMovies extends AsyncTask<URL, Void, List<MovieEntity>> {

		private final String TAG = MainActivity.FetchMovies.class.getSimpleName();

		public FetchMovies() {}

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
			Log.d(TAG, "onPostExecute " + movies.size());
			if (movies==null) {
				Toast.makeText(mContext, R.string.error_loading_movies, Toast.LENGTH_SHORT).show();
			} else {
				mMoviesAdapter.addMoviesList(movies);
				hideLoadingBar();
			}
		}
	}
}
