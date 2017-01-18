package com.adiaz.movies.utilities;

import com.adiaz.movies.MovieEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* Created by toni on 17/01/2017. */

public class JSonUtilities {

	public static List<MovieEntity> extractMoviesFromJson(String jsonMoviesStr) throws JSONException {
		List<MovieEntity> movies = new ArrayList<>();
		JSONObject jsonMoviesObj = new JSONObject(jsonMoviesStr);
		JSONArray results = jsonMoviesObj.getJSONArray("results");
		for (int i = 0; i < results.length(); i++) {
			String strTitle = results.getJSONObject(i).getString("title");
			String pathPoster = results.getJSONObject(i).getString("poster_path");
			String plot = results.getJSONObject(i).getString("overview");
			String releaseDate = results.getJSONObject(i).getString("release_date");
			String voteAverage = results.getJSONObject(i).getString("vote_average");

			MovieEntity movieEntity = new MovieEntity();
			movieEntity.setTitle(strTitle);
			movieEntity.setPosterPath(pathPoster);
			movieEntity.setReleaseDate(releaseDate);
			movieEntity.setPlot(plot);
			movieEntity.setVoteAverage(voteAverage);
			movies.add(movieEntity);

		}
		return movies;
	}
}


