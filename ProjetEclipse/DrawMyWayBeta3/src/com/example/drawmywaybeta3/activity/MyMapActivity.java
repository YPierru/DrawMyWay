package com.example.drawmywaybeta3.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.example.drawmywaybeta3.Decoder;
import com.example.drawmywaybeta3.GeocodeJSONParser;
import com.example.drawmywaybeta3.AsyncTasks.GettingRoute;
import com.example.drawmywaybeta3.Trajet.AllTrajets;
import com.example.drawmywaybeta3.Trajet.Trajet;
import com.example.drawmywaybeta3.Trajet.TrajetManager;
import com.example.drawmywaybeta3.Trajet.Downloaded.DirectionsResponse;
import com.example.drawmywaybeta3.Trajet.Downloaded.Legs;
import com.example.gmapstests.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MyMapActivity extends SherlockActivity {

	private GoogleMap map;
	private EditText etPlace;
	private Button mBtnFind, btnL, btnT, btnR, btnS, btnG, btnV;
	private ArrayList<Polyline> listPolyline;
	private Polyline myPolyline;
	private ArrayList<Marker> listMarkers;
	private ArrayList<LatLng> listRealPoints;
	private AllTrajets allTraj;
	private int idCurrentTrajet;
	private DirectionsResponse myRoad;
	private static final String FILE_NAME = "AllTrajet.dmw";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//fullscreenActivity();
		setContentView(R.layout.layout_map);
		myPolyline = null;
		listPolyline = new ArrayList<Polyline>();
		listMarkers = new ArrayList<Marker>();
		allTraj = TrajetManager.loadAllTrajet();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// map.setMyLocationEnabled(true);

		settingMapLongClickListener(false);
		settingMapClickListener(false);

		settingBtnSaveTrajetListener();

		settingBtnLoad();

		settingBtnDelete();

		settingBtnMapStyleListener();

		settingBtnLockMovListener();

		settingBtnValidate();

		settingBtnEnableCorrectionModeListener();

		settingSearchBarListener();

		settingBtnGPS();

	}

	private void fullscreenActivity() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	private String getCurrentDayTime(){
		Date aujourdhui = new Date();

	    DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
	        DateFormat.SHORT,
	        DateFormat.SHORT);

	   return shortDateFormat.format(aujourdhui);
	}

	private void settingBtnLoad() {
		Button btn = (Button) findViewById(R.id.btn_load);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//allTraj = TrajetManager.loadAllTrajet();

				for (int i = 0; i < allTraj.size(); i++) {
					Trajet tj = allTraj.get(i);
					ArrayList<LatLng> listRP = tj
							.getPointsWhoDrawsPolylineLatLng();
					ArrayList<LatLng> listMk = tj.getListMarkersLatLng();
					setMarker(listMk.get(0), "Départ", false);
					setMarker(listMk.get(listMk.size() - 1), "Arrivée", false);

					for (int j = 0; j < listMk.size(); j++) {
						setMarker(
								new LatLng(listMk.get(j).latitude, listMk
										.get(j).longitude), "", true);
					}

					if (tj.isEstDessine()) {
						PolylineOptions options = new PolylineOptions()
								.geodesic(false).width(15)
								.color(Color.argb(120, 0, 0, 221));
						for (int j = 0; j < listRP.size(); j++) {
							options.add(listRP.get(j));
						}
						map.addPolyline(options);
					}
				}
				Trajet tj = allTraj.get(0);
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(tj
						.getListMarkersLatLng().get(0), 16);
				map.animateCamera(cu, 600, null);
				idCurrentTrajet = System.identityHashCode(allTraj.get(0));
			}
		});

	}

	private void settingBtnDelete() {
		Button btnD = (Button) findViewById(R.id.btn_delete);

		btnD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TrajetManager.deleteFile();
			}
		});
	}

	private void settingMapLongClickListener(boolean isCorrectionMode) {
		if (!isCorrectionMode) {
			map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng point) {
					// On créer notre trajet
					Trajet tj = new Trajet("TemporaryName", false, false, getCurrentDayTime());
					idCurrentTrajet = System.identityHashCode(tj);

					// On rend le bouton de sauvegarde utilisable
					btnS = (Button) findViewById(R.id.btn_Save);
					btnS.setEnabled(true);

					// On efface tout sur la map ainsi que dans les listes
					// concernées (longClick=nouveau trajet)
					map.clear();
					// listJalons.clear();
					listMarkers.clear();

					// On positionne la caméra sur le point clické
					CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point,
							16);
					map.animateCamera(cu, 600, null);

					/*
					 * On ajoute le jalon en LatLng. Dans lmb car le jalon peut
					 * être posé wanegain
					 */
					listMarkers.add(setMarker(point, "Départ", true));
					// listJalons.add(point);
					tj.getListMarkers().clear();
					tj.getListMarkers().add(point.latitude, point.longitude);
					allTraj.add(tj);

					// On active l'ajout de marker pour les jalons
				}
			});
		} else {
			map.setOnMapLongClickListener(null);
		}
	}

	private void settingMapClickListener(boolean isCorrectionMode) {

		if (!isCorrectionMode) {
			btnR = (Button) findViewById(R.id.btn_correctionMode);
			btnR.setTextColor(Color.RED);
			map.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					btnV.setEnabled(true);
					findViewById(R.id.btn_correctionMode).setEnabled(true);
					// listJalons.add(point);
					listMarkers.add(setMarker(point, "Jalon posé", true));
					Trajet tj = allTraj.getByHashId(idCurrentTrajet);
					tj.getListMarkers().add(point.latitude, point.longitude);

				}
			});
		} else {
			map.setOnMapClickListener(null);
		}
	}

	private void settingBtnValidate() {
		btnV = (Button) findViewById(R.id.btn_validate);
		btnV.setEnabled(false);

		btnV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnR = (Button) findViewById(R.id.btn_correctionMode);
				if (btnR.getTextColors().getDefaultColor() == Color.GREEN) {
					btnR.performClick();
				}

				if (myPolyline != null) {
					myPolyline.remove();
				}

				Trajet tj = allTraj.getByHashId(idCurrentTrajet);
				// On DL le trajet, depuis la DirectionsAPI, en passant une
				// liste de WayPoints
				/*
				 * ArrayList<LatLng> listWayPoints = new ArrayList<LatLng>();
				 * for (int i = 0; i < listMarkers.size(); i++) {
				 * listWayPoints.add(listMarkers.get(i).getPosition()); }
				 */
				Log.d("DEBUT", "" + tj.getListMarkersLatLng().size());
				new GettingRoute().execute(tj.getListMarkersLatLng());

				try {
					Thread.sleep(3500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// On récupère tout notre trajet
				myRoad = GettingRoute.getDR();
				tj.getListSegment().clear();
				tj.getListSegment().add(myRoad);
				tj.setEstDessine(true);

				// listRealPoints.clear();
				// Liste de tout les points du trajet (overview_polyline)
				listRealPoints = Decoder.decodePoly(myRoad.getRoutes().get(0)
						.getOverview_polyline().getPoints());

				// Le bloc ci-dessous permet de récupérer les coo LatLng des
				// Markers après correction de google
				List<Legs> listLegs = myRoad.getRoutes().get(0).getLegs();
				ArrayList<LatLng> tmpPoints = new ArrayList<LatLng>();
				for (int i = 0; i < listLegs.size(); i++) {
					tmpPoints.add(new LatLng(listLegs.get(i)
							.getStart_location().getLat(), listLegs.get(i)
							.getStart_location().getLng()));
					if (i + 1 == listLegs.size()) {
						tmpPoints.add(new LatLng(listLegs.get(i)
								.getEnd_location().getLat(), listLegs.get(i)
								.getEnd_location().getLng()));
					}
				}
				tj.setListMarkersLatLng(tmpPoints);
				// Met les Marker à leur nouvelle place
				for (int i = 0; i < listMarkers.size(); i++) {
					listMarkers.get(i).setPosition(tmpPoints.get(i));
				}
				// currentTrajet.setListMarker(listMarkers);

				// Log.d("DEBUUUUUUG","LT = "+tmpPoints.size()+" LMB = "+listMarkersBad.size());
				PolylineOptions options = new PolylineOptions().geodesic(false)
						.width(15).color(Color.argb(120, 0, 0, 221));
				for (int i = 0; i < listRealPoints.size(); i++) {
					options.add(listRealPoints.get(i));
				}
				myPolyline = map.addPolyline(options);
				tj.setPointsWhoDrawsPolylineLatLng(listRealPoints);
				allTraj.replace(tj);
				findViewById(R.id.btn_LaunchGPS).setEnabled(true);
			}
		});
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
		// tmp.showInfoWindow();
		return tmp;
	}

	private void settingBtnSaveTrajetListener() {

		btnS = (Button) findViewById(R.id.btn_Save);

		btnS.setEnabled(false);
		btnS.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Trajet tj = allTraj.getByHashId(idCurrentTrajet);
				if (!tj.isHasBeenSave()) {
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
									Trajet tj = allTraj
											.getByHashId(idCurrentTrajet);
									tj.setName(value);
									tj.setHasBeenSave(true);
									allTraj.replace(tj);
									TrajetManager.saveAllTrajet(allTraj);
									Toast.makeText(getApplicationContext(),
											"Trajet " + tj.getName() + " save",
											Toast.LENGTH_SHORT).show();
								}
							});
					alert.show();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
				} else {
					// allTraj.replace(tj);
					TrajetManager.saveAllTrajet(allTraj);
					Toast.makeText(getApplicationContext(),
							"Trajet " + tj.getName() + " save",
							Toast.LENGTH_SHORT).show();
				}

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

	public void settingBtnEnableCorrectionModeListener() {
		btnR = (Button) findViewById(R.id.btn_correctionMode);
		btnR.setEnabled(false);
		btnR.setTextColor(Color.RED);
		findViewById(R.id.btn_validate).setEnabled(false);
		findViewById(R.id.btn_LaunchGPS).setEnabled(false);

		btnR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btnR.getTextColors().getDefaultColor() == Color.RED) {
					btnR.setTextColor(Color.GREEN);
					if (myPolyline != null) {
						myPolyline.remove();
					}
					settingMapClickListener(true);
					settingMapLongClickListener(true);
					map.setOnMarkerClickListener(new OnMarkerClickListener() {

						@Override
						public boolean onMarkerClick(Marker marker) {
							marker.hideInfoWindow();
							for (int i = 0; i < listMarkers.size(); i++) {
								if (listMarkers.get(i).getId()
										.equals(marker.getId())) {
									listMarkers.remove(i);
									break;
								}
							}
							marker.remove();
							return false;
						}
					});
					Toast.makeText(getApplicationContext(),
							"Mode correction activé", Toast.LENGTH_SHORT)
							.show();
				} else {
					btnR.setTextColor(Color.RED);
					settingMapLongClickListener(false);
					settingMapClickListener(false);
					Toast.makeText(getApplicationContext(),
							"Mode correction désactivé", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		btnLongClickToast(btnR, "Efface le dernier jalon tracé");
	}

	public void settingBtnGPS() {
		btnG = (Button) findViewById(R.id.btn_LaunchGPS);
		// btnG.setEnabled(false);
		btnG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toGPS = new Intent(MyMapActivity.this, GPSRunner.class);
				toGPS.putExtra("TRAJET",
						(Parcelable) allTraj.getByHashId(idCurrentTrajet));
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

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem trajet_item = menu.add("Trajet").setIcon(
				R.drawable.android);

		trajet_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		trajet_item
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent toTrajetDisplay = new Intent(MyMapActivity.this, TrajetDisplayList.class);
						startActivity(toTrajetDisplay);
						return false;
					}
				});

		return true;
	}

}
