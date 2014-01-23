package com.example.drawmywaybeta2.activity;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.drawmywaybeta2.Parcours.Trajet;
import com.example.drawmywaybeta2.Parcours.Downloaded.Step;
import com.example.gmapstests.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

public class GPSRunner extends Activity {

	private GoogleMap map;
	private Trajet myTrajet;
	private ArrayList<Step> listSteps;
	private ArrayList<Polyline> listPoly;
	private int currentStepIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_gps);
		
		listPoly = new ArrayList<Polyline>();
		myTrajet = getIntent().getExtras().getParcelable("TRAJET");
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		listSteps = myTrajet.getListSteps();
		drawParkour();
		currentStepIndex=0;
		setClickListeners();
		putStepOnScreen(currentStepIndex);
	}
	
	public void drawParkour(){
		ArrayList<LatLng> listPoints = myTrajet.getListPoint();

		//CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPoints.get(0), 17);
		LatLng point = listPoints.get(0);
		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(point).title("Départ"))
				.showInfoWindow();
		for(int i=0;i<listPoints.size()-1;i++){
			map.addPolyline(new PolylineOptions().geodesic(false)
					.add(listPoints.get(i))
					.add(listPoints.get(i + 1)).width(15)
					.color(Color.argb(120, 0, 0, 221)));
		}
		
		point = listPoints.get(listPoints.size()-1);

		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(point).title("Arrivée"));
	}
	
	public void setClickListeners(){
		TextView viewRight = (TextView)findViewById(R.id.vitesseMoy);
		viewRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentStepIndex<listSteps.size()-1){
					currentStepIndex++;
					putStepOnScreen(currentStepIndex);
				}
			}
		});
		

		LinearLayout viewLeft = (LinearLayout)findViewById(R.id.leftLinearLayout);
		viewLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(currentStepIndex>0){
					currentStepIndex--;
					putStepOnScreen(currentStepIndex);
				}
			}
		});
		
		final Chronometer tempsEcoule = (Chronometer)findViewById(R.id.dureeTotal);
		//tempsEcoule.setText(DateFormat.f)
		tempsEcoule.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tempsEcoule.start();
			}
		});
	}
	
	public void putStepOnScreen(int index){
		/*for(int i=0;i<listPoly.size();i++){
			listPoly.get(0).remove();
		}
		if(listPoly.size()!=0){
			listPoly.clear();
		}*/
		Step myStep = listSteps.get(index);
		ArrayList<LatLng> segment = decodePoly(myStep.getPolyline().getPoints());
		LatLng beginPoint = segment.get(0);
		LatLng endPoint = segment.get(segment.size()-1);
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(beginPoint, 17);
		map.animateCamera(cu, 600, null);
		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(beginPoint).title("Départ"))
				.showInfoWindow();
		for(int i=0;i<segment.size()-1;i++){
			Polyline p=map.addPolyline(new PolylineOptions().geodesic(false)
						.add(segment.get(i))
						.add(segment.get(i+1)).width(15)
						.color(Color.argb(255, 0, 0, 221)));
			listPoly.add(p);
		}
		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(endPoint).title("Départ"));
		TextView htmlInstr_view = (TextView)findViewById(R.id.html_instructions);
		htmlInstr_view.setText(myStep.getHtml_instructions());
		
		TextView distanceAvantChangement_view = (TextView)findViewById(R.id.distanceAvantChangement);
		distanceAvantChangement_view.setText(myStep.getDistance().getText());
		
		TextView kilometrageEffectue_view = (TextView)findViewById(R.id.kilometrageEffectue);
		kilometrageEffectue_view.setText("0m");
		
		//TextView chrono_view = (TextView)findViewById(R.id.dureeTotal);
		//chrono_view.setText("00' 00''");
		
		TextView kilometrageTrajet_view = (TextView)findViewById(R.id.kilometrageTrajet);
		kilometrageTrajet_view.setText(myTrajet.getDistTotal()+"m");
		
		DateTime dt =new DateTime();
		int dureeSecond = myTrajet.getDureeTotal();
		int heures=dt.getHourOfDay()+(dureeSecond / 3600);
		int minutes=dt.getMinuteOfHour()+((dureeSecond % 3600) / 60);

		TextView heureFin_view = (TextView)findViewById(R.id.heureFin);
		heureFin_view.setText(heures+"h"+minutes);
	}
	
	private ArrayList<LatLng> decodePoly(String encoded) {

		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			LatLng ll = new LatLng(p.getLatitudeE6() / 1E6,
					p.getLongitudeE6() / 1E6);
			// System.out.println(ll.latitude+" "+ll.longitude);
			poly.add(ll);
		}

		return poly;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gpsrunner, menu);
		return true;
	}

}
