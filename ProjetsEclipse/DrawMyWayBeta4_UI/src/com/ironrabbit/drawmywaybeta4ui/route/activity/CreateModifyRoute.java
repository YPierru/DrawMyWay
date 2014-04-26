package com.ironrabbit.drawmywaybeta4ui.route.activity;

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
import com.ironrabbit.drawmywaybeta4ui.asyncTasks.GettingRoute;
import com.ironrabbit.drawmywaybeta4ui.route.Route;
import com.ironrabbit.drawmywaybeta4ui.route.RoutesCollection;
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
	private String mMode;
	private Route mRoute;
	static CreateModifyRoute thisActivity;
	private RadialMenuWidget mWheelMenu;
	private LinearLayout mLinearLayourWheel;
	private MenuItem mItemWheelMenu;
	private boolean wheelEnable,canBeDraw,correctionEnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map_create);
		
		//Instanciation/R??cup??ration des objets
		thisActivity = this;
		mLinearLayourWheel=(LinearLayout)findViewById(R.id.linearLayoutForWheel);
		wheelEnable = false;
		canBeDraw=false;
		correctionEnable=false;
		mListMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mPolyline = null;
		mRoute = getIntent().getExtras().getParcelable("trajet");

		//Modification de la action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(mRoute.getName());
		
		//Action si montrajet re??u est en cours
		traitementsSiTrajetEnCours();


		// Initialisation des listener
		settingMapLongClickListenerNormal();
		mMap.setOnMarkerDragListener(new ActionDragMarker());
		

	}

	private void traitementsSiTrajetEnCours(){
		mMode = getIntent().getExtras().getString("MODE");
		
		if (mMode.equals("Modification")) {
			settingMapClickListenerNomal();
			ArrayList<LatLng> tmpListMarkers = mRoute.getListMarkersLatLng();
			for (int i = 0; i < tmpListMarkers.size(); i++) {
				if (i == 0) {
					mListMarkers.add(putMarker(tmpListMarkers.get(i), "D??part",
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
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point,
						16);
				mMap.animateCamera(cu, 600, null);

				
				//On ajoute le jalon en LatLng.
				mListMarkers.add(putMarker(point, "D??part", true));
				mRoute.getListMarkers().clear();
				mRoute.getListMarkers().add(point.latitude, point.longitude);
				
				//On initialise le listener pour le click simple
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
				mListMarkers.add(putMarker(point, "Jalon pos??", true));
				canBeDraw=true;
				mRoute.getListMarkers().add(point.latitude, point.longitude);
			}
		});
	}
	
	//Supprime de fa??on propre le listener du click simple
	private void settingMapClickListenerCorrectionMode(){
		mMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {}
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
	
	public void setPolyline(Polyline p){
		this.mPolyline=p;
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
								Html.fromHtml("<b>Appui long</b> : efface tout sur la carte et place un <u>point de d??part</u>"
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
		mItemWheelMenu = menu.add("Menu wheel").setIcon(R.drawable.sidemenu);
		mItemWheelMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mItemWheelMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						if (!wheelEnable) {
							
							//Cr??ation de la wheel
							wheelEnable = true;
							mWheelMenu = new RadialMenuWidget(getBaseContext());
							int xLayoutSize = mLinearLayourWheel.getHeight();
							int yLayoutSize = mLinearLayourWheel.getWidth();
							mWheelMenu.setSourceLocation(xLayoutSize,
									yLayoutSize);
							mWheelMenu.setIconSize(15, 30);
							mWheelMenu.setTextSize(13);

							//Initialisation selon les ??tats des flags
							mWheelMenu.setCenterCircle(new WheelMenu("Close", true, android.R.drawable.ic_menu_close_clear_cancel));
							if(canBeDraw){
								mWheelMenu.addMenuEntry(new WheelMenu("Dessiner", true, 0));
							}
							mWheelMenu.addMenuEntry(new WheelMenu("Terminer", true, 0));
							if(mListMarkers.size()>1 || correctionEnable){
								mWheelMenu.addMenuEntry(new WheelMenu("Correction", true, 0));
							}
							mWheelMenu.addMenuEntry(new MapTypeMenu());

							mLinearLayourWheel.addView(mWheelMenu);
						} 
						//Suppression
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

	/*
	 * Lorsqu'un marker est deplac??, on actualise la liste des marker du trajet
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
			
			if(this.name.equals("Normal")){
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}else if(this.name.equals("Hybride")){
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}else if(this.name.equals("Satellite")){
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}else if(this.name.equals("Terrain")){
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}else if(this.name.equals("Dessiner")){
				actionDraw();
			}else if(this.name.equals("Correction")){
				actionCorrection();
			}else if(this.name.equals("Terminer")){
				actionFinish();
			}
			
			
			if(this.closeWheelWhenTouch){
				((LinearLayout) mWheelMenu.getParent()).removeView(mWheelMenu);
				wheelEnable = false;
			}
		}
		
		public void actionDraw(){
			if (mPolyline != null) {
				mPolyline.remove();
			}
			
			if(correctionEnable){
				correctionEnable=false;
				settingMapClickListenerNomal();
			}

			GettingRoute getRoute = new GettingRoute(
					CreateModifyRoute.this, mRoute,
					mListOverviewPolylinePoints, mListMarkers,
					mMap);

			getRoute.execute();
		}
		
		public void actionFinish(){
			if(mListMarkers.size()>0){
				RoutesCollection at = RoutesCollection.getInstance();
				mRoute.setSave(true);
				if (!at.replace(mRoute)) {
					at.add(mRoute);
				}
				at.saveAllTrajet();
				ListRoutesCards.updateDataList();
			}
			CreateModifyRoute.getInstance().finish();
		}
		
		public void actionCorrection(){
			if (!correctionEnable) {

				correctionEnable=true;
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
							if (mListMarkers.get(i).getId().equals(marker.getId())) {
								if(i==0){
									Toast.makeText(getApplicationContext(),
										"Pour supprimer le point de d??part, faites un appui long sur une autre zone", Toast.LENGTH_SHORT)
										.show();
								}else{
									mListMarkers.remove(i);
									mRoute.setListMarkersMk(mListMarkers);
									marker.remove();
								}
								break;
							}
						}
						
						if(mListMarkers.size()==0){
							canBeDraw=false;
						}
						return false;
					}
				});
				Toast.makeText(getApplicationContext(),
						"Mode correction activ??", Toast.LENGTH_SHORT)
						.show();
			} else {
				correctionEnable=false;
				settingMapClickListenerNomal();
				Toast.makeText(getApplicationContext(),
						"Mode correction d??sactiv??", Toast.LENGTH_SHORT)
						.show();
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

		public void menuActiviated() {}
	}

	
}
