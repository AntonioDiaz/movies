package com.adiaz.movies;

/* Created by toni on 16/01/2017. */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{

	private static final String TAG = MoviesAdapter.class.getSimpleName();
	private List<MovieEntity> mMoviesList;

	public MoviesAdapter () {
		mMoviesList = new ArrayList<>();
	}

	@Override
	public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View viewInflated = layoutInflater.inflate(R.layout.movies_list_item, parent, false);
		MoviesViewHolder moviesViewHolder = new MoviesViewHolder(viewInflated);
		return moviesViewHolder;
	}

	@Override
	public void onBindViewHolder(MoviesViewHolder holder, int position) {
		if (mMoviesList!=null && position<mMoviesList.size()) {
			MovieEntity movieEntity = mMoviesList.get(position);
			holder.tvTitle.setText(movieEntity.getTitle());
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

	class MoviesViewHolder extends RecyclerView.ViewHolder {

		TextView tvTitle;

		public MoviesViewHolder(View view) {
			super(view);
			tvTitle = (TextView)view.findViewById(R.id.tv_movie_title);
		}
	}
}
