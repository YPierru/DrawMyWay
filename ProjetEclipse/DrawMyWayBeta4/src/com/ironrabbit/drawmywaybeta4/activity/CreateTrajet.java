package com.ironrabbit.drawmywaybeta4.activity;

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
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
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
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.Decoder;
import com.ironrabbit.drawmywaybeta4.GeocodeJSONParser;
import com.ironrabbit.drawmywaybeta4.asyncTasks.GettingRoute;
import com.ironrabbit.drawmywaybeta4.trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta4.trajet.Trajet;
import com.ironrabbit.drawmywaybeta4.trajet.TrajetAdapter;
import com.ironrabbit.drawmywaybeta4.trajet.downloaded.DirectionsResponse;
import com.ironrabbit.drawmywaybeta4.trajet.downloaded.Legs;

/*
 * Activity principale
 */
public class CreateTrajet extends SherlockActivity {

	private GoogleMap map;
	private EditText etPlace;
	private Polyline myPolyline;
	private ArrayList<Marker> listMarkers;
	private ArrayList<LatLng> listRealPoints; //Liste des points overview_polyline
	//private AllTrajets allTraj;
	private int idCurrentTrajet;
	private Trajet currentTrajet;
	static CreateTrajet thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map);
		thisActivity=this;

		currentTrajet = getIntent().getExtras().getParcelable("nouveau_trajet");
		listMarkers = new ArrayList<Marker>();
		//Initialisation des objets
		myPolyline = null;
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		getSupportActionBar().setTitle(currentTrajet.getName());

		// map.setMyLocationEnabled(true);
		
		//M??thodes initialisant les comportements de l'??cran
		settingMapLongClickListener(false);
		settingMapClickListener(false);
		settingBtnValidate();
		settingBtnEnableCorrectionModeListener();
		settingSearchBarListener();

	}
	
	public static CreateTrajet getInstance(){
		return thisActivity;
	}

	private String getCurrentDayTime() {
		Date aujourdhui = new Date();

		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.SHORT);

		return shortDateFormat.format(aujourdhui);
	}


	/*
	 * Comportement lors d'un long click sur la map,
	 * selon le mode courant (CorrectionMode ou pas)
	 */
	private void settingMapLongClickListener(boolean isCorrectionMode) {
		if (!isCorrectionMode) {
			map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng point) {
					// On efface tout sur la map ainsi que dans les listes
					// concern??es (longClick=nouveau trajet)
					map.clear();
					// listJalons.clear();
					listMarkers.clear();

					// On positionne la cam??ra sur le point click??
					CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point,
							16);
					map.animateCamera(cu, 600, null);

					/*
					 * On ajoute le jalon en LatLng. Dans lmb car le jalon peut
					 * ????tre pos?? wanegain
					 */
					listMarkers.add(setMarker(point, "Départ", true));
					currentTrajet.getListMarkers().add(point.latitude, point.longitude);
					// On active l'ajout de marker pour les jalons
				}
			});
		} else {
			map.setOnMapLongClickListener(null);
		}
	}

	/*
	 * Comportement lors d'un click court (click simple),
	 * selon le mode courant (CorrectionMode ou pas)
	 */
	private void settingMapClickListener(boolean isCorrectionMode) {

		if (!isCorrectionMode) {
			Button btnR = (Button) findViewById(R.id.btn_correctionMode);
			btnR.setTextColor(Color.RED);
			map.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					Button btnV = (Button) findViewById(R.id.btn_validate);
					btnV.setEnabled(true);
					findViewById(R.id.btn_correctionMode).setEnabled(true);
					// listJalons.add(point);
					listMarkers.add(setMarker(point, "Jalon posé", true));
					currentTrajet.getListMarkers().add(point.latitude, point.longitude);
				}
			});
		} else {
			map.setOnMapClickListener(null);
		}
	}

	/*
	 * Valide le trajet => appel ???? l'API google DirectionsResponse
	 * pour avoir tout le trajet.
	 */
	private void settingBtnValidate() {
		Button btnV = (Button) findViewById(R.id.btn_validate);
		//btnV.setEnabled(false);

		btnV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Button btnR = (Button) findViewById(R.id.btn_correctionMode);
				if (btnR.getTextColors().getDefaultColor() == Color.GREEN) {
					btnR.performClick();
				}

				if (myPolyline != null) {
					myPolyline.remove();
				}


				new GettingRoute().execute(currentTrajet.getListMarkersLatLng());
				try {
					Thread.sleep(3500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// On r??cup????re tout notre trajet
				DirectionsResponse myRoad = GettingRoute.getDR();
				currentTrajet.getListSegment().clear();
				currentTrajet.getListSegment().add(myRoad);
				//tj.setDraw(true);

				// listRealPoints.clear();
				// Liste de tout les points du trajet (overview_polyline)
				listRealPoints = Decoder.decodePoly(myRoad.getRoutes().get(0).getOverview_polyline().getPoints());

				// Le bloc ci-dessous permet de r??cup??rer les coo LatLng des
				// Markers apr??s correction de google
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
				currentTrajet.setListMarkersLatLng(tmpPoints);
				// Met les Marker ?? leur nouvelle place
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
				currentTrajet.setPointsWhoDrawsPolylineLatLng(listRealPoints);
				currentTrajet.setValidate(true);
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
	

	private void settingSearchBarListener() {
		Button mBtnFind = (Button) findViewById(R.id.btn_show);

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

	/*
	 * Active le mode correction.
	 * Dans ce mode, seul les jalons sont visibles.
	 * Il suffit de toucher un jalon pour le supprimer.
	 */
	public void settingBtnEnableCorrectionModeListener() {
		Button btnR = (Button) findViewById(R.id.btn_correctionMode);
		btnR.setEnabled(false);
		btnR.setTextColor(Color.RED);

		btnR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findViewById(R.id.btn_validate).setEnabled(false);
				Button btnR = (Button) v;
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
							"Mode correction activ??", Toast.LENGTH_SHORT)
							.show();
				} else {
					btnR.setTextColor(Color.RED);
					settingMapLongClickListener(false);
					settingMapClickListener(false);
					Toast.makeText(getApplicationContext(),
							"Mode correction d??sactiv??", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		btnLongClickToast(btnR, "Efface le dernier jalon trac??");
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

	/* A class to parse the Geocoding Places in non-ui thread */
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

				/* Getting the parsed data as a an ArrayList */
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

	/*
	 * Ajoute les items.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem item_endTrajet = menu.add("Terminer le trajet").setIcon(R.drawable.okgreen);
		item_endTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_endTrajet.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AllTrajets at = AllTrajets.getInstance();
				currentTrajet.setSave(true);
				at.add(currentTrajet);
				at.saveAllTrajet();
				CreateTrajet.getInstance().finish();
				return false;
			}
		});

		MenuItem item_quit = menu.add("Quitter").setIcon(R.drawable.deleteicon);
		item_quit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_quit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateTrajet.this);
				//alertDialogBuilder.setTitle("Your Title");
				alertDialogBuilder
						.setMessage("Voulez-vous sauvegarder "+currentTrajet.getName()+" ?\nVous perdrez toutes vos modifications.")
						.setCancelable(false)
						.setPositiveButton("Oui",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										AllTrajets at = AllTrajets.getInstance();
										currentTrajet.setSave(true);
										at.add(currentTrajet);
										at.saveAllTrajet();
										CreateTrajet.getInstance().finish();
									}
								})
						.setNegativeButton("Non",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										CreateTrajet.getInstance().finish();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return false;
			}
		});
		
		
		SubMenu subMenu1 = menu.addSubMenu("Type de carte");
        subMenu1.add("Hybride").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				return false;
			}
		});
        subMenu1.add("Normal").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				return false;
			}
		});
        subMenu1.add("Satellite").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				return false;
			}
		});
        subMenu1.add("Terrain").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				return false;
			}
		});
        MenuItem mapType_item = subMenu1.getItem();
        mapType_item.setIcon(R.drawable.maps);
        mapType_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);


		return true;
	}
}
