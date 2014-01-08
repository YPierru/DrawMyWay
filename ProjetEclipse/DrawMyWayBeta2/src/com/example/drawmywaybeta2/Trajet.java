package com.example.drawmywaybeta2;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Trajet {
	
	private ArrayList<LatLng> listPoint;
	private String name;
	private boolean isFinish;
	
	public Trajet(String n,ArrayList<LatLng> lpnt, boolean isF){
		this.name=n;
		this.listPoint =lpnt;
		this.isFinish=isF;
	}

	public String getName(){
		return this.name;
	}
	
	public void setName(String n){
		this.name=n;
	}
	
	public ArrayList<LatLng> getListPoint(){
		return this.listPoint;
	}
	
	public void setListPoint(ArrayList<LatLng> lp){
		this.listPoint=lp;
	}
	
	public LatLng getStartPoint(){
		return this.listPoint.get(0);
	}
	
	public LatLng getEndPoint(){
		if(this.isFinish)
			return this.listPoint.get(this.listPoint.size()-1);
		else
			return null;
	}
	
	public boolean isFinish(){
		return this.isFinish;
	}
	
	public void setFinish(boolean isF){
		this.isFinish=isF;
	}
	
}
