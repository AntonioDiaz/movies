package com.adiaz.movies.utilities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.adiaz.movies.MainActivity;
import com.adiaz.movies.MovieEntity;
import com.adiaz.movies.R;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

	private static final String TAG = DetailsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		MovieEntity movie = getIntent().getParcelableExtra(MainActivity.INTENT_MOVIE_DETAILS);
		Log.d(TAG, movie.getTitle());
		TextView tvTitle = (TextView)findViewById(R.id.tv_title);
		tvTitle.setText(movie.getTitle());
		TextView tvRelease = (TextView)findViewById(R.id.tv_release);
		tvRelease.setText(movie.getReleaseDate());
		TextView tvVoteAverage = (TextView)findViewById(R.id.tv_vote);
		tvVoteAverage.setText(movie.getVoteAverage());
		TextView tvPlot = (TextView)findViewById(R.id.tv_plot);
		tvPlot.setText(movie.getPlot());

		ImageView imageView = (ImageView)findViewById(R.id.iv_poster);
		String urlImage = MainActivity.IMAGE_URL + movie.getPosterPath();
		Picasso.with(this)
				.load(urlImage)
				.error(R.drawable.ic_error_black_24dp)
				.resize(400, 600)
				.centerInside()
				.into(imageView);

	}
}

