package com.example.drawmywaybeta3.Trajet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.drawmywaybeta3.DoubleArrayList;
import com.example.drawmywaybeta3.Trajet.Downloaded.DirectionsResponse;
import com.example.drawmywaybeta3.Trajet.Downloaded.Legs;
import com.example.drawmywaybeta3.Trajet.Downloaded.Step;
import com.google.android.gms.maps.model.LatLng;

public class Trajet implements Parcelable,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<DirectionsResponse> listSegment;
	private String name;
	private boolean hasBeenSave;
	private boolean estDessine;
	private DoubleArrayList<Double> pointsWhoDrawsPolyline;
	private DoubleArrayList<Double> listMarkers;
	private String dateCreation;
	private String dateDerModif;

	public Trajet(  ArrayList<DirectionsResponse> ls,
					String n,
					boolean hbs,
					ArrayList<LatLng> pwho,
					ArrayList<LatLng> lm,
					boolean ed,
					String dc,
					String ddm){
		this.listSegment=ls;
		this.name=n;
		this.hasBeenSave=hbs;
		this.pointsWhoDrawsPolyline=new DoubleArrayList<Double>();
		for(int i=0;i<pwho.size();i++){
			this.pointsWhoDrawsPolyline.add(pwho.get(i).latitude,pwho.get(i).longitude);
		}
		this.listMarkers=new DoubleArrayList<Double>();
		for(int i=0;i<lm.size();i++){
			this.listMarkers.add(lm.get(i).latitude,lm.get(i).longitude);
		}
		this.estDessine=ed;
		this.dateCreation=dc;
		this.dateDerModif=ddm;
	}
	
	public Trajet(String n, boolean isF, boolean isD,String dc) {
		this.name = n;
		this.hasBeenSave = isF;
		this.estDessine=isD;
		this.listSegment = new ArrayList<DirectionsResponse>();
		this.pointsWhoDrawsPolyline=new DoubleArrayList<Double>();
		this.listMarkers=new DoubleArrayList<Double>();
		this.dateCreation=dc;
		this.dateDerModif=dc;
	}
	
	public Trajet(Parcel in){
		this.listSegment = new ArrayList<DirectionsResponse>();
		in.readList(this.listSegment, getClass().getClassLoader());
		this.name = in.readString();
		boolean[] bool = new boolean[2];
		in.readBooleanArray(bool);
		this.hasBeenSave = bool[0];
		this.estDessine = bool[1];
		this.pointsWhoDrawsPolyline=in.readParcelable(getClass().getClassLoader());
		this.listMarkers=in.readParcelable(getClass().getClassLoader());
		this.dateCreation=in.readString();
		this.dateDerModif=in.readString();
	}


	public ArrayList<DirectionsResponse> getListSegment() {
		return listSegment;
	}

	public void setListSegment(ArrayList<DirectionsResponse> listSegment) {
		this.listSegment = listSegment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateDerModif() {
		return dateDerModif;
	}

	public void setDateDerModif(String dateDerModif) {
		this.dateDerModif = dateDerModif;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public boolean isHasBeenSave() {
		return hasBeenSave;
	}

	public void setHasBeenSave(boolean hasBeenSave) {
		this.hasBeenSave = hasBeenSave;
	}

	public boolean isEstDessine() {
		return estDessine;
	}

	public void setEstDessine(boolean estDessine) {
		this.estDessine = estDessine;
	}

	public DoubleArrayList<Double> getPointsWhoDrawsPolyline() {
		return pointsWhoDrawsPolyline;
	}

	public void setPointsWhoDrawsPolyline(
			DoubleArrayList<Double> pointsWhoDrawsPolyline) {
		this.pointsWhoDrawsPolyline = pointsWhoDrawsPolyline;
	}

	public ArrayList<LatLng> getPointsWhoDrawsPolylineLatLng() {
		ArrayList<LatLng> lp = new ArrayList<LatLng>();
		ArrayList<Double> ld = new ArrayList<Double>();
		for(int i=0;i<this.pointsWhoDrawsPolyline.size();i++){
			ld=this.pointsWhoDrawsPolyline.get(i);
			LatLng tmp = new LatLng(ld.get(0), ld.get(1));
			lp.add(tmp);
		}
		return lp;
	}

	public void setPointsWhoDrawsPolylineLatLng(ArrayList<LatLng> pwho) {
		this.pointsWhoDrawsPolyline.clear();
		Double lat,lng;
		for(int i=0;i<pwho.size();i++){
			lat = pwho.get(i).latitude;
			lng = pwho.get(i).longitude;
			this.pointsWhoDrawsPolyline.add(lat,lng);
		}
	}

	public DoubleArrayList<Double> getListMarkers() {
		return listMarkers;
	}

	public void setListMarkers(DoubleArrayList<Double> listMarkers) {
		this.listMarkers = listMarkers;
	}

	public ArrayList<LatLng> getListMarkersLatLng() {
		ArrayList<LatLng> lp = new ArrayList<LatLng>();
		ArrayList<Double> ld = new ArrayList<Double>();
		for(int i=0;i<this.listMarkers.size();i++){
			ld=this.listMarkers.get(i);
			LatLng tmp = new LatLng(ld.get(0), ld.get(1));
			lp.add(tmp);
		}
		return lp;
	}

	public void setListMarkersLatLng(ArrayList<LatLng> lm) {
		this.listMarkers.clear();
		Double lat,lng;
		for(int i=0;i<lm.size();i++){
			lat = lm.get(i).latitude;
			lng = lm.get(i).longitude;
			this.listMarkers.add(lat,lng);
		}
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
		dest.writeList(this.listSegment);
		dest.writeString(this.name);
		boolean[] arrayBool = { this.hasBeenSave,this.estDessine };
		dest.writeBooleanArray(arrayBool);
		dest.writeParcelable(this.pointsWhoDrawsPolyline,0);
		dest.writeParcelable(this.listMarkers,0);
		dest.writeString(this.dateCreation);
		dest.writeString(this.dateDerModif);
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
		if(this.listSegment.size()>0){
			List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
			for(int i=0;i<listLegs.size();i++){
				distTotal+=listLegs.get(i).getDistance().getValue();
			}
		}
		
		return distTotal;
	}
	
	public int getDureeTotal(){
		int dureeTotal=0;
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		for(int i=0;i<listLegs.size();i++){
			dureeTotal+=listLegs.get(i).getDuration().getValue();
		}
		
		return dureeTotal;
	}
	
	public String getStartAddress(){
		return this.listSegment.get(0).getRoutes().get(0).getLegs().get(0).getStart_address();
	}
	
	public String getEndAddress(){
		List<Legs> listLegs = this.listSegment.get(0).getRoutes().get(0).getLegs();
		return listLegs.get(listLegs.size()-1).getEnd_address();
	}
	
	public void removeLastDR(){
		this.listSegment.remove(this.listSegment.size()-1);
	}
}
