package com.ironrabbit.drawmywaybeta3.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta3.Trajet.Trajet;

public class TrajetDetails extends SherlockActivity {

	private GoogleMap map;
	private Trajet myTrajet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_details);

		myTrajet = getIntent().getExtras().getParcelable("trajet_for_details");
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapTrajetDetails)).getMap();

		drawOnMap();
		setDataOnScreen();

	}

	public void drawOnMap() {
		ArrayList<LatLng> listPoints = myTrajet
				.getPointsWhoDrawsPolylineLatLng();
		setMarker(listPoints.get(0), "Départ", false);
		setMarker(listPoints.get(listPoints.size() - 1), "Départ", false);

		PolylineOptions options = new PolylineOptions().geodesic(false)
				.width(15).color(Color.argb(120, 0, 0, 221));
		for (int i = 0; i < listPoints.size(); i++) {
			options.add(listPoints.get(i));
		}
		map.addPolyline(options);
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(listPoints.get(0), 14);
		map.animateCamera(cu, 600, null);
	}

	public void setDataOnScreen() {
		initTV(R.id.tv_crea, "Créé le " + myTrajet.getDateCreation());
		initTV(R.id.tv_nom, myTrajet.getName());
		initTV(R.id.tv_mod, "Modifié le " + myTrajet.getDateDerModif());
		double dist = myTrajet.getDistTotal();
		if (dist < 1000) {
			initTV(R.id.tv_dist, (int) dist + "m");
		} else {
			initTV(R.id.tv_dist, (dist / 1000) + "Km");
		}
		initTV(R.id.tv_dur, "" + myTrajet.getDureeTotal());
		initTV(R.id.tv_addrDeb, myTrajet.getStartAddress());
		initTV(R.id.tv_addrFin, myTrajet.getEndAddress());
	}

	public void initTV(int idTV, String data) {
		TextView tv = (TextView) findViewById(idTV);
		tv.setText(data);
	}

	private Marker setMarker(LatLng p, String str, boolean isDrag) {
		Marker tmp = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_green))
				.anchor(0.0f, 1.0f) // Anchors the
									// marker on the
									// bottom left
				.position(p).title(str));
		tmp.setDraggable(isDrag);
		return tmp;
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		/*MenuItem item_Correction = menu.add("Correct").setIcon(
				R.drawable.android);
		item_Correction.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_Correction
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent toMyMapActivity = new Intent(TrajetDetails.this,
								MyMapActivity.class);
						toMyMapActivity.putExtra("trajet_for_correction",
								(Parcelable) myTrajet);
						startActivity(toMyMapActivity);
						return true;
					}
				});*/

		/*MenuItem item_GPS = menu.add("GPS").setIcon(R.drawable.android);
		item_GPS.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_GPS.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent toGPSRunner = new Intent(TrajetDetails.this,
						GPSRunner.class);
				toGPSRunner.putExtra("TRAJET", (Parcelable) myTrajet);
				startActivity(toGPSRunner);
				return true;
			}
		});*/

		/*MenuItem item_Rename = menu.add("Rename").setIcon(R.drawable.android);
		item_Rename.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_Rename.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(
						TrajetDetails.this).setTitle("Nouveau nom");
				final EditText input = new EditText(getApplicationContext());
				input.setHint(myTrajet.getName());
				input.setTextColor(Color.BLACK);
				alert.setView(input);
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString()
										.trim();
								myTrajet.setName(value);
								TextView tv=(TextView)findViewById(R.id.tv_nom);
								tv.setText(value);
							}
						});
				alert.show();
				return false;
			}
		});*/

		return true;
	}

}
