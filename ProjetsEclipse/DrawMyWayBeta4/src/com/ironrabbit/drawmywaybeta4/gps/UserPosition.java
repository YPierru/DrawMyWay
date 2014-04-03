package com.ironrabbit.drawmywaybeta4.gps;

import java.util.ArrayList;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ironrabbit.drawmyway.R;

public class UserPosition {

	private LatLng mCurrentPos;
	private ArrayList<LatLng> mHistoPos;
	private Marker mMyMarker;
	private boolean isOnRoute;
	private int mIndexPointToFollow;

	public UserPosition(LatLng p) {
		this.mCurrentPos = p;
		this.mMyMarker = null;
		this.mHistoPos = new ArrayList<LatLng>();
		this.mHistoPos.add(p);
		this.isOnRoute = false;
		this.mIndexPointToFollow = 0;
	}

	public UserPosition() {
		this.mMyMarker = null;
		this.mHistoPos = new ArrayList<LatLng>();
		this.isOnRoute = false;
		this.mIndexPointToFollow = 0;
	}

	public void setCurrentPos(LatLng p) {
		this.mHistoPos.add(this.mCurrentPos);
		this.mCurrentPos = p;
	}

	public void setCurrentPos(Double lat, Double lng) {
		this.mHistoPos.add(this.mCurrentPos);
		this.mCurrentPos = new LatLng(lat, lng);
	}

	public LatLng getCurrentPos() {
		return this.mCurrentPos;
	}

	public int getIndexPointToFollow() {
		return this.mIndexPointToFollow;
	}

	public void setToNextPointToFollow() {
		this.mIndexPointToFollow++;
	}

	public boolean isOnRoute() {
		return this.isOnRoute;
	}

	public void setIsOnRoute(boolean b) {
		this.isOnRoute = b;
	}

	public void addCurrentPosOnMap(GoogleMap map) {

		if (this.mMyMarker == null) {
			this.mMyMarker = map.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.android))
					.anchor(0.0f, 1.0f)
					.position(this.mCurrentPos));

			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(this.mCurrentPos,
					19);
			map.animateCamera(cu);
		}else{
			this.mMyMarker.setPosition(this.mCurrentPos);
		}
	}

	public void drawUserRoute(GoogleMap map) {
		Log.d("DEBUUUUG", ""+this.mHistoPos.size());
		/*PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(15).color(Color.argb(255, 0, 0, 221));
		for (int i = 0; i < this.mHistoPos.size(); i++) {
			options.add(this.mHistoPos.get(i));
		}
		map.addPolyline(options);*/
	}

	public int distanceBetween(LatLng p) {
		double lat1 = this.mCurrentPos.latitude;
		double lng1 = this.mCurrentPos.longitude;
		double lat2 = p.latitude;
		double lng2 = p.longitude;

		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (int) (dist * meterConversion);
	}

}
