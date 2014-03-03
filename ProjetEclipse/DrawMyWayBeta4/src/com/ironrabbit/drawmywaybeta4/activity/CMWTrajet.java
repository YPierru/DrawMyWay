package com.ironrabbit.drawmywaybeta4.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.ironrabbit.drawmywaybeta4.asyncTasks.GettingRoute;
import com.ironrabbit.drawmywaybeta4.trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta4.trajet.Trajet;
import com.ironrabbit.drawmywaybeta4.trajet.downloaded.DirectionsResponse;
import com.ironrabbit.drawmywaybeta4.trajet.downloaded.Legs;

/*
 * Create/Modify/Watch trajet
 */
public class CMWTrajet extends SherlockActivity {

	private GoogleMap map;
	private EditText etPlace;
	private Polyline myPolyline;
	private ArrayList<Marker> listMarkers;
	private ArrayList<LatLng> listRealPoints; //Liste des points overview_polyline
	//private AllTrajets allTraj;
	private Button btn_validate,btn_correctionmode;
	private MenuItem item_endTrajet, item_quit, item_maptype;
	private int idCurrentTrajet;
	private String mode;
	private Trajet currentTrajet;
	static CMWTrajet thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map);
		thisActivity=this;

		currentTrajet = getIntent().getExtras().getParcelable("trajet");
		mode = getIntent().getExtras().getString("MODE");
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		listMarkers = new ArrayList<Marker>();
		btn_validate= (Button) findViewById(R.id.btn_validate);
		btn_correctionmode = (Button) findViewById(R.id.btn_correctionMode);
		
		if(mode.equals("Voir")){
			ArrayList<LatLng> al = currentTrajet.getListMarkersLatLng();
			ArrayList<LatLng> alp = currentTrajet.getPointsWhoDrawsPolylineLatLng();
			PolylineOptions options = new PolylineOptions().geodesic(false)
					.width(15).color(Color.argb(120, 0, 0, 221));
			for (int i = 0; i < alp.size(); i++) {
				options.add(alp.get(i));
			}
			myPolyline = map.addPolyline(options);
			for(int i=0;i<al.size();i++){
				listMarkers.add(setMarker(al.get(i), "point", false));
			}
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(al.get(0),
					15);
			map.animateCamera(cu, 600, null);
			btn_validate.setVisibility(View.GONE);
			btn_correctionmode.setVisibility(View.GONE);
		}else{
			
			if(mode.equals("Modification")){
				ArrayList<LatLng> al = currentTrajet.getListMarkersLatLng();
				for(int i=0;i<al.size();i++){
					listMarkers.add(setMarker(al.get(i), "point", true));
				}
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(al.get(0),
						15);
				map.animateCamera(cu, 600, null);
			}
			
			//Initialisation des objets
			myPolyline = null;
	
			// map.setMyLocationEnabled(true);
			
			//M??thodes initialisant les comportements de l'??cran
			settingMapLongClickListener(false);
			settingMapClickListener(false);
			settingBtnValidate();
			settingBtnEnableCorrectionModeListener();
		}
		
		getSupportActionBar().setTitle("["+mode+"]"+currentTrajet.getName());

	}
	
	public static CMWTrajet getInstance(){
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
	 * Valide le trajet => appel à l'API google DirectionsResponse
	 * pour avoir tout le trajet.
	 */
	private void settingBtnValidate() {
		//btnV.setEnabled(false);

		btn_validate.setOnClickListener(new OnClickListener() {

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

	/*
	 * Active le mode correction.
	 * Dans ce mode, seul les jalons sont visibles.
	 * Il suffit de toucher un jalon pour le supprimer.
	 */
	public void settingBtnEnableCorrectionModeListener() {

		btn_correctionmode.setEnabled(false);
		btn_correctionmode.setTextColor(Color.RED);

		btn_correctionmode.setOnClickListener(new OnClickListener() {

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

		btnLongClickToast(btn_correctionmode, "Efface le dernier jalon trac??");
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

	public void setAllInvisible(){
		
	}
	
	/*
	 * Ajoute les items.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		item_endTrajet = menu.add("Terminer le trajet").setIcon(R.drawable.okgreen);
		item_endTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_endTrajet.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AllTrajets at = AllTrajets.getInstance();
				currentTrajet.setSave(true);
				at.add(currentTrajet);
				at.saveAllTrajet();
				Welcome.updateDataList();
				CMWTrajet.getInstance().finish();
				return false;
			}
		});

		item_quit = menu.add("Quitter").setIcon(R.drawable.deleteicon);
		item_quit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_quit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CMWTrajet.this);
				alertDialogBuilder.setTitle("Attention");
				alertDialogBuilder
						.setMessage("Vous allez perdre toutes vos modifications.")
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										CMWTrajet.getInstance().finish();
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
				return false;
			}
		});
		
		if(mode.equals("Voir")){
			item_endTrajet.setVisible(false);
			item_quit.setVisible(false);
		}
		
		
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
        item_maptype = subMenu1.getItem();
        item_maptype.setIcon(R.drawable.maps);
        item_maptype.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);


		return true;
	}
}
