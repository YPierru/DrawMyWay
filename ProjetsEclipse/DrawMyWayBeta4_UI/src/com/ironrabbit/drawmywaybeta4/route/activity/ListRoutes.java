package com.ironrabbit.drawmywaybeta4.route.activity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.gps.activity.GPSRunner;
import com.ironrabbit.drawmywaybeta4.route.Route;
import com.ironrabbit.drawmywaybeta4.route.RouteAdapter;
import com.ironrabbit.drawmywaybeta4.route.RoutesCollection;
import com.navdrawer.SimpleSideDrawer;

public class ListRoutes extends Activity {

	private static ListView mListView;
	private SimpleSideDrawer mSlidingMenuRight, mSlidingMenuLeft;
	private MenuDrawer mDrawer;
	private static String mTypeRouteCurrent;// VOITURE ou COUREUR
	private float x1, x2;// Points permettant de stocker l'abscisse de l'user
	static final int MIN_DISTANCE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		getActionBar().setHomeButtonEnabled(true);

		mSlidingMenuRight = new SimpleSideDrawer(this);
		mSlidingMenuRight.setRightBehindContentView(R.layout.side_menu_listroutes);

		mSlidingMenuLeft = new SimpleSideDrawer(this);
		mSlidingMenuLeft.setLeftBehindContentView(R.layout.side_menu_typeroute);

		mListView = (ListView) findViewById(R.id.listView);

		/*
		 * Ici, on récupère le typeCurrentRoute par un intent. On le fait dans
		 * un try catch, si ça passe par le catch, on met un typeCurrentRoute
		 * par défaut (VOITURE ou COUREUR, osef)
		 */
		try {
			mTypeRouteCurrent = getIntent().getExtras().getString("TYPEROUTE");
		} catch (NullPointerException npe) {
			mTypeRouteCurrent = "VOITURE";
		}

		initListWithRouteType();

		mListView.setOnItemClickListener(new ActionOnClickItemTrajet());
		mListView.setOnTouchListener(new ActionOnTouchEvent());

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			showLeftSideMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showLeftSideMenu() {
		mSlidingMenuLeft.toggleLeftDrawer();
		
		Button btn_routeVoiture = (Button) findViewById(R.id.btn_routeVoiture);
		btn_routeVoiture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeRouteCurrent = "VOITURE";
				initListWithRouteType();
				mSlidingMenuLeft.toggleLeftDrawer();
			}
		});
		
		Button btn_routeRunner = (Button) findViewById(R.id.btn_routeRunner);
		btn_routeRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeRouteCurrent = "COUREUR";
				initListWithRouteType();
				mSlidingMenuLeft.toggleLeftDrawer();
			}
		});
	}

	public void initListWithRouteType() {
		RouteAdapter newAdapter;
		RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
		if (mTypeRouteCurrent.equals("VOITURE")) {
			getActionBar().setTitle("Vos trajets voiture");
			if (mRoutesCollection.getListRoutesVoiture().size() == 0) {
				findViewById(R.id.tv_aucunTrajets).setVisibility(View.VISIBLE);
				findViewById(R.id.listView).setVisibility(View.GONE);
			} else {
				findViewById(R.id.listView).setVisibility(View.VISIBLE);
				findViewById(R.id.tv_aucunTrajets).setVisibility(View.GONE);
			}
			newAdapter = new RouteAdapter(this,
					mRoutesCollection.getListRoutesVoiture());
		} else {
			getActionBar().setTitle("Vos trajets coureur");
			if (mRoutesCollection.getListRoutesCoureur().size() == 0) {
				findViewById(R.id.tv_aucunTrajets).setVisibility(View.VISIBLE);
				findViewById(R.id.listView).setVisibility(View.GONE);
			} else {
				findViewById(R.id.listView).setVisibility(View.VISIBLE);
				findViewById(R.id.tv_aucunTrajets).setVisibility(View.GONE);
			}
			newAdapter = new RouteAdapter(this,
					mRoutesCollection.getListRoutesCoureur());
		}
		mListView.setAdapter(newAdapter);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item_NouveauTrajet = menu.add("Créer un trajet").setIcon(
				R.drawable.plus);
		item_NouveauTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_NouveauTrajet
				.setOnMenuItemClickListener(new ActionOnMenuItemClick());

		MenuItem item_deleteAll = menu.add("tout virer").setIcon(
				R.drawable.android);
		item_deleteAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_deleteAll
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
						mRoutesCollection.deleteFile();
						return false;
					}
				});
		return true;
	}

	private class ActionOnMenuItemClick implements OnMenuItemClickListener {
		public boolean onMenuItemClick(MenuItem item) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(
					ListRoutes.this).setTitle("Saisir le nom du trajet");
			final EditText input = new EditText(getApplicationContext());
			input.setHint("Nom du trajet");
			input.setTextColor(Color.BLACK);
			alert.setView(input);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString().trim();
							Route newTrajet = new Route(value, false, false,
									getCurrentDayTime(), mTypeRouteCurrent);
							//Log.d("DEBUUUUUG", mTypeRouteCurrent);
							Intent toCreateTrajetActivity = new Intent(
									ListRoutes.this, CreateModifyRoute.class);
							toCreateTrajetActivity.putExtra("trajet",
									(Parcelable) newTrajet);
							toCreateTrajetActivity.putExtra("MODE", "Création");
							startActivity(toCreateTrajetActivity);
						}
					});
			alert.show();
			return true;
		}
	}

	private String getCurrentDayTime() {
		Date aujourdhui = new Date();

		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.SHORT);

		return shortDateFormat.format(aujourdhui);
	}

	public static void updateDataList() {
		RouteAdapter ta = (RouteAdapter) mListView.getAdapter();
		if (mTypeRouteCurrent.equals("VOITURE")) {
			ta.updateData(RoutesCollection.getInstance().getListRoutesVoiture());
		} else {
			ta.updateData(RoutesCollection.getInstance().getListRoutesCoureur());
		}
	}

	private class ActionOnTouchEvent implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (event.getX() <= 30) {
					x1 = event.getX();
				} else {
					x1 = 40;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (x1 <= 30) {
					x2 = event.getX();
					float deltaX = x2 - x1;
					if (deltaX > MIN_DISTANCE) {
						showLeftSideMenu();
					}
				}
				break;
			}
			return false;
		}
	}

	/*
	 * Si on clique sur un trajet, on bascule vers TrajetDetails
	 */
	private class ActionOnClickItemTrajet implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			final Route tj;
			final 	RoutesCollection mRoutesCollection = RoutesCollection.getInstance();
			Log.d("DEBUUUUUUUG", "" + arg2);
			if (mTypeRouteCurrent.equals("VOITURE")) {
				tj = mRoutesCollection.getListRoutesVoiture().get(arg2);
			} else {
				tj = mRoutesCollection.getListRoutesCoureur().get(arg2);
			}

			if (tj.isValidate()) {
				TextView tv_datecrea = (TextView) findViewById(R.id.tv_datecrea);
				tv_datecrea.setText(tj.getDateCreation());

				TextView tv_kmtrage = (TextView) findViewById(R.id.tv_kmtrage);
				double dist = tj.getDistTotal();
				if (dist < 1000) {
					tv_kmtrage.setText((int) dist + "m");
				} else {
					tv_kmtrage.setText((dist / 1000) + "Km");
				}

				TextView tv_duree = (TextView) findViewById(R.id.tv_duree);
				int dureeSecond = tj.getDureeTotal();
				int heures = (dureeSecond / 3600);
				int minutes = ((dureeSecond % 3600) / 60);
				if (heures == 0) {
					tv_duree.setText(minutes + "min");
				} else {
					tv_duree.setText(heures + "h" + minutes + "min");
				}

				TextView tv_addrdeb = (TextView) findViewById(R.id.tv_adrdepart);
				tv_addrdeb.setText(tj.getStartAddress());

				TextView tv_addrfin = (TextView) findViewById(R.id.tv_adrarriv);
				tv_addrfin.setText(tj.getEndAddress());

				Button btn_voirmodif = (Button) findViewById(R.id.btn_voirmodif);
				btn_voirmodif.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent toSeeRoute = new Intent(ListRoutes.this,
								SeeRoute.class);
						toSeeRoute.putExtra("trajet", (Parcelable) tj);
						startActivity(toSeeRoute);

						mSlidingMenuRight.toggleRightDrawer();
					}
				});

				Button btn_gps = (Button) findViewById(R.id.btn_gps);
				btn_gps.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent toGPSRunner = new Intent(ListRoutes.this,
								GPSRunner.class);
						toGPSRunner.putExtra("TRAJET", (Parcelable) tj);
						startActivity(toGPSRunner);
						mSlidingMenuRight.toggleRightDrawer();
					}
				});

				Button btn_renommer = (Button) findViewById(R.id.btn_renommer);
				btn_renommer.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final AlertDialog.Builder alert = new AlertDialog.Builder(
								ListRoutes.this).setTitle("Nouveau nom");
						final EditText input = new EditText(
								getApplicationContext());
						input.setText(tj.getName());
						input.setTextColor(Color.BLACK);
						input.setCursorVisible(true);
						alert.setView(input);
						alert.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = input.getText()
												.toString().trim();
										tj.setName(value);
										mRoutesCollection.replace(tj);
										mRoutesCollection.saveAllTrajet();
									}
								});
						alert.show();
						RouteAdapter ta = (RouteAdapter) mListView.getAdapter();
						ta.notifyDataSetChanged();
					}
				});

				Button btn_delete = (Button) findViewById(R.id.btn_supp);
				btn_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								ListRoutes.this);
						// alertDialogBuilder.setTitle("Your Title");
						alertDialogBuilder
								.setMessage(
										"Supprimer le trajet \"" + tj.getName()
												+ "\" ?")
								.setCancelable(false)
								.setPositiveButton("Oui",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												mRoutesCollection.remove(tj);
												mRoutesCollection
														.saveAllTrajet();
												ListRoutes.updateDataList();
												/*RouteAdapter ta = (RouteAdapter) mListView
														.getAdapter();
												ta.notifyDataSetChanged();*/
												mSlidingMenuRight
														.toggleRightDrawer();
											}
										})
								.setNegativeButton("Non",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				});
				mSlidingMenuRight.toggleRightDrawer();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						ListRoutes.this);
				alertDialogBuilder.setTitle("Trajet non terminé");
				alertDialogBuilder
						.setMessage(
								Html.fromHtml("Le trajet <b>"
										+ tj.getName()
										+ "</b> n'est pas terminé.<br>"
										+ "Vous pouvez le <b>poursuivre</b> ou le <b>supprimer</b>."))
						.setCancelable(false)
						.setPositiveButton("Poursuivre",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Intent toFinishTrajet = new Intent(
												ListRoutes.this,
												CreateModifyRoute.class);
										toFinishTrajet.putExtra("trajet",
												(Parcelable) tj);
										toFinishTrajet.putExtra("MODE",
												"Modification");
										startActivity(toFinishTrajet);
									}
								})
						.setNeutralButton("Annuler",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								})
						.setNegativeButton("Supprimer",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										mRoutesCollection.remove(tj);
										mRoutesCollection.saveAllTrajet();
										ListRoutes.updateDataList();
										/*RouteAdapter ta = (RouteAdapter) mListView
												.getAdapter();
										ta.notifyDataSetChanged();*/
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}

		}
	}
}
