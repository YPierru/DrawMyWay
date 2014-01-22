package com.example.drawmywaybeta2.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.example.drawmywaybeta2.Parcours.Trajet;
import com.example.gmapstests.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GPSRunner extends Activity {

	private GoogleMap map;
	private Trajet myTrajet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		
		myTrajet = getIntent().getExtras().getParcelable("TRAJET");
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		ArrayList<LatLng> listPoints = myTrajet.getListPoint();

		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPoints.get(0), 17);
		map.animateCamera(cu, 600, null);
		
		for(int i=0;i<listPoints.size()-1;i++){
			Polyline p=map.addPolyline(new PolylineOptions().geodesic(false)
					.add(listPoints.get(i))
					.add(listPoints.get(i + 1)).width(15)
					.color(Color.argb(120, 0, 0, 221)));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gpsrunner, menu);
		return true;
	}

}
