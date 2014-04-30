package com.ironrabbit.drawmywaybeta4.route.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.widget.RadialMenuWidget;
import com.example.widget.RadialMenuWidget.RadialMenuEntry;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.PlaceJSONParser;
import com.ironrabbit.drawmywaybeta4.asyncTasks.GettingRoute;
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.RoutesCollection;

/*
 * Create/Modify/Watch trajet
 */
public class CreateModifyRoute extends Activity {

	private GoogleMap mMap;
	private Polyline mPolyline;
	private ArrayList<Marker> mListMarkers;
	private ArrayList<LatLng> mListOverviewPolylinePoints; // Liste des points
															// overview_polyline
	private String mMode;
	private Route mRoute;
	static CreateModifyRoute thisActivity;
	private RadialMenuWidget mWheelMenu;
	private LinearLayout mLinearLayourWheel;
	private AutoCompleteTextView atvPlaces;
	private MenuItem itemWheelMenu, itemSearch, itemHelp;
	private boolean wheelEnable, canBeDraw, correctionEnable, onSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_create);

		// Instanciation/Récupération des objets
		thisActivity = this;
		mLinearLayourWheel = (LinearLayout) findViewById(R.id.linearLayoutForWheel);
		onSearch = false;
		wheelEnable = false;
		canBeDraw = false;
		correctionEnable = false;
		mListMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mPolyline = null;
		mRoute = getIntent().getExtras().getParcelable("trajet");

		// Modification de la action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(mRoute.getName());

		// Action si montrajet reçu est en cours
		traitementsSiTrajetEnCours();

		// Initialisation des listener
		settingMapLongClickListenerNormal();
		mMap.setOnMarkerDragListener(new ActionDragMarker());

	}

	private void traitementsSiTrajetEnCours() {
		mMode = getIntent().getExtras().getString("MODE");

		if (mMode.equals("Modification")) {
			settingMapClickListenerNomal();
			ArrayList<LatLng> tmpListMarkers = mRoute.getListMarkersLatLng();
			for (int i = 0; i < tmpListMarkers.size(); i++) {
				if (i == 0) {
					mListMarkers.add(putMarker(tmpListMarkers.get(i), "Départ",
							true));
				} else {
					mListMarkers
							.add(putMarker(tmpListMarkers.get(i), "", true));
				}
			}
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
					tmpListMarkers.get(tmpListMarkers.size() - 1), 15);
			mMap.animateCamera(cu, 600, null);
		}
	}

	public static CreateModifyRoute getInstance() {
		return thisActivity;
	}

	/*
	 * Comportement lors d'un long click sur la map, selon le mode courant
	 * (CorrectionMode ou pas)
	 */
	private void settingMapLongClickListenerNormal() {
		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				// On efface tout sur la map ainsi que dans les listes
				// concern??es (longClick=nouveau trajet)
				mMap.clear();
				mListMarkers.clear();

				// On positionne la cam??ra sur le point click??
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, 16);
				mMap.animateCamera(cu, 600, null);

				// On ajoute le jalon en LatLng.
				mListMarkers.add(putMarker(point, "Départ", true));
				mRoute.getListMarkers().clear();
				mRoute.getListMarkers().add(point.latitude, point.longitude);

				// On initialise le listener pour le click simple
				settingMapClickListenerNomal();
			}
		});
	}

	/*
	 * Comportement lors d'un click court (click simple), selon le mode courant
	 * (CorrectionMode ou pas)
	 */
	private void settingMapClickListenerNomal() {
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				mListMarkers.add(putMarker(point, "Jalon posé", true));
				canBeDraw = true;
				mRoute.getListMarkers().add(point.latitude, point.longitude);
			}
		});
	}

	// Supprime de façon propre le listener du click simple
	private void settingMapClickListenerCorrectionMode() {
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
			}
		});
	}

	/*
	 * Ajout un marker sur la carte
	 */
	private Marker putMarker(LatLng p, String str, boolean isDrag) {
		Marker tmp = mMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_green))
				.anchor(0.0f, 1.0f) // Anchors the
									// marker on the
									// bottom left
				.position(p).title(str));
		tmp.setDraggable(isDrag);
		tmp.showInfoWindow();
		return tmp;
	}

	@Override
	public void onBackPressed() {
		actionIfUserWantsBack();
	}

	public void setPolyline(Polyline p) {
		this.mPolyline = p;
	}

	/*
	 * Comportement lors du click sur le bouton "home"
	 */
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getItemId() == android.R.id.home) {
			if (onSearch) {
				closeSearchBar();
			} else {
				actionIfUserWantsBack();
			}
		}
		return true;
	}

	public void actionIfUserWantsBack() {

		if (mListMarkers.size() > 0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					CreateModifyRoute.this);
			alertDialogBuilder.setTitle("Attention");
			alertDialogBuilder
					.setMessage("Vous allez perdre toutes vos modifications.")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									onBackPressed();
									CreateModifyRoute.getInstance().finish();
								}
							})
					.setNegativeButton("Annuler",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * Ajoute les items.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		itemSearch = menu.add("Recherche").setIcon(R.drawable.ic_action_search);
		itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemSearch.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				openSearchBar();
				return true;

			}
		});

		itemHelp = menu.add("Aide").setIcon(R.drawable.help);
		itemHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemHelp.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						CreateModifyRoute.this);
				alertDialog.setTitle("Aide");
				alertDialog
						.setMessage(
								Html.fromHtml("<b>Appui long</b> : efface tout sur la carte et place un <u>point de départ</u>"
										+ "<br/><b>Appui simple</b> : place un point par lequel <u>vous voulez passer</u>"))
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).create();
				alertDialog.show();
				return false;
			}
		});

		// Permet de faire apparaitre la roue
		itemWheelMenu = menu.add("Menu wheel").setIcon(R.drawable.sidemenu);
		itemWheelMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		itemWheelMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				if (!wheelEnable) {

					// Création de la wheel
					wheelEnable = true;
					mWheelMenu = new RadialMenuWidget(getBaseContext());
					int xLayoutSize = mLinearLayourWheel.getHeight();
					int yLayoutSize = mLinearLayourWheel.getWidth();
					mWheelMenu.setSourceLocation(xLayoutSize, yLayoutSize);
					mWheelMenu.setIconSize(15, 30);
					mWheelMenu.setTextSize(13);

					// Initialisation selon les états des flags
					mWheelMenu.setCenterCircle(new WheelMenu("Close", true,
							android.R.drawable.ic_menu_close_clear_cancel));
					if (canBeDraw) {
						mWheelMenu.addMenuEntry(new WheelMenu("Dessiner", true,
								0));
					}
					mWheelMenu.addMenuEntry(new WheelMenu("Terminer", true, 0));
					if (mListMarkers.size() > 1 || correctionEnable) {
						mWheelMenu.addMenuEntry(new WheelMenu("Correction",
								true, 0));
					}
					mWheelMenu.addMenuEntry(new MapTypeMenu());

					mLinearLayourWheel.addView(mWheelMenu);
				}
				// Suppression
				else {
					((LinearLayout) mWheelMenu.getParent())
							.removeView(mWheelMenu);
					wheelEnable = false;
				}

				return false;
			}
		});

		return true;
	}

	public void openSearchBar() {

		
		onSearch = true;
		itemHelp.setVisible(false);
		itemWheelMenu.setVisible(false);
		itemSearch.setVisible(false);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.ic_action_search);

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_search, null);

		actionBar.setCustomView(v);

		// On r��cup��re l'autocompletetextview
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_search_places);
		
		showKeyboard();
		
		// Permet de d��clencher une action �� chaque caract��re tap��
		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Lorsque le texte change on part chercher les r��sultats
				PlacesTask placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void closeSearchBar() {

		closeKeyboard();
		onSearch = false;
		itemSearch.setVisible(true);
		itemHelp.setVisible(true);
		itemWheelMenu.setVisible(true);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setIcon(R.drawable.gps);
	}
	
	
	public void showKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(atvPlaces, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public void closeKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
	}
	

	// Telecharge des donnees en json depuis une url passee en param
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

			StringBuilder sb = new StringBuilder();

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

	/*
	 * Ci-dessous sont liste les differentes classes privees : -
	 * ActionDragMarker, utilise lorsqu'on deplace un marker - WheelMenu et
	 * MapTypeMenu, permettant de creer la roue - Les differentes AsyncTasks
	 * utilisees pour la recherche de lieux
	 */

	/*
	 * Lorsqu'un marker est deplacé, on actualise la liste des marker du trajet
	 */
	private class ActionDragMarker implements OnMarkerDragListener {

		@Override
		public void onMarkerDrag(Marker arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMarkerDragEnd(Marker arg0) {
			mRoute.setListMarkersMk(mListMarkers);
		}

		@Override
		public void onMarkerDragStart(Marker arg0) {
			// TODO Auto-generated method stub

		}

	}

	/*
	 * Ci-dessous les menu de la roue
	 */

	public class WheelMenu implements RadialMenuEntry {

		private String name;
		private boolean closeWheelWhenTouch;
		private int idIcon;

		public WheelMenu(String n, boolean c, int i) {
			this.name = n;
			this.closeWheelWhenTouch = c;
			this.idIcon = i;
		}

		public String getName() {
			return getLabel();
		}

		public String getLabel() {
			if (this.name.equals("Close")) {
				return null;
			} else {
				return this.name;
			}
		}

		public int getIcon() {
			return this.idIcon;
		}

		public List<RadialMenuEntry> getChildren() {
			return null;
		}

		public void menuActiviated() {

			if (this.name.equals("Normal")) {
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			} else if (this.name.equals("Hybride")) {
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			} else if (this.name.equals("Satellite")) {
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			} else if (this.name.equals("Terrain")) {
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			} else if (this.name.equals("Dessiner")) {
				actionDraw();
			} else if (this.name.equals("Correction")) {
				actionCorrection();
			} else if (this.name.equals("Terminer")) {
				actionFinish();
			}

			if (this.closeWheelWhenTouch) {
				((LinearLayout) mWheelMenu.getParent()).removeView(mWheelMenu);
				wheelEnable = false;
			}
		}

		public void actionDraw() {
			if (mPolyline != null) {
				mPolyline.remove();
			}

			if (correctionEnable) {
				correctionEnable = false;
				settingMapClickListenerNomal();
			}

			GettingRoute getRoute = new GettingRoute(CreateModifyRoute.this,
					mRoute, mListOverviewPolylinePoints, mListMarkers, mMap);

			getRoute.execute();
		}

		public void actionFinish() {
			if (mListMarkers.size() > 0) {
				RoutesCollection at = RoutesCollection.getInstance();
				mRoute.setSave(true);
				if (!at.replace(mRoute)) {
					at.add(mRoute);
				}
				at.saveAllTrajet();
				ListRoutes.updateDataList();
			}
			CreateModifyRoute.getInstance().finish();
		}

		public void actionCorrection() {
			if (!correctionEnable) {

				correctionEnable = true;
				mRoute.setValidate(false);
				if (mPolyline != null) {
					mPolyline.remove();
				}
				settingMapClickListenerCorrectionMode();
				mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						marker.hideInfoWindow();
						for (int i = 0; i < mListMarkers.size(); i++) {
							if (mListMarkers.get(i).getId()
									.equals(marker.getId())) {
								if (i == 0) {
									Toast.makeText(
											getApplicationContext(),
											"Pour supprimer le point de départ, faites un appui long sur une autre zone",
											Toast.LENGTH_SHORT).show();
								} else {
									mListMarkers.remove(i);
									mRoute.setListMarkersMk(mListMarkers);
									marker.remove();
								}
								break;
							}
						}

						if (mListMarkers.size() == 0) {
							canBeDraw = false;
						}
						return false;
					}
				});
				Toast.makeText(getApplicationContext(),
						"Mode correction activé", Toast.LENGTH_SHORT).show();
			} else {
				correctionEnable = false;
				settingMapClickListenerNomal();
				Toast.makeText(getApplicationContext(),
						"Mode correction désactivé", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class MapTypeMenu implements RadialMenuEntry {
		public String getName() {
			return "NewTestMenu";
		}

		public String getLabel() {
			return "Changer\ncarte";
		}

		public int getIcon() {
			return 0;
		}

		private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>(
				Arrays.asList(new WheelMenu("Normal", true, 0), new WheelMenu(
						"Hybride", true, 0),
						new WheelMenu("Satellite", true, 0), new WheelMenu(
								"Terrain", true, 0)));

		public List<RadialMenuEntry> getChildren() {
			return children;
		}

		public void menuActiviated() {
		}
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {

			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyB4m_X5XwwnhYenzLhIexv-glVWu-j_Egs";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			String components = "components=country:fr";

			// Building the parameters to the web service
			String parameters = input + "&" + types + "&" + components + "&"
					+ sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}

			return data;
		}

		@Override
		// Une fois les places t��l��charg��s => �� la fin
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			ParserTask parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };
			final List<HashMap<String, String>> finalResults = result;

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			atvPlaces.setAdapter(adapter);
			atvPlaces.showDropDown();
			atvPlaces.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					
					String ref = finalResults.get(position).get("reference");
					ParserUrlTask parserUrlTask = new ParserUrlTask();
					parserUrlTask.execute(ref.toString());
					closeSearchBar();
				}

			});
		}
	}

	private class ParserUrlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyB4m_X5XwwnhYenzLhIexv-glVWu-j_Egs";

			String reference = "";

			try {
				reference = "reference=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched

			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = reference + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/details/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from we service
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;

		}

		@Override
		// Une fois les places t��l��charg��s => �� la fin
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			try {
				JSONObject locations = new JSONObject(result)
						.getJSONObject("result").getJSONObject("geometry")
						.getJSONObject("location");
				double lat = Double.parseDouble(locations.getString("lat"));
				double lng = Double.parseDouble(locations.getString("lng"));

				// Log.d("DEBU", lat);
				// Log.d("DEBU", lng);

				// Toast.makeText(getApplicationContext(),
				// adrAdress+"\n"+lat+" - "+lng, Toast.LENGTH_SHORT).show();
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15);
				mMap.animateCamera(cu, 600, null);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
