package com.ironrabbit.drawmywaybeta4.gps.activity;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.gps.UserPosition;
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.downloaded.Step;


/*
 * DO
 * On affiche la position de l'utilisateur, uniquement via le GPS, pour plus de pr??cision.
 * On affiche le parcours
 * TODO
 * Synchroniser la position de l'user avec celle du parcours :
 * Lorsque l'user franchit une step, les informations de direction sont mise ?? jour.
 * Trouver des ??l??ments graphiques sympa pour afficher les infos de directions
 * 
 */

public class GPSRunner extends Activity {

	private LocationManager mLocManag;
	private MyLocationListener mLocList;
	static GPSRunner thisactivity;
	private ArrayList<LatLng> listPointsToFollow;
	private Route mRoute;
	private ArrayList<Step> mListSteps;
	private GoogleMap mMap;
	private UserPosition mUserPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		this.mUserPos = new UserPosition();
		
		thisactivity = this;
		getActionBar().hide();

		mMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapGPS)).getMap();
		//mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		mRoute = getIntent().getExtras().getParcelable("TRAJET");
		this.mListSteps=mRoute.getListSteps();
		
		this.listPointsToFollow=new ArrayList<LatLng>();
		for (int i = 0; i < mListSteps.size(); i++) {
			listPointsToFollow.add(new LatLng(mListSteps.get(i).getStart_location()
					.getLat(), mListSteps.get(i).getStart_location().getLng()));
			if(i+1==mListSteps.size()){
				listPointsToFollow.add(new LatLng(mListSteps.get(i).getEnd_location()
						.getLat(), mListSteps.get(i).getEnd_location().getLng()));
			}
		}
		drawRoute();
	}
	
	public static GPSRunner getInstance(){
		return thisactivity;
	}
	
	private void drawRoute(){
		//Departure
		ArrayList<LatLng> listPointsOverview = mRoute.getPointsWhoDrawsPolylineLatLng();
		setMarker(listPointsOverview.get(0), "Go !");
		
		CircleOptions circleOptions = new CircleOptions()
	    .center(listPointsOverview.get(0))
	    .radius(5);
		
		mMap.addCircle(circleOptions);
		
		//CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPointsOverview.get(0), 15);
		//mMap.animateCamera(cu);
		//Log.d("DEBUUUUUG", listPointsOverview.get(0).toString());
		
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(15).color(Color.argb(120, 0, 0, 221));
		for (int i = 0; i < listPointsOverview.size(); i++) {
			options.add(listPointsOverview.get(i));
		}
		mMap.addPolyline(options);
		
		
		
		setMarker(listPointsOverview.get(listPointsOverview.size() - 1), "Arrivee");
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
			mLocManag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0,
				mLocList);
		}
			//mLocManag.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0,mLocList);
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
			mUserPos.setCurrentPos(location.getLatitude() , location.getLongitude());
			mUserPos.addCurrentPosOnMap(mMap);
			int indexCurrentPoint = mUserPos.getIndexPointToFollow();
			int dist;
			Step currentStep;
			LinearLayout ll_DistInstr=(LinearLayout)findViewById(R.id.centralLinLay);
			
			if(!mUserPos.isOnRoute()){
				if(ll_DistInstr.getVisibility()==View.VISIBLE){
					ll_DistInstr.setVisibility(View.INVISIBLE);
				}
				dist = mUserPos.distanceBetween(listPointsToFollow.get(indexCurrentPoint));
				if(dist<5){
					Toast.makeText(GPSRunner.this, "Départ !",Toast.LENGTH_SHORT).show();
					mUserPos.setIsOnRoute(true);
					mUserPos.setToNextPointToFollow();
					displayInformations(mUserPos.getIndexPointToFollow());
				}
			}else{
				displayInformations(indexCurrentPoint);
			}
			
			Log.d("DEBUUUUUG","P1="+mUserPos.getCurrentPos().toString());
			//Log.d("DEBUUUUUG","P2="+listPointsOverview.get(0).toString());
			//Log.d("DEBUUUUUG","DIST="+dist);
		}
		
		public void displayInformations(int indexCurrentPoint){
			Step currentStep = mListSteps.get(indexCurrentPoint-1);
			TextView tv_DistNextPoint = (TextView)findViewById(R.id.tv_distNextPoint);
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_instructions);
			LinearLayout ll_DistInstr=(LinearLayout)findViewById(R.id.centralLinLay);
			int dist;
			
			if(ll_DistInstr.getVisibility()==View.INVISIBLE){
				ll_DistInstr.setVisibility(View.VISIBLE);
			}
			tv_DistNextPoint.setText(""+mUserPos.distanceBetween(listPointsToFollow.get(indexCurrentPoint)));
			tv_Instructions.setText(Html.fromHtml(currentStep.getHtml_instructions()));
			mUserPos.drawUserRoute(mMap);
			if(indexCurrentPoint<listPointsToFollow.size()){
				dist = mUserPos.distanceBetween(listPointsToFollow.get(indexCurrentPoint++));
				if(dist<5){
					Toast.makeText(GPSRunner.this, "Next point !",Toast.LENGTH_SHORT).show();
					mUserPos.setToNextPointToFollow();
				}
			}else{
				dist = mUserPos.distanceBetween(listPointsToFollow.get(indexCurrentPoint));
				if(dist<5){
					Toast.makeText(GPSRunner.this, "Fini !",Toast.LENGTH_SHORT).show();
					mUserPos.setIsOnRoute(false);
				}
			}
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
