package com.adiaz.movies;

/* Created by toni on 16/01/2017. */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{

	private static final String TAG = MoviesAdapter.class.getSimpleName();
	private List<MovieEntity> mMoviesList;
	private Context mContext;
	private int mWidth;
	private int mHeight;
	private ListItemClickListener mListItemClickListener;

	public interface ListItemClickListener {
		void onListItemClick(MovieEntity movie);
	}

	public MoviesAdapter (ListItemClickListener listItemClickListener) {
		mMoviesList = new ArrayList<>();
		mListItemClickListener = listItemClickListener;
	}

	@Override
	public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		mWidth = parent.getWidth();
		mHeight = parent.getHeight();
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View viewInflated = layoutInflater.inflate(R.layout.movies_list_item, parent, false);
		MoviesViewHolder moviesViewHolder = new MoviesViewHolder(viewInflated);
		return moviesViewHolder;
	}

	@Override
	public void onBindViewHolder(MoviesViewHolder holder, int position) {
		if (mMoviesList!=null && position<mMoviesList.size()) {
			MovieEntity movieEntity = mMoviesList.get(position);
			ImageView imageView = holder.ivMovie;
			String urlImage = MainActivity.IMAGE_URL + movieEntity.getPosterPath();
			Picasso.with(mContext)
					.load(urlImage)
					.error(R.drawable.ic_error_black_24dp)
					.resize(400, 600)
					.centerInside()
					.into(imageView);
		}
	}

	@Override
	public int getItemCount() {
		return mMoviesList==null?0:mMoviesList.size();
	}

	public List<MovieEntity> getmMoviesList() {
		return mMoviesList;
	}

	public void setmMoviesList(List<MovieEntity> movies) {
		this.mMoviesList = movies;
		notifyDataSetChanged();
	}

	public void addMoviesList(List<MovieEntity> newMovies) {
		this.mMoviesList.addAll(newMovies);
		notifyDataSetChanged();
	}

	class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView ivMovie;

		public MoviesViewHolder(View view) {
			super(view);
			view.setOnClickListener(this);
			ivMovie = (ImageView)view.findViewById(R.id.iv_movie);
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			mListItemClickListener.onListItemClick(mMoviesList.get(position));
		}
	}
}
