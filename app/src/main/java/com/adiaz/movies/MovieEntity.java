package com.adiaz.movies;

/* Created by toni on 16/01/2017. */

import android.os.Parcel;
import android.os.Parcelable;

public class MovieEntity implements Parcelable{

	private String title;
	private String posterPath;
	private String releaseDate;
	private String plot;
	private String voteAverage;

	public MovieEntity() {
	}

	protected MovieEntity(Parcel in) {
		title = in.readString();
		posterPath = in.readString();
		releaseDate = in.readString();
		plot = in.readString();
		voteAverage = in.readString();
	}

	public static final Creator<MovieEntity> CREATOR = new Creator<MovieEntity>() {
		@Override
		public MovieEntity createFromParcel(Parcel in) {
			return new MovieEntity(in);
		}

		@Override
		public MovieEntity[] newArray(int size) {
			return new MovieEntity[size];
		}
	};

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public String getVoteAverage() {
		return voteAverage;
	}

	public void setVoteAverage(String voteAverage) {
		this.voteAverage = voteAverage;
	}

	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(posterPath);
		dest.writeString(releaseDate);
		dest.writeString(plot);
		dest.writeString(voteAverage);
	}

}
