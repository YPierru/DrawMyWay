package com.example.drawmywaybeta2;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Trajet {
	
	private ArrayList<LatLng> listPoint;
	private String name;
	
	public Trajet(String n,ArrayList<LatLng> lpnt){
		this.name=n;
		this.listPoint =lpnt;
	}

	public String getName(){
		return name;
	}
	
	public ArrayList<LatLng> getListPoint(){
		return this.listPoint;
	}
	
	public LatLng getStartPoint(){
		return this.listPoint.get(0);
	}
	
	public LatLng getEndPoint(){
		return this.listPoint.get(this.listPoint.size()-1);
	}
	
}
