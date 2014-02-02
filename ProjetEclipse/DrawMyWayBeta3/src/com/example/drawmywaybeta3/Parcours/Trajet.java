package com.example.drawmywaybeta3.Parcours;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.drawmywaybeta3.Parcours.Downloaded.DirectionsResponse;
import com.example.drawmywaybeta3.Parcours.Downloaded.Legs;
import com.example.drawmywaybeta3.Parcours.Downloaded.Step;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

public class Trajet implements Parcelable {
	private ArrayList<LatLng> listPoint;
	private ArrayList<DirectionsResponse> listSegment;
	private Polyline myPolyline;
	private ArrayList<Marker> listMarker;
	private String name;
	private boolean isFinish;

	public Trajet(String n, boolean isF) {
		this.name = n;
		this.isFinish = isF;
		this.listPoint = new ArrayList<LatLng>();
		this.listSegment = new ArrayList<DirectionsResponse>();
		this.listMarker = new ArrayList<Marker>();
	}
	
	public Trajet(Parcel in){
		this.listPoint = new ArrayList<LatLng>();
		in.readList(this.listPoint, getClass().getClassLoader());
		this.listSegment = new ArrayList<DirectionsResponse>();
		in.readList(this.listSegment, getClass().getClassLoader());
		this.name = in.readString();
		boolean[] bool = new boolean[1];
		in.readBooleanArray(bool);
		this.isFinish = bool[0];
	}

	public String getName() {
		return this.name;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public void setPolyline(Polyline p){
		this.myPolyline=p;
	}
	
	public void setListMarker(ArrayList<Marker> lm){
		this.listMarker=lm;
	}

	public ArrayList<LatLng> getListPoint() {
		return this.listPoint;
	}

	public ArrayList<DirectionsResponse> getListSegment() {
		return this.listSegment;
	}

	public LatLng getLastPoint() {
		return this.listPoint.get(this.listPoint.size() - 1);
	}

	public void setListPoint(ArrayList<LatLng> lp) {
		this.listPoint = lp;
	}

	public LatLng getStartPoint() {
		return this.listPoint.get(0);
	}

	public LatLng getEndPoint() {
		if (this.isFinish)
			return this.listPoint.get(this.listPoint.size() - 1);
		else
			return null;
	}

	public boolean isFinish() {
		return this.isFinish;
	}

	public void setFinish(boolean isF) {
		this.isFinish = isF;
	}

	public static final Parcelable.Creator<Trajet> CREATOR = new Parcelable.Creator<Trajet>() {

		@Override
		public Trajet createFromParcel(Parcel source) {
			return new Trajet(source);
		}

		@Override
		public Trajet[] newArray(int size) {
			return new Trajet[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.listPoint);
		dest.writeList(this.listSegment);
		dest.writeString(this.name);
		boolean[] arrayBool = { this.isFinish };
		dest.writeBooleanArray(arrayBool);
	}
	
	public ArrayList<Step> getListSteps(){
		ArrayList<Step> listSteps = new ArrayList<Step>();
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		for(int i=0;i<listLegs.size();i++){
			listSteps.addAll(listLegs.get(i).getSteps());
		}		
		return listSteps;
	}
	
	public int getDistTotal(){
		int distTotal=0;
		for(int i=0;i<this.listSegment.size();i++){
			distTotal+=this.listSegment.get(i).getRoutes().get(0).getLegs().get(0).getDistance().getValue();
		}
		
		return distTotal;
	}
	
	public int getDureeTotal(){
		int dureeTotal=0;
		for(int i=0;i<this.listSegment.size();i++){
			dureeTotal+=this.listSegment.get(i).getRoutes().get(0).getLegs().get(0).getDuration().getValue();
		}
		
		return dureeTotal;
	}
	
	public void removeLastDR(){
		this.listSegment.remove(this.listSegment.size()-1);
	}
}
