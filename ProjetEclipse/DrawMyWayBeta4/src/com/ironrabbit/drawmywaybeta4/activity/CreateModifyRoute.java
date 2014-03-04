package com.ironrabbit.drawmywaybeta4.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
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
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.RoutesCollection;
import com.ironrabbit.drawmywaybeta4.route.downloaded.DirectionsResponse;
import com.ironrabbit.drawmywaybeta4.route.downloaded.Legs;
import com.navdrawer.SimpleSideDrawer;

/*
 * Create/Modify/Watch trajet
 */
public class CreateModifyRoute extends SherlockActivity {

	private GoogleMap mMap;
	private Polyline mPolyline;
	private ArrayList<Marker> mListMarkers;
	private ArrayList<LatLng> mListOverviewPolylinePoints; //Liste des points overview_polyline
	private Button btn_dessiner,btn_correctionmode,btn_finirTrajet;
	private String mMode;
	private Route mRoute;
	static CreateModifyRoute thisActivity;
	private SimpleSideDrawer mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_create);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mSlidingMenu = new SimpleSideDrawer(this);
		mSlidingMenu.setRightBehindContentView(R.layout.side_menu_map);
		thisActivity=this;

		mRoute = getIntent().getExtras().getParcelable("trajet");
		mMode = getIntent().getExtras().getString("MODE");
		mListMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if(mMode.equals("Modification")){
			ArrayList<LatLng> tmpListMarkers = mRoute.getListMarkersLatLng();
			for(int i=0;i<tmpListMarkers.size();i++){
				if(i==0){
					mListMarkers.add(putMarker(tmpListMarkers.get(i), "Départ", true));
				}else{
					mListMarkers.add(putMarker(tmpListMarkers.get(i), "", true));
				}
			}
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(tmpListMarkers.get(tmpListMarkers.size()-1),
					15);
			mMap.animateCamera(cu, 600, null);
		}
		
		btn_dessiner= (Button) findViewById(R.id.btn_dessiner);
		btn_correctionmode = (Button) findViewById(R.id.btn_correctionMode);
		btn_finirTrajet = (Button) findViewById(R.id.btn_finirTrajet);
	
		//Initialisation des objets
		mPolyline = null;

		//map.setMyLocationEnabled(true);
		
		//Méthodes initialisant les comportements de l'écran
		settingMapLongClickListener(false);
		settingMapClickListener(false);	
		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMarkerDragEnd(Marker arg0) {
				mRoute.setListMarkersMk(mListMarkers);
			}
			
			@Override
			public void onMarkerDrag(Marker arg0) {
			}
		});
		
		getSupportActionBar().setTitle(mRoute.getName());

	}
	
	public static CreateModifyRoute getInstance(){
		return thisActivity;
	}


	/*
	 * Comportement lors d'un long click sur la map,
	 * selon le mode courant (CorrectionMode ou pas)
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
					 * On ajoute le jalon en LatLng. Dans lmb car le jalon peut
					 * ????tre pos?? wanegain
					 */
					mListMarkers.add(putMarker(point, "Départ", true));
					mRoute.getListMarkers().clear();
					mRoute.getListMarkers().add(point.latitude, point.longitude);
					// On active l'ajout de marker pour les jalons
				}
			});
		} else {
			mMap.setOnMapLongClickListener(null);
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
			mMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					findViewById(R.id.btn_correctionMode).setEnabled(true);
					// listJalons.add(point);
					mListMarkers.add(putMarker(point, "Jalon posé", true));
					mRoute.getListMarkers().add(point.latitude, point.longitude);
				}
			});
		} else {
			mMap.setOnMapClickListener(null);
		}
	}

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
	
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		
		if(menuItem.getItemId()==android.R.id.home){
			if(mListMarkers.size()>0){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateModifyRoute.this);
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
			}else{
				onBackPressed();
			}
		}
		return true;
	}
	
	/*
	 * Ajoute les items.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem item_showSideMenu = menu.add("Menu droit").setIcon(R.drawable.sidemenu);
		item_showSideMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_showSideMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mSlidingMenu.toggleRightDrawer();
				
				btn_dessiner.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (btn_correctionmode.getTextColors().getDefaultColor() == Color.GREEN) {
							btn_correctionmode.performClick();
						}

						if (mPolyline != null) {
							mPolyline.remove();
						}


						new GettingRoute().execute(mRoute.getListMarkersLatLng());
						try {
							Thread.sleep(3500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// On r??cup????re tout notre trajet
						DirectionsResponse myRoad = GettingRoute.getDR();
						mRoute.getListSegment().clear();
						mRoute.getListSegment().add(myRoad);
						//tj.setDraw(true);

						// listRealPoints.clear();
						// Liste de tout les points du trajet (overview_polyline)
						mListOverviewPolylinePoints = Decoder.decodePoly(myRoad.getRoutes().get(0).getOverview_polyline().getPoints());

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
						mRoute.setListMarkersLatLng(tmpPoints);
						// Met les Marker ?? leur nouvelle place
						for (int i = 0; i < mListMarkers.size(); i++) {
							mListMarkers.get(i).setPosition(tmpPoints.get(i));
						}
						// currentTrajet.setListMarker(listMarkers);

						// Log.d("DEBUUUUUUG","LT = "+tmpPoints.size()+" LMB = "+listMarkersBad.size());
						PolylineOptions options = new PolylineOptions().geodesic(false)
								.width(15).color(Color.argb(120, 0, 0, 221));
						for (int i = 0; i < mListOverviewPolylinePoints.size(); i++) {
							options.add(mListOverviewPolylinePoints.get(i));
						}
						mPolyline = mMap.addPolyline(options);
						mRoute.setPointsWhoDrawsPolylineLatLng(mListOverviewPolylinePoints);
						mRoute.setValidate(true);
						
						String strSubtitle = "";
						double dist = mRoute.getDistTotal();
						if (dist < 1000) {
							strSubtitle += ((int) dist + "m");
						} else {
							strSubtitle += ((dist / 1000) + "Km");
						}

						int dureeSecond = mRoute.getDureeTotal();
						int heures = (dureeSecond / 3600);
						int minutes = ((dureeSecond % 3600) / 60);
						if (heures == 0) {
							strSubtitle += " - ~" + (minutes + "min");
						} else {
							strSubtitle += " - ~" + (heures + "h" + minutes + "min");
						}
						getActionBar().setSubtitle(strSubtitle);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				
				btn_correctionmode.setTextColor(Color.RED);
				btn_correctionmode.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (btn_correctionmode.getTextColors().getDefaultColor() == Color.RED) {
							findViewById(R.id.btn_dessiner).setEnabled(false);
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
									"Mode correction activ??", Toast.LENGTH_SHORT)
									.show();
						} else {
							btn_correctionmode.setTextColor(Color.RED);
							settingMapLongClickListener(false);
							settingMapClickListener(false);
							Toast.makeText(getApplicationContext(),
									"Mode correction d??sactiv??", Toast.LENGTH_SHORT)
									.show();
						}
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				btn_finirTrajet = (Button)findViewById(R.id.btn_finirTrajet);
				btn_finirTrajet.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						RoutesCollection at = RoutesCollection.getInstance();
						mRoute.setSave(true);
						if(!at.replace(mRoute)){
							at.add(mRoute);
						}
						at.saveAllTrajet();
						ListRoutes.updateDataList();
						CreateModifyRoute.getInstance().finish();
					}
				});
				
				
				Button btn_mapNormal = (Button)findViewById(R.id.btn_mapNormal);
				btn_mapNormal.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				Button btn_mapHybride = (Button)findViewById(R.id.btn_mapHybride);
				btn_mapHybride.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				Button btn_mapSatellite = (Button)findViewById(R.id.btn_mapSatellite);
				btn_mapSatellite.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				Button btn_mapTerrain = (Button)findViewById(R.id.btn_mapTerrain);
				btn_mapTerrain.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				return false;
			}
		});

		return true;
	}
}
