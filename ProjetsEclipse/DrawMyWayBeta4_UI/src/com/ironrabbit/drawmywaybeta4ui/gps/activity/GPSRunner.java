package com.ironrabbit.drawmywaybeta4ui.gps.activity;

import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.Visibility;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.s;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ironrabbit.drawmywaybeta4ui.Constantes;
import com.ironrabbit.drawmywaybeta4ui.gps.UserPosition;
import com.ironrabbit.drawmywaybeta4ui.route.Route;
import com.ironrabbit.drawmywaybeta4ui.route.downloaded.Step;
import com.ironrabbit.drawmywayui.R;


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

public class GPSRunner extends Activity implements SensorEventListener,TextToSpeech.OnInitListener {
	
	private LocationManager mLocManag;
	private TextToSpeech textToSpeech;
	private MyLocationListener mLocList;
	static GPSRunner thisactivity;
	private ArrayList<LatLng> listPointsToFollow;
	private Route mRoute;
	private ArrayList<Step> mListSteps;
	private GoogleMap mMap;
	private UserPosition mUserPos;
	private int compteurAffichage=0;
	private boolean speak1000=false,speak500=false,speak200=false,speak50=false;

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
		
		this.textToSpeech = new TextToSpeech(this, this);
		
		//On recup??re la liste des steps
		this.mListSteps=mRoute.getListSteps();
		
		/*
		 * On r??cup??re la liste des points ?? suivre.
		 * A chaque nouveau point correspond une nouvelle instruction
		 */
		this.listPointsToFollow=new ArrayList<LatLng>();
		for (int i = 0; i < mListSteps.size(); i++) {
			listPointsToFollow.add(new LatLng(mListSteps.get(i).getStart_location()
					.getLat(), mListSteps.get(i).getStart_location().getLng()));
			if(i+1==mListSteps.size()){
				listPointsToFollow.add(new LatLng(mListSteps.get(i).getEnd_location()
						.getLat(), mListSteps.get(i).getEnd_location().getLng()));
			}
		}
		
		/*for(int i=0;i<this.mListSteps.size();i++){
			Log.d("debug.showInstr", this.mListSteps.get(i).getHtml_instructions());
		}
		for(int i=0;i<this.listPointsToFollow.size();i++){
			Log.d("PTFL", this.listPointsToFollow.get(i).toString());
		}*/
		
		//On dessine le trajet
		drawRoute();
	}
	
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = textToSpeech.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			}
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		textToSpeech.shutdown();
	}
	
	public static GPSRunner getInstance(){
		return thisactivity;
	}
	
	private void drawRoute(){
		//Departure
		ArrayList<LatLng> listPointsOverview = mRoute.getPointsWhoDrawsPolylineLatLng();
		setMarker(listPointsOverview.get(0), "Départ");
		
		//Zone de d??tection (5 m??tres) du point de d??part
		CircleOptions circleOptions;
		
		for(int i=0;i<this.listPointsToFollow.size();i++){
			setMarker(this.listPointsToFollow.get(i), "");
			circleOptions= new CircleOptions()
		    .center(this.listPointsToFollow.get(i))
		    .radius(Constantes.RADIUS_DETECTION);
			mMap.addCircle(circleOptions);
		}
		
		//CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPointsOverview.get(0), 15);
		//mMap.animateCamera(cu);
		//Log.d("DEBUUUUUG", listPointsOverview.get(0).toString());
		
		
		//Tra??age du trajet
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(Constantes.WIDTH_POLYLINE).color(Constantes.COLOR_POLYLINE);
		
		for (int i = 0; i < listPointsOverview.size(); i++) {
			options.add(listPointsOverview.get(i)); 
		}
		mMap.addPolyline(options);
		
		
		//On met le marker ?? l'arriv??e
		//setMarker(listPointsOverview.get(listPointsOverview.size() - 1), "Arrivee");
	}
	
	public void setMarker(LatLng point, String str) {
		mMap.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_marker_princ))
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
			mLocManag.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constantes.MIN_TIME_GPS_REQUEST_MS, Constantes.MIN_DIST_GPS_REQUEST_M,
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
		//Lorsque la position de mon utilisateur change...
		public void onLocationChanged(Location location) {
			
			int indexCurrentPoint = mUserPos.getIndexPointToFollow();
			mUserPos.setCurrentPos(location.getLatitude() , location.getLongitude(), location.getBearing(),mMap);
			int dist=formatDist(mUserPos.distanceBetween(listPointsToFollow.get(indexCurrentPoint)));
			Step currentStep=mListSteps.get(indexCurrentPoint);
			Step nextStep=null;
			if(indexCurrentPoint<listPointsToFollow.size()){
				nextStep=mListSteps.get(indexCurrentPoint+1);
			}

			if(!mUserPos.isOnRoute()){
				
				if(dist<Constantes.RADIUS_DETECTION && compteurAffichage==0){
					compteurAffichage++;
					//Toast.makeText(GPSRunner.this, "Départ",Toast.LENGTH_SHORT).show();
					
					String text=Html.fromHtml(currentStep.getHtml_instructions()).toString()+". puis, "+Html.fromHtml(nextStep.getHtml_instructions()).toString();
					textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					showLayout();
					displayInstructions(nextStep);
					mUserPos.setIsOnRoute(true);
					mUserPos.setToNextPointToFollow();
				}
				
			}

			else{
				displayDist(dist);
				if(500<dist && dist<=1000 && !speak1000){
					speak1000=true;
					textToSpeech.speak("Dans "+dist+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(200<dist && dist<=500 && !speak500){
					speak500=true;
					textToSpeech.speak("Dans "+dist+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(50<dist && dist<=200 && !speak200){
					speak200=true;
					textToSpeech.speak("Dans "+dist+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(dist<=50 && !speak50){
					speak50=true;
					textToSpeech.speak("Dans "+dist+" mètres ,"+Html.fromHtml(mListSteps.get(indexCurrentPoint).getHtml_instructions()).toString(), TextToSpeech.QUEUE_ADD, null);
				}
				if(dist<Constantes.RADIUS_DETECTION){
					speak1000=false;
					speak200=false;
					speak50=false;
					speak500=false;
					if(indexCurrentPoint<listPointsToFollow.size()){
						mUserPos.setToNextPointToFollow();
						displayInstructions(nextStep);
						textToSpeech.speak(Html.fromHtml(currentStep.getHtml_instructions()).toString()+" puis, dans "+dist+" mètres "+Html.fromHtml(mListSteps.get(indexCurrentPoint+1).getHtml_instructions()).toString(), TextToSpeech.QUEUE_FLUSH, null);
					}else{
						mUserPos.setIsOnRoute(false);
						/*
						 * Quitter ?
						 */
					}
				}
			}

		}
		public void displayDist(int dist){
			TextView tv_DistNextPoint = (TextView)findViewById(R.id.tv_distNextPoint);
			tv_DistNextPoint.setText(dist+"m");
		}
		
		public void displayInstructions(Step cStep){
			TextView tv_Instructions = (TextView)findViewById(R.id.tv_instructions);
			tv_Instructions.setText(Html.fromHtml(cStep.getHtml_instructions()));
		}
		
		public void showLayout(){
			LinearLayout ll_DistInstr=(LinearLayout)findViewById(R.id.centralLinLay);
			ll_DistInstr.setVisibility(View.VISIBLE);
		}
		
		public int formatDist(int d){
			int lastDigit=d%10;
			
			switch(lastDigit){
				case 1: case 2: case 3:
					d=d-lastDigit;
				break;
				
				case 4: case 6:
					d=d-lastDigit+5;
				break;
				
				case 7: case 8 : case 9:
					d=d-lastDigit+10;
				break;
			}
			
			return d;
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
