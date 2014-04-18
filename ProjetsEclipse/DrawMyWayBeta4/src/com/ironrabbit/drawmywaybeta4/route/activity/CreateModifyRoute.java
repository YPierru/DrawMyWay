package com.ironrabbit.drawmywaybeta4.route.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.widget.RadialMenuActivity.BlueCircle;
import com.example.widget.RadialMenuActivity.CircleOptions;
import com.example.widget.RadialMenuActivity.GreenCircle;
import com.example.widget.RadialMenuActivity.IconOnly;
import com.example.widget.RadialMenuActivity.Menu1;
import com.example.widget.RadialMenuActivity.Menu2;
import com.example.widget.RadialMenuActivity.Menu3;
import com.example.widget.RadialMenuActivity.NewTestMenu;
import com.example.widget.RadialMenuActivity.RedCircle;
import com.example.widget.RadialMenuActivity.StringAndIcon;
import com.example.widget.RadialMenuActivity.StringOnly;
import com.example.widget.RadialMenuActivity.YellowCircle;
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
import com.ironrabbit.drawmywaybeta4.asyncTasks.GettingRoute;
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.RoutesCollection;
import com.navdrawer.SimpleSideDrawer;

/*
 * Create/Modify/Watch trajet
 */
public class CreateModifyRoute extends Activity {

	private GoogleMap mMap;
	private Polyline mPolyline;
	private ArrayList<Marker> mListMarkers;
	private ArrayList<LatLng> mListOverviewPolylinePoints; // Liste des points
															// overview_polyline
	private Button btn_dessiner, btn_correctionmode, btn_finirTrajet;
	private Point mPoint;
	private String mMode;
	private Route mRoute;
	static CreateModifyRoute thisActivity;
	private SimpleSideDrawer mSlidingMenu;// Slide menu à droite

	private RadialMenuWidget mWheelMenu;
	private LinearLayout mLLForRMW;
	private MenuItem mItemWheelMenu;
	private boolean isWheelEnable,canBeDraw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_create);
		thisActivity = this;

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mLLForRMW = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		addContentView(mLLForRMW, params);
		isWheelEnable = false;
		canBeDraw=false;

		// Initialisation du SSD. On lui affecte le layout pour le côté droit.
		mSlidingMenu = new SimpleSideDrawer(this);
		mSlidingMenu.setRightBehindContentView(R.layout.side_menu_map);

		// On recupère : le trajet ainsi que le "mode" :
		// Modification : le trajet est "en cours"
		// Création : on créer un nouveau trajet.
		mRoute = getIntent().getExtras().getParcelable("trajet");
		mMode = getIntent().getExtras().getString("MODE");
		mListMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		// mMap.setMyLocationEnabled(true);
		if (mMode.equals("Modification")) {
			settingMapClickListener(true);
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

		btn_dessiner = (Button) findViewById(R.id.btn_dessiner);
		btn_correctionmode = (Button) findViewById(R.id.btn_correctionMode);
		btn_finirTrajet = (Button) findViewById(R.id.btn_finirTrajet);

		// Initialisation des objets
		mPolyline = null;

		// map.setMyLocationEnabled(true);

		// Méthodes initialisant les comportements de l'écran
		settingMapLongClickListener(false);
		// settingMapClickListener(false);

		mMap.setOnMarkerDragListener(new ActionDragMarker());

		mPoint = new Point();
		getWindowManager().getDefaultDisplay().getSize(mPoint);
		Button btn = (Button) findViewById(R.id.btn_slideMenuCM);
		btn.setOnTouchListener(new ActionOnTouchForSlide());

		getActionBar().setTitle(mRoute.getName());

	}

	public static CreateModifyRoute getInstance() {
		return thisActivity;
	}

	/*
	 * Comportement lors d'un long click sur la map, selon le mode courant
	 * (CorrectionMode ou pas)
	 */
	private void settingMapLongClickListener(boolean isCorrectionMode) {
		if (!isCorrectionMode) {
			mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng point) {
					// On efface tout sur la map ainsi que dans les listes
					// concern??es (longClick=nouveau trajet)
					mMap.clear();
					// listJalons.clear();
					mListMarkers.clear();

					// On positionne la cam??ra sur le point click??
					CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point,
							16);
					mMap.animateCamera(cu, 600, null);

					/*
					 * On ajoute le jalon en LatLng.
					 */
					mListMarkers.add(putMarker(point, "Départ", true));
					mRoute.getListMarkers().clear();
					mRoute.getListMarkers()
							.add(point.latitude, point.longitude);
					settingMapClickListener(false);
					// On active l'ajout de marker pour les jalons
				}
			});
		} else {
			mMap.setOnMapLongClickListener(null);
		}
	}

	/*
	 * Comportement lors d'un click court (click simple), selon le mode courant
	 * (CorrectionMode ou pas)
	 */
	private void settingMapClickListener(boolean isCorrectionMode) {

		if (!isCorrectionMode) {
			Button btnR = (Button) findViewById(R.id.btn_correctionMode);
			btnR.setTextColor(Color.RED);
			mMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					findViewById(R.id.btn_correctionMode).setEnabled(true);
					// listJalons.add(point);
					mListMarkers.add(putMarker(point, "Jalon posé", true));
					btn_dessiner.setEnabled(true);
					canBeDraw=true;
					mRoute.getListMarkers()
							.add(point.latitude, point.longitude);
				}
			});
		} else {
			mMap.setOnMapClickListener(null);
		}
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

	@Override
	public void onBackPressed() {
		actionIfUserWantsBack();
	}

	/*
	 * Comportement lors du click sur le bouton "home"
	 */
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getItemId() == android.R.id.home) {
			actionIfUserWantsBack();
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

		MenuItem item_help = menu.add("Aide").setIcon(R.drawable.help);
		item_help.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_help.setOnMenuItemClickListener(new OnMenuItemClickListener() {

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

		// Permet de faire apparaitre le menu droit.
		/*
		 * MenuItem item_showSideMenu = menu.add("Menu droit").setIcon(
		 * R.drawable.sidemenu);
		 * item_showSideMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 * item_showSideMenu .setOnMenuItemClickListener(new
		 * OnMenuItemClickListener() {
		 * 
		 * @Override public boolean onMenuItemClick(MenuItem item) {
		 * mSlidingMenu.toggleRightDrawer();
		 * 
		 * behaviorButtonDraw(); behaviorButtonCorrectionMode();
		 * behaviorButtonFinishRoute();
		 * behaviorButtonsMapType(R.id.btn_mapNormal,
		 * GoogleMap.MAP_TYPE_NORMAL);
		 * behaviorButtonsMapType(R.id.btn_mapHybride,
		 * GoogleMap.MAP_TYPE_HYBRID);
		 * behaviorButtonsMapType(R.id.btn_mapSatellite,
		 * GoogleMap.MAP_TYPE_SATELLITE);
		 * behaviorButtonsMapType(R.id.btn_mapTerrain,
		 * GoogleMap.MAP_TYPE_TERRAIN);
		 * 
		 * return false; } });
		 */

		// Permet de faire apparaitre le menu droit.
		mItemWheelMenu = menu.add("Menu wheel").setIcon(R.drawable.sidemenu);
		mItemWheelMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mItemWheelMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						// Log.d("DEBUUUUUG",
						// "ISDAMODUFKINWHEELENABLE ? => "+isWheelEnable);

						if (!isWheelEnable) {
							isWheelEnable = true;
							mWheelMenu = new RadialMenuWidget(getBaseContext());
							// PieMenu.setAnimationSpeed(0L);
							int xLayoutSize = mLLForRMW.getHeight();
							int yLayoutSize = mLLForRMW.getWidth();
							mWheelMenu.setSourceLocation(xLayoutSize,
									yLayoutSize);
							//PieMenu.setCenterLocation(xLayoutSize,yLayoutSize);
							mWheelMenu.setIconSize(15, 30);
							mWheelMenu.setTextSize(13);

							mWheelMenu.setCenterCircle(new WheelMenu("Close", true, android.R.drawable.ic_menu_close_clear_cancel));
							if(canBeDraw){
								mWheelMenu.addMenuEntry(new WheelMenu("Dessiner", true, 0));
							}
							mWheelMenu.addMenuEntry(new WheelMenu("Terminer", true, 0));
							mWheelMenu.addMenuEntry(new WheelMenu("Correction", true, 0));
							mWheelMenu.addMenuEntry(new MapTypeMenu());
							// mWheelMenu.addMenuEntry(new CircleOptions());
							// mWheelMenu.addMenuEntry(new Menu2());
							// mWheelMenu.addMenuEntry(new Menu3());

							mLLForRMW.addView(mWheelMenu);
						} else {
							((LinearLayout) mWheelMenu.getParent())
									.removeView(mWheelMenu);
							isWheelEnable = false;
						}

						return false;
					}
				});

		return true;
	}

	public void behaviorButtonDraw() {
		btn_dessiner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_correctionmode.getTextColors().getDefaultColor() == Color.GREEN) {
					btn_correctionmode.performClick();
				}

				if (mPolyline != null) {
					mPolyline.remove();
				}

				mSlidingMenu.toggleRightDrawer();

				GettingRoute getRoute = new GettingRoute(
						CreateModifyRoute.this, mRoute,
						mListOverviewPolylinePoints, mListMarkers, mPolyline,
						mMap);

				getRoute.execute();
			}
		});
	}

	public void behaviorButtonCorrectionMode() {
		btn_correctionmode.setTextColor(Color.RED);
		btn_correctionmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_correctionmode.getTextColors().getDefaultColor() == Color.RED) {
					btn_correctionmode.setTextColor(Color.GREEN);
					if (mPolyline != null) {
						mPolyline.remove();
					}
					settingMapClickListener(true);
					settingMapLongClickListener(true);
					mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

						@Override
						public boolean onMarkerClick(Marker marker) {
							marker.hideInfoWindow();
							for (int i = 0; i < mListMarkers.size(); i++) {
								if (mListMarkers.get(i).getId()
										.equals(marker.getId())) {
									mListMarkers.remove(i);
									mRoute.setListMarkersMk(mListMarkers);
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
					btn_correctionmode.setTextColor(Color.RED);
					settingMapLongClickListener(false);
					settingMapClickListener(false);
					Toast.makeText(getApplicationContext(),
							"Mode correction désactiv??", Toast.LENGTH_SHORT)
							.show();
				}
				mSlidingMenu.toggleRightDrawer();
			}
		});
	}

	public void behaviorButtonFinishRoute() {
		btn_finirTrajet = (Button) findViewById(R.id.btn_finirTrajet);
		btn_finirTrajet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RoutesCollection at = RoutesCollection.getInstance();
				mRoute.setSave(true);
				if (!at.replace(mRoute)) {
					at.add(mRoute);
				}
				at.saveAllTrajet();
				ListRoutes.updateDataList();
				CreateModifyRoute.getInstance().finish();
			}
		});
	}

	public void behaviorButtonsMapType(int btnId, final int mapType) {
		Button btn = (Button) findViewById(btnId);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMap.setMapType(mapType);
				mSlidingMenu.toggleRightDrawer();
			}
		});
	}

	private class ActionOnTouchForSlide implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mSlidingMenu.toggleRightDrawer();

				behaviorButtonDraw();
				behaviorButtonCorrectionMode();
				behaviorButtonFinishRoute();
				behaviorButtonsMapType(R.id.btn_mapNormal,
						GoogleMap.MAP_TYPE_NORMAL);
				behaviorButtonsMapType(R.id.btn_mapHybride,
						GoogleMap.MAP_TYPE_HYBRID);
				behaviorButtonsMapType(R.id.btn_mapSatellite,
						GoogleMap.MAP_TYPE_SATELLITE);
				behaviorButtonsMapType(R.id.btn_mapTerrain,
						GoogleMap.MAP_TYPE_TERRAIN);
				break;
			}
			return false;
		}
	}

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

		public WheelMenu(String n, boolean c,int i){
			this.name=n;
			this.closeWheelWhenTouch=c;
			this.idIcon=i;
		}
		
		public String getName() {
			return getLabel();
		}

		public String getLabel() {
			if(this.name.equals("Close")){
				return null;
			}else{
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
			
			//Log.d("DEBUUUG", "yoooo !");
			
			if(this.name.equals("Normal")){
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}else if(this.name.equals("Hybride")){
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}else if(this.name.equals("Satellite")){
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}else if(this.name.equals("Terrain")){
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}else if(this.name.equals("Dessiner")){
				if (mPolyline != null) {
					mPolyline.remove();
				}

				GettingRoute getRoute = new GettingRoute(
						CreateModifyRoute.this, mRoute,
						mListOverviewPolylinePoints, mListMarkers, mPolyline,
						mMap);

				getRoute.execute();
			}else if(this.name.equals("Correction")){
				if (btn_correctionmode.getTextColors().getDefaultColor() == Color.RED) {
					btn_correctionmode.setTextColor(Color.GREEN);
					if (mPolyline != null) {
						mPolyline.remove();
					}
					settingMapClickListener(true);
					settingMapLongClickListener(true);
					mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

						@Override
						public boolean onMarkerClick(Marker marker) {
							marker.hideInfoWindow();
							for (int i = 0; i < mListMarkers.size(); i++) {
								if (mListMarkers.get(i).getId()
										.equals(marker.getId())) {
									mListMarkers.remove(i);
									mRoute.setListMarkersMk(mListMarkers);
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
					btn_correctionmode.setTextColor(Color.RED);
					settingMapLongClickListener(false);
					settingMapClickListener(false);
					Toast.makeText(getApplicationContext(),
							"Mode correction désactiv??", Toast.LENGTH_SHORT)
							.show();
				}
			}else if(this.name.equals("Terminer")){
				if(mListMarkers.size()>0){
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
			
			if(this.closeWheelWhenTouch){
				((LinearLayout) mWheelMenu.getParent()).removeView(mWheelMenu);
				isWheelEnable = false;
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
				Arrays.asList(new WheelMenu("Normal",true,0), 
							new WheelMenu("Hybride",true,0),
							new WheelMenu("Satellite",true,0), 
							new WheelMenu("Terrain",true,0)));

		public List<RadialMenuEntry> getChildren() {
			return children;
		}

		public void menuActiviated() {
			// System.out.println( "New Test Menu Activated");
		}
	}

	
}
