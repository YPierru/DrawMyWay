package com.example.drawmywaybeta2.Parcours.Downloaded;

import java.util.ArrayList;
import java.util.List;

import com.example.drawmywaybeta2.Parcours.Trajet;

import android.os.Parcel;
import android.os.Parcelable;

public class DirectionsResponse implements Parcelable{

	private List<Route> routes;

	public List<Route> getRoutes() {
		return routes;
	}
	
	public DirectionsResponse(Parcel in){
		this.routes = new ArrayList<Route>();
		in.readList(this.routes, getClass().getClassLoader());
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		return "DirectionsResponse [routes=" + routes + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<DirectionsResponse> CREATOR = new Parcelable.Creator<DirectionsResponse>() {

		@Override
		public DirectionsResponse createFromParcel(Parcel source) {
			return new DirectionsResponse(source);
		}

		@Override
		public DirectionsResponse[] newArray(int size) {
			return new DirectionsResponse[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.routes);
	}
}
