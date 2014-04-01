package com.ironrabbit.drawmywaybeta4.gps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ironrabbit.drawmyway.R;

public class UserPosition {
	
	private LatLng mPos;
	private Marker mMyMarker;
	
	public UserPosition(LatLng p){
		this.mPos=p;
		this.mMyMarker=null;
	}
	
	public UserPosition(){
		this.mMyMarker=null;
	}
	
	public void setPos(LatLng p){
		this.mPos=p;
	}
	
	public void setPos(Double lat, Double lng){
		this.mPos = new LatLng(lat,lng);
	}
	
	public LatLng getPos(){
		return this.mPos;
	}
	
	public void addPosOnMap(GoogleMap map){
		
		if(!(this.mMyMarker==null)){
			this.mMyMarker.remove();
		}
		this.mMyMarker=map.addMarker(new MarkerOptions()
		.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.android))
		.anchor(0.0f, 1.0f) // Anchors the
							// marker on the
							// bottom left
		.position(this.mPos));
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(this.mPos, 19);
		map.animateCamera(cu);
	}
	
	public int distanceBetween(LatLng p){
		double lat1=this.mPos.latitude;
		double lng1=this.mPos.longitude;
		double lat2=p.latitude;
		double lng2=p.longitude;
		
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return (int)(dist * meterConversion);
	}

}
