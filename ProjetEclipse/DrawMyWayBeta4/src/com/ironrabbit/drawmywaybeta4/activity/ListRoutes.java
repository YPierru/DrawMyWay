package com.ironrabbit.drawmywaybeta4.activity;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.trajet.RoutesCollection;
import com.ironrabbit.drawmywaybeta4.trajet.Route;
import com.ironrabbit.drawmywaybeta4.trajet.RouteAdapter;
import com.navdrawer.SimpleSideDrawer;

public class ListRoutes extends Activity {

	private RoutesCollection mRoutesCollection;
	private static ListView mListView;
	private SimpleSideDrawer mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);

		mSlidingMenu = new SimpleSideDrawer(this);
		mSlidingMenu.setRightBehindContentView(R.layout.side_menu_welcome);

		mRoutesCollection = RoutesCollection.getInstance();
		getActionBar().setTitle("Vos trajets");
		if (mRoutesCollection.size() == 0) {
			findViewById(R.id.tv_aucunTrajets).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.listView).setVisibility(View.VISIBLE);
		}
		mListView = (ListView) findViewById(R.id.listView);

		// myLV.setOnItemLongClickListener(new
		// ActionOnLongClickItemTrajet());
		mListView.setOnItemClickListener(new ActionOnClickItemTrajet());

		RouteAdapter adapter = new RouteAdapter(this, mRoutesCollection);
		mListView.setAdapter(adapter);
		// registerForContextMenu(myLV);

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
									getCurrentDayTime());
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
		ta.updateData(RoutesCollection.getInstance());
	}

	/*
	 * Si on clique sur un trajet, on bascule vers TrajetDetails
	 */
	private class ActionOnClickItemTrajet implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			final Route tj = mRoutesCollection.get(arg2);

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

						Intent toCMTrajetActivity = new Intent(ListRoutes.this,
								CreateModifyRoute.class);
						toCMTrajetActivity.putExtra("trajet", (Parcelable) tj);
						startActivity(toCMTrajetActivity);

						mSlidingMenu.toggleRightDrawer();
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
						mSlidingMenu.toggleRightDrawer();
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
												mRoutesCollection.saveAllTrajet();
												RouteAdapter ta = (RouteAdapter) mListView
														.getAdapter();
												ta.notifyDataSetChanged();
												mSlidingMenu
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
				mSlidingMenu.toggleRightDrawer();
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
												ListRoutes.this, CreateModifyRoute.class);
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
										RouteAdapter ta = (RouteAdapter) mListView
												.getAdapter();
										ta.notifyDataSetChanged();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}

		}
	}
}
