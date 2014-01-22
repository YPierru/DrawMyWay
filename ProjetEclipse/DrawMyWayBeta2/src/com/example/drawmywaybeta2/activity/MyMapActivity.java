package com.example.drawmywaybeta2.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.example.drawmywaybeta2.GeocodeJSONParser;
import com.example.drawmywaybeta2.AsyncTasks.GettingRoute;
import com.example.drawmywaybeta2.AsyncTasks.NearestStreet;
import com.example.drawmywaybeta2.Parcours.Trajet;
import com.example.drawmywaybeta2.Parcours.Downloaded.DirectionsResponse;
import com.example.gmapstests.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MyMapActivity extends Activity {

	private GoogleMap map;
	private EditText etPlace;
	private Button mBtnFind, btnA, btnL, btnT, btnR, btnS, btnG;
	private ArrayList<Polyline> listPolyline;
	private Trajet currentTrajet;
	// private ObjectContainer db;
	private static final String DB_NAME = "DrawMyWay.db4o";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullscreenActivity();
		setContentView(R.layout.layout_map);
		listPolyline = new ArrayList<Polyline>();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// map.setMyLocationEnabled(true);
		// db =
		// Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DB_NAME);

		// removeAllInDb4o();

		/*
		 * ObjectSet list=db.query(new Predicate<Trajet>() { public boolean
		 * match(Trajet tj) { return tj.getName().equals("Trajet1"); } });
		 * 
		 * 
		 * if(list.hasNext()){ Trajet tj=(Trajet)list.next();
		 * 
		 * // tst.show(); CameraUpdate cu = CameraUpdateFactory
		 * .newLatLngZoom(tj.getStartPoint(), 14); // map.moveCamera(cu);
		 * map.animateCamera(cu, 600, null);
		 * 
		 * map.addMarker(new MarkerOptions() .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.icon_green)) .anchor(0.0f, 1.0f)
		 * .position(tj.getStartPoint()) .title("Départ")) .showInfoWindow();
		 * 
		 * ArrayList<LatLng> lp = tj.getListPoint(); for(int
		 * i=0;i<lp.size()-1;i++){ map.addPolyline(new
		 * PolylineOptions().geodesic(false) .add(lp.get(i))
		 * .add(lp.get(i+1)).width(15) .color(Color.argb(120, 0, 0, 221))); }
		 * 
		 * map.addMarker(new MarkerOptions() .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.icon_green)) .anchor(0.0f, 1.0f)
		 * .position(tj.getEndPoint()) .title("Arrivée")) .showInfoWindow(); }
		 */

		settingMapClickListener();

		settingBtnSaveTrajetListener();

		settingBtnMapStyleListener();

		settingBtnLockMovListener();

		settingBtnArriveAddrListener();

		settingBtnEraseLineListener();

		settingSearchBarListener();

		settingBtnGPS();

	}

	private void removeAllInDb4o() {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + DB_NAME);
		ObjectSet listToRemove = db.queryByExample(DirectionsResponse.class);
		while (listToRemove.hasNext()) {
			db.delete((DirectionsResponse) listToRemove.next());
		}
		db.close();
	}

	private void fullscreenActivity() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void settingMapClickListener() {
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				LatLng lastPoint = currentTrajet.getLastPoint();
				new GettingRoute().execute(lastPoint, point);
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DirectionsResponse segmentRoad = GettingRoute.getDR();
				currentTrajet.getListSegment().add(segmentRoad);
				ArrayList<LatLng> tmpListLatLng = GettingRoute.getRoute();
				Log.d("DEBUUUUUG", "" + tmpListLatLng.size());
				currentTrajet.getListPoint().addAll(tmpListLatLng);
				// Toast.makeText(getApplicationContext(),"size list ="+tmpListLatLng.size(),Toast.LENGTH_SHORT).show();
				for (int i = 0; i < tmpListLatLng.size() - 1; i++) {
					Polyline p = map.addPolyline(new PolylineOptions()
							.geodesic(false).add(tmpListLatLng.get(i))
							.add(tmpListLatLng.get(i + 1)).width(15)
							.color(Color.argb(120, 0, 0, 221)));
					listPolyline.add(p);
				}
			}
		});

		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				currentTrajet = new Trajet("TemporaryName", false);
				btnS = (Button) findViewById(R.id.btn_Save);
				btnS.setEnabled(true);
				map.clear();
				new NearestStreet().execute(point);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				point = NearestStreet.getPoint();
				currentTrajet.getListPoint().add(point);
				Toast.makeText(getApplicationContext(), "Trajet créé",
						Toast.LENGTH_SHORT).show();
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, 17);
				map.animateCamera(cu, 600, null);

				map.addMarker(
						new MarkerOptions()
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.icon_green))
								.anchor(0.0f, 1.0f) // Anchors the
													// marker on the
													// bottom left
								.position(point).title("Départ"))
						.showInfoWindow();
			}
		});
	}

	private void settingBtnSaveTrajetListener() {
		btnS = (Button) findViewById(R.id.btn_Save);

		btnS.setEnabled(false);
		btnS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(
						MyMapActivity.this).setTitle("Saisir le nom du trajet");
				final EditText input = new EditText(getApplicationContext());
				input.setHint("Nom du trajet");
				input.setTextColor(Color.BLACK);
				alert.setView(input);
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString()
										.trim();
								currentTrajet.setName(value);
								ObjectContainer db = Db4oEmbedded.openFile(
										Db4oEmbedded.newConfiguration(),
										Environment
												.getExternalStorageDirectory()
												.getAbsolutePath()
												+ "/" + DB_NAME);
								db.store(currentTrajet);
								db.commit();
								db.close();
								Toast.makeText(
										getApplicationContext(),
										"Trajet " + currentTrajet.getName()
												+ " save", Toast.LENGTH_SHORT)
										.show();
							}
						});
				alert.show();

			}
		});
	}

	private void settingBtnMapStyleListener() {
		btnT = (Button) findViewById(R.id.btn_mapStyle);

		btnT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
					map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				} else {
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				}
			}
		});

		btnLongClickToast(btnT, "Change le type de map : normal/hybride");

	}

	private void settingBtnLockMovListener() {
		btnL = (Button) findViewById(R.id.btn_lock);
		btnL.setTextColor(Color.GREEN);

		btnL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (map.getUiSettings().isScrollGesturesEnabled()) {
					map.getUiSettings().setAllGesturesEnabled(false);
					btnL.setTextColor(Color.RED);
				} else {
					map.getUiSettings().setAllGesturesEnabled(true);
					btnL.setTextColor(Color.GREEN);
				}
			}
		});

		btnLongClickToast(btnL, "Active/Désactive les mouvements de la carte");
	}

	private void settingBtnArriveAddrListener() {
		btnA = (Button) findViewById(R.id.btn_arrivee);

		btnA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnS = (Button) findViewById(R.id.btn_Save);
				btnS.setEnabled(false);
				if (listPolyline.size() > 0) {
					map.addMarker(
							new MarkerOptions()
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.icon_green))
									.anchor(0.0f, 1.0f)
									.position(currentTrajet.getLastPoint())
									.title("Arrivée")).showInfoWindow();

					currentTrajet.setFinish(true);

					final AlertDialog.Builder alert = new AlertDialog.Builder(
							MyMapActivity.this)
							.setTitle("Saisir le nom du trajet");
					final EditText input = new EditText(getApplicationContext());
					input.setHint("Nom du trajet");
					input.setTextColor(Color.BLACK);
					alert.setView(input);
					alert.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = input.getText().toString()
											.trim();
									currentTrajet.setName(value);
									ObjectContainer db = Db4oEmbedded.openFile(
											Db4oEmbedded.newConfiguration(),
											Environment
													.getExternalStorageDirectory()
													.getAbsolutePath()
													+ "/" + DB_NAME);
									db.store(currentTrajet);
									db.commit();
									db.close();
									Toast.makeText(
											getApplicationContext(),
											"Trajet (fini) "
													+ currentTrajet.getName()
													+ " sauvegardé",
											Toast.LENGTH_SHORT).show();
									listPolyline.clear();
								}
							});
					alert.show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Il faut avoir tracé un parcours !",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		btnLongClickToast(btnA, "Ajoute un marqueur \"Arrivée\"");
	}

	private void settingSearchBarListener() {
		mBtnFind = (Button) findViewById(R.id.btn_show);

		// Getting reference to EditText
		etPlace = (EditText) findViewById(R.id.et_place);

		// Setting click event listener for the find button
		mBtnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Getting the place entered
				String location = etPlace.getText().toString();

				if (location == null || location.equals("")) {
					Toast.makeText(getBaseContext(), "No Place is entered",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String url = "https://maps.googleapis.com/maps/api/geocode/json?";

				try {
					// encoding special characters like space in the user input
					// place
					location = URLEncoder.encode(location, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				String address = "address=" + location;

				String sensor = "sensor=false";

				// url , from where the geocoding data is fetched
				url = url + address + "&" + sensor;

				// Instantiating DownloadTask to get places from Google
				// Geocoding service
				// in a non-ui thread
				DownloadTask downloadTask = new DownloadTask();

				// Start downloading the geocoding places
				downloadTask.execute(url);

			}
		});
	}

	public void settingBtnEraseLineListener() {
		btnR = (Button) findViewById(R.id.btn_eraseLine);

		btnR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listPolyline.size() > 0) {
					listPolyline.get(listPolyline.size() - 1).remove();
					listPolyline.remove(listPolyline.size() - 1);
					currentTrajet.getListPoint().remove(
							currentTrajet.getLastPoint());
				} else {
					Toast.makeText(getApplicationContext(),
							"Plus rien à effacer !", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnLongClickToast(btnR, "Efface la dernière ligne tracée");
	}

	public void settingBtnGPS() {
		btnG = (Button) findViewById(R.id.btn_LaunchGPS);
		// btnG.setEnabled(false);
		btnG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toGPS = new Intent(MyMapActivity.this, GPSRunner.class);
				toGPS.putExtra("TRAJET", currentTrajet);
				startActivity(toGPS);
			}
		});

	}

	public void btnLongClickToast(Button btn, CharSequence cs) {
		final CharSequence chs = cs;
		btn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(getApplicationContext(), chs, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;

	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {

			// Instantiating ParserTask which parses the json data from
			// Geocoding webservice
			// in a non-ui thread
			ParserTask parserTask = new ParserTask();

			// Start parsing the places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}

	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
			map.clear();

			for (int i = 0; i < list.size(); i++) {

				// Creating a marker
				MarkerOptions markerOptions = new MarkerOptions();

				// Getting a place from the places list
				HashMap<String, String> hmPlace = list.get(i);

				// Getting latitude of the place
				double lat = Double.parseDouble(hmPlace.get("lat"));

				// Getting longitude of the place
				double lng = Double.parseDouble(hmPlace.get("lng"));

				// Getting name
				String name = hmPlace.get("formatted_address");

				LatLng latLng = new LatLng(lat, lng);

				// Setting the position for the marker
				markerOptions.position(latLng);

				// Setting the title for the marker
				markerOptions.title(name);

				markerOptions.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_green));

				// Placing a marker on the touched position
				map.addMarker(markerOptions);

				// Locate the first location
				if (i == 0)
					map.animateCamera(
							CameraUpdateFactory.newLatLngZoom(latLng, 14), 600,
							null);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
