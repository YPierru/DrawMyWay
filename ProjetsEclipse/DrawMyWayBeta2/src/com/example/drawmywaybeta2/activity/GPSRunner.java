package com.example.drawmywaybeta2.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
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

public class GPSRunner extends Activity {

	private GoogleMap map;
	private Trajet myTrajet;
	private ArrayList<Step> listSteps;
	private ArrayList<Polyline> listPoly;
	private int currentStepIndex;
	private HashMap<Integer, ArrayList<PolylineOptions>> hmapIndexStepPolyline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_gps);

		listPoly = new ArrayList<Polyline>();
		hmapIndexStepPolyline = new HashMap<Integer, ArrayList<PolylineOptions>>();
		myTrajet = getIntent().getExtras().getParcelable("TRAJET");
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		listSteps = myTrajet.getListSteps();
		drawParkour();
		currentStepIndex = 0;
		setClickListeners();
		putStepOnScreen();
	}

	public void drawParkour() {
		ArrayList<LatLng> allPoints= myTrajet.getListPoint();
		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(allPoints.get(0)).title("Départ")).showInfoWindow();

		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(allPoints.get(0), 16);
		map.animateCamera(cu, 600, null);
		
		PolylineOptions options = new PolylineOptions()
										.geodesic(false)
										.width(15)
										.color(Color.argb(120, 0, 0, 221));
		for(int i=0;i<allPoints.size();i++){
			options.add(allPoints.get(i));
		}
		map.addPolyline(options);
		
		map.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(myTrajet.getLastPoint()).title("Arrivée")).showInfoWindow();
	}

	public void setClickListeners() {
		TextView viewRight = (TextView) findViewById(R.id.vitesseMoy);
		viewRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (currentStepIndex < listSteps.size() - 1) {
					currentStepIndex++;
					putStepOnScreen();
				}
			}
		});

		LinearLayout viewLeft = (LinearLayout) findViewById(R.id.leftLinearLayout);
		viewLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (currentStepIndex > 0) {
					currentStepIndex--;
					putStepOnScreen();
				}
			}
		});

		/*
		 * final Chronometer tempsEcoule =
		 * (Chronometer)findViewById(R.id.dureeTotal);
		 * //tempsEcoule.setText(DateFormat.f)
		 * tempsEcoule.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { tempsEcoule.start(); } });
		 */
	}

	public void putStepOnScreen() {
		Step myStep = listSteps.get(currentStepIndex);
		LatLng point = new LatLng(myStep.getStart_location().getLat(), myStep.getStart_location().getLng());
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, 16);
		map.animateCamera(cu, 600, null);
		
		initGPSInfoPanel(myStep);
	}
	
	private void initGPSInfoPanel(Step myStep){
		TextView htmlInstr_view = (TextView) findViewById(R.id.html_instructions);
		htmlInstr_view.setText(Jsoup.clean(myStep.getHtml_instructions(), new Whitelist()));

		TextView distanceAvantChangement_view = (TextView) findViewById(R.id.distanceAvantChangement);
		distanceAvantChangement_view.setText(myStep.getDistance().getText());

		TextView kilometrageEffectue_view = (TextView) findViewById(R.id.kilometrageEffectue);
		kilometrageEffectue_view.setText("0m");

		// TextView chrono_view = (TextView)findViewById(R.id.dureeTotal);
		// chrono_view.setText("00' 00''");

		TextView kilometrageTrajet_view = (TextView) findViewById(R.id.kilometrageTrajet);
		kilometrageTrajet_view.setText(myTrajet.getDistTotal() + "m");

		DateTime dt = new DateTime();
		int dureeSecond = myTrajet.getDureeTotal();
		int heures = dt.getHourOfDay() + (dureeSecond / 3600);
		int minutes = dt.getMinuteOfHour() + ((dureeSecond % 3600) / 60);

		TextView heureFin_view = (TextView) findViewById(R.id.heureFin);
		heureFin_view.setText(heures + "h" + minutes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gpsrunner, menu);
		return true;
	}

}
