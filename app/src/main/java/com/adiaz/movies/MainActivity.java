package com.adiaz.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adiaz.movies.utilities.DetailsActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener, FetchMovies.FetchMoviesListener {
	public static final String TAG = MainActivity.class.getSimpleName();
	public static final String THEMOVIEDB_URL_POPULARITY = "http://api.themoviedb.org/3/movie/popular";
	public static final String THEMOVIEDB_URL_RATING = "http://api.themoviedb.org/3/movie/top_rated";
	public static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
	public static final String PARAM_API_KEY = "api_key";
	public static final String PARAM_API_PAGE = "page";
	public static final String INTENT_MOVIE_DETAILS = "INTENT_MOVIE_DETAILS";

	private Context mContext;
	private MoviesAdapter mMoviesAdapter;
	private RecyclerView mRecyclerViewMovies;
	private int mPage;
	private GridLayoutManager mGridLayoutManager;
	private boolean mLoading = true;
	private int previousItemCount = 0;
	private ProgressBar mProgressBarMovies;
	private Boolean mSortPopularity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		mPage = 1;
		mProgressBarMovies = (ProgressBar)findViewById(R.id.pb_loading_indicator);
		mMoviesAdapter = new MoviesAdapter(this);

		mGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
		mRecyclerViewMovies = (RecyclerView)findViewById(R.id.rv_movies);
		mRecyclerViewMovies.setLayoutManager(mGridLayoutManager);
		mRecyclerViewMovies.setHasFixedSize(true);
		mRecyclerViewMovies.setAdapter(mMoviesAdapter);
		refreshMovies();
		mRecyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int visibleItemCount = mRecyclerViewMovies.getChildCount();
				int totalItemCount = mGridLayoutManager.getItemCount();
				int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
				int visibleThreshold = 15;
				if (mLoading) {
					if (totalItemCount>previousItemCount) {
						mLoading = false;
						previousItemCount = totalItemCount;
					}
				} else {
					if ((firstVisibleItem + visibleItemCount) >= (totalItemCount - visibleThreshold)) {
						mLoading = true;
						refreshMovies();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (R.id.action_settings == itemId) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String sortCriteria = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
		boolean newCriteria = Boolean.parseBoolean(sortCriteria);
		if (mSortPopularity != newCriteria) {
			mPage = 1;
			mMoviesAdapter = new MoviesAdapter(this);
			mRecyclerViewMovies.setAdapter(mMoviesAdapter);
			refreshMovies();
		}
	}

	private void refreshMovies() {
		showLoadingBar();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String sortCriteria = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
		mSortPopularity = new Boolean(sortCriteria);
		String strUrl = mSortPopularity ? THEMOVIEDB_URL_POPULARITY : THEMOVIEDB_URL_RATING;
		Uri uri = Uri.parse(strUrl).buildUpon()
				.appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIES_API_KEY)
				.appendQueryParameter(PARAM_API_PAGE, Integer.toString(mPage))
				.build();
		try {
			URL url = new URL(uri.toString());
			FetchMovies fetchMovies = new FetchMovies(this);
			fetchMovies.execute(url);
			mPage++;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(MovieEntity movie) {
		Intent intent = new Intent(this, DetailsActivity.class);
		intent.putExtra(INTENT_MOVIE_DETAILS, movie);
		startActivity(intent);
	}

	@Override
	public void onLoadCompleted(List<MovieEntity> movies) {
		if (movies==null) {
			Toast.makeText(mContext, R.string.error_loading_movies, Toast.LENGTH_SHORT).show();
		} else {
			mMoviesAdapter.addMoviesList(movies);
			hideLoadingBar();
		}

	}
}
