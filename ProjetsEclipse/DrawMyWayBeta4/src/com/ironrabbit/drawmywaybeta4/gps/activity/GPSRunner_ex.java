package com.ironrabbit.drawmywaybeta4.gps.activity;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.downloaded.Step;

/*
 * Le GPS de l'application
 */
public class GPSRunner_ex extends SherlockActivity {

	private GoogleMap mMap;
	private Route mRoute;
	private Marker meMarker;
	private ArrayList<Step> mListSteps;
	private ArrayList<LatLng> mListPoints;
	private int mCurrentStepIndex;
	private int currentPt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gps);
		// getSupportActionBar().hide();
		/*mRoute = getIntent().getExtras().getParcelable("TRAJET");

		mMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapGPS)).getMap();

		mListSteps = mRoute.getListSteps();
		mListPoints = new ArrayList<LatLng>();
		for (int i = 0; i < mListSteps.size(); i++) {
			mListPoints.add(new LatLng(mListSteps.get(i).getStart_location()
					.getLat(), mListSteps.get(i).getStart_location().getLng()));
			if(i+1==mListSteps.size()){
				mListPoints.add(new LatLng(mListSteps.get(i).getEnd_location()
						.getLat(), mListSteps.get(i).getEnd_location().getLng()));
			}
		}
		
		Log.d("DEBUUUUUUUG", ""+mListPoints.size());

		drawParkour();// Dessine le trajet sur la map
		mCurrentStepIndex = 0;
		setClickListeners();// Permet de passer d'une step e une autre
		putStepOnScreen();// Affiche les details de la step courante sur l'ecran
		*/
	}

	public void drawParkour() {
		/*ArrayList<LatLng> pts = mRoute.getPointsWhoDrawsPolylineLatLng();

		// Marker du debut
		setMarker(pts.get(0), "Depart");

		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pts.get(0), 16);
		mMap.animateCamera(cu, 600, null);

		// Dessin du trajet
		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(15).color(Color.argb(120, 0, 0, 221));
		for (int i = 0; i < pts.size(); i++) {
			options.add(pts.get(i));
		}
		mMap.addPolyline(options);

		// Marker cense representer l'user
		meMarker = mMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.android))
				.position(pts.get(0)).flat(true));

		// Marker de fin
		setMarker(pts.get(pts.size() - 1), "Arrivee");*/
	}

	public void setMarker(LatLng point, String str) {
		/*mMap.addMarker(
				new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_green))
						.anchor(0.0f, 1.0f) // Anchors the
											// marker on the
											// bottom left
						.position(point).title(str)).showInfoWindow();*/
	}

	public void setClickListeners() {
		/*// On touch sur la droite, on passe e la step suivante
		TextView viewRight = (TextView) findViewById(R.id.vitesseMoy);
		viewRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mCurrentStepIndex < mListSteps.size() - 1) {
					mCurrentStepIndex++;
					putStepOnScreen();
				}
			}
		});

		// On touch sur la gauche, on passe e la step precedente
		LinearLayout viewLeft = (LinearLayout) findViewById(R.id.leftLinearLayout);
		viewLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mCurrentStepIndex > 0) {
					mCurrentStepIndex--;
					putStepOnScreen();
				}
			}
		});*/

	}

	public void putStepOnScreen() {
		/*// Deplace le marker "me" selon la step courante
		Step myStep = mListSteps.get(mCurrentStepIndex);
		LatLng point = new LatLng(myStep.getStart_location().getLat(), myStep
				.getStart_location().getLng());
		meMarker.setPosition(point);
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, 16);
		mMap.animateCamera(cu, 600, null);

		initGPSInfoPanel(myStep);*/
	}

	private void initGPSInfoPanel(Step myStep) {
		/*TextView htmlInstr_view = (TextView) findViewById(R.id.html_instructions);
		htmlInstr_view.setText(Jsoup.clean(myStep.getHtml_instructions(),
				new Whitelist()));

		TextView distanceAvantChangement_view = (TextView) findViewById(R.id.distanceAvantChangement);
		distanceAvantChangement_view.setText(myStep.getDistance().getText());

		TextView kilometrageEffectue_view = (TextView) findViewById(R.id.kilometrageEffectue);
		kilometrageEffectue_view.setText("0m");

		// TextView chrono_view = (TextView)findViewById(R.id.dureeTotal);
		// chrono_view.setText("00' 00''");

		TextView kilometrageTrajet_view = (TextView) findViewById(R.id.kilometrageTrajet);
		kilometrageTrajet_view.setText(mRoute.getDistTotal() + "m");

		DateTime dt = new DateTime();
		int dureeSecond = mRoute.getDureeTotal();
		int heures = dt.getHourOfDay() + (dureeSecond / 3600);
		int minutes = dt.getMinuteOfHour() + ((dureeSecond % 3600) / 60);

		TextView heureFin_view = (TextView) findViewById(R.id.heureFin);
		heureFin_view.setText(heures + "h" + minutes);*/
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		/*MenuItem item_animate = menu.add("Animation").setIcon(R.drawable.gps);
		item_animate.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_animate.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				Log.d("DEBUUUUUUUUUG", "ici");
				mMap.animateCamera(
						CameraUpdateFactory.newLatLng(mListPoints.get(0)),
						MyCancelableCallback);
				currentPt = 0;

				return false;
			}
		});*/

		return true;
	}
	
	/*CancelableCallback MyCancelableCallback = new CancelableCallback() {

		@Override
		public void onCancel() {
		}

		@Override
		public void onFinish() {
			Log.d("DEBUUUUUUUUUG", "l??");
			if (++currentPt < mListPoints.size()) {
				mMap.animateCamera(
						CameraUpdateFactory.newLatLng(mListPoints.get(currentPt)),
						MyCancelableCallback);
			}
		}

	};*/

}
