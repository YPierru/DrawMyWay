package com.ironrabbit.drawmywaybeta4.activity;

import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.route.Route;


/*
 * DO
 * On affiche la position de l'utilisateur, uniquement via le GPS, pour plus de précision.
 * On affiche le parcours
 * TODO
 * Synchroniser la position de l'user avec celle du parcours :
 * Lorsque l'user franchit une step, les informations de direction sont mise à jour.
 * Trouver des éléments graphiques sympa pour afficher les infos de directions
 * 
 */

public class GPSRunner extends Activity {

	private LocationManager mLocManag;
	private MyLocationListener mLocList;
	private double latitude, longitude, altitude;
	static GPSRunner thisactivity;
	private Route mRoute;
	private float accuracy;
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		
		thisactivity = this;
		getActionBar().hide();

		mMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapGPS)).getMap();
		

		mRoute = getIntent().getExtras().getParcelable("TRAJET");
		drawRoute(mRoute.getPointsWhoDrawsPolylineLatLng());
	}
	
	public static GPSRunner getInstance(){
		return thisactivity;
	}
	
	private void drawRoute(ArrayList<LatLng> listPointsOverview){
		//Departure
		setMarker(listPointsOverview.get(0), "Go !");
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPointsOverview.get(0), 15);
		mMap.animateCamera(cu);
		
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(15).color(Color.argb(120, 0, 0, 221));
		for (int i = 0; i < listPointsOverview.size(); i++) {
			options.add(listPointsOverview.get(i));
		}
		mMap.addPolyline(options);
		
		setMarker(listPointsOverview.get(listPointsOverview.size() - 1), "Arrivée");
	}
	
	public void setMarker(LatLng point, String str) {
		mMap.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(point).title(str)).showInfoWindow();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocList = new MyLocationListener();
		mLocManag = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		if (!mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}else{
			mLocManag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,
				mLocList);
		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Veuillez activer votre GPS pour continuer")
				.setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,final int id) {
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						GPSRunner.getInstance().finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocManag.removeUpdates(mLocList);
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();
			accuracy = location.getAccuracy();

			String msg = "Lat = " + latitude + "\nLong = " + longitude
					+ "\nAlt = " + altitude + "\nAccuracy = " + accuracy;
			Toast.makeText(GPSRunner.this, msg, Toast.LENGTH_LONG).show();

			LatLng p = new LatLng(latitude, longitude);
			mMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.android))
					.anchor(0.0f, 1.0f) // Anchors the
										// marker on the
										// bottom left
					.position(p).title("Moua :3"));
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(p, 15);
			mMap.animateCamera(cu);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			String newStatus = "";
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				newStatus = "OUT_OF_SERVICE";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				newStatus = "TEMPORARILY_UNAVAILABLE";
				break;
			case LocationProvider.AVAILABLE:
				newStatus = "AVAILABLE";
				break;
			}
			Toast.makeText(GPSRunner.this, provider + " " + newStatus,
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(GPSRunner.this, "enable : " + provider,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(GPSRunner.this, "disable : " + provider,
					Toast.LENGTH_SHORT).show();
		}

	}
}
