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
import android.widget.Toast;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta4.trajet.Trajet;
import com.ironrabbit.drawmywaybeta4.trajet.TrajetAdapter;
import com.navdrawer.SimpleSideDrawer;


public class Welcome extends Activity {
	
	private AllTrajets myAllTrajets;
	private static ListView myLV;
	private SimpleSideDrawer mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		
		mSlidingMenu=new SimpleSideDrawer(this);
		mSlidingMenu.setRightBehindContentView(R.layout.side_menu_welcome);
		
		myAllTrajets = AllTrajets.getInstance();
		getActionBar().setTitle("Vos trajets");
		if(myAllTrajets.size()==0){
			findViewById(R.id.tv_aucunTrajets).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.listView).setVisibility(View.VISIBLE);
			myLV = (ListView) findViewById(R.id.listView);

			// myLV.setOnItemLongClickListener(new ActionOnLongClickItemTrajet());
			myLV.setOnItemClickListener(new ActionOnClickItemTrajet());

			TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
			myLV.setAdapter(adapter);
			//registerForContextMenu(myLV);
		}
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item_NouveauTrajet = menu.add("Créer un trajet").setIcon(R.drawable.plus);
		item_NouveauTrajet.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item_NouveauTrajet.setOnMenuItemClickListener(new ActionOnMenuItemClick());
		return true;
	}

	
	private class ActionOnMenuItemClick implements OnMenuItemClickListener{
		public boolean onMenuItemClick(MenuItem item) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(Welcome.this).setTitle("Saisir le nom du trajet");
			final EditText input = new EditText(getApplicationContext());
			input.setHint("Nom du trajet");
			input.setTextColor(Color.BLACK);
			alert.setView(input);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							String value = input.getText().toString().trim();
							Trajet newTrajet = new Trajet(value, false, false, getCurrentDayTime());
							Intent toCreateTrajetActivity = new Intent(Welcome.this,CMWTrajet.class);
							toCreateTrajetActivity.putExtra("trajet", (Parcelable)newTrajet);
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
	
	public static void updateDataList(){
		
		TrajetAdapter ta = (TrajetAdapter)myLV.getAdapter();
		ta.updateData(AllTrajets.getInstance());
	}
	
	/*
	 * Si on clique sur un trajet, on bascule vers TrajetDetails
	 */
	private class ActionOnClickItemTrajet implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			final Trajet tj = myAllTrajets.get(arg2);
			
			if(tj.isValidate()){
				TextView tv_datecrea = (TextView)findViewById(R.id.tv_datecrea);
				tv_datecrea.setText(tj.getDateCreation());
				
				TextView tv_kmtrage = (TextView)findViewById(R.id.tv_kmtrage);
				double dist = tj.getDistTotal();
				if (dist < 1000) {
					tv_kmtrage.setText((int) dist + "m");
				} else {
					tv_kmtrage.setText((dist / 1000) + "Km");
				}
				
				TextView tv_duree = (TextView)findViewById(R.id.tv_duree);
				int dureeSecond = tj.getDureeTotal();
				int heures = (dureeSecond / 3600);
				int minutes = ((dureeSecond % 3600) / 60);
				if(heures==0){
					tv_duree.setText(minutes+"min");
				}else{
					tv_duree.setText(heures+"h"+minutes+"min");
				}
				
				TextView tv_addrdeb = (TextView)findViewById(R.id.tv_adrdepart);
				tv_addrdeb.setText(tj.getStartAddress());
				
				TextView tv_addrfin = (TextView)findViewById(R.id.tv_adrarriv);
				tv_addrfin.setText(tj.getEndAddress());
				
				
				Button btn_voirmodif = (Button)findViewById(R.id.btn_voirmodif);
				btn_voirmodif.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent toCreateTrajetActivity = new Intent(Welcome.this,CMWTrajet.class);
						toCreateTrajetActivity.putExtra("trajet", (Parcelable)tj);
						toCreateTrajetActivity.putExtra("MODE", "Voir");
						startActivity(toCreateTrajetActivity);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				
				Button btn_gps = (Button)findViewById(R.id.btn_gps);
				btn_gps.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent toGPSRunner = new Intent(Welcome.this,
								GPSRunner.class);
						toGPSRunner.putExtra("TRAJET", (Parcelable) tj);
						startActivity(toGPSRunner);
						mSlidingMenu.toggleRightDrawer();
					}
				});
				
				
				Button btn_renommer = (Button)findViewById(R.id.btn_renommer);
				btn_renommer.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final AlertDialog.Builder alert = new AlertDialog.Builder(
								Welcome.this).setTitle("Nouveau nom");
						final EditText input = new EditText(getApplicationContext());
						input.setText(tj.getName());
						input.setTextColor(Color.BLACK);
						input.setCursorVisible(true);
						alert.setView(input);
						alert.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = input.getText().toString()
												.trim();
										tj.setName(value);
										myAllTrajets.replace(tj);
										myAllTrajets.saveAllTrajet();
									}
								});
						alert.show();
						TrajetAdapter ta = (TrajetAdapter) myLV.getAdapter();
						ta.notifyDataSetChanged();
					}
				});
				
				
				Button btn_delete = (Button)findViewById(R.id.btn_supp);
				btn_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Welcome.this);
						//alertDialogBuilder.setTitle("Your Title");
						alertDialogBuilder
								.setMessage("Supprimer le trajet \""+tj.getName()+"\" ?")
								.setCancelable(false)
								.setPositiveButton("Oui",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												myAllTrajets.remove(tj);
												myAllTrajets.saveAllTrajet();
												TrajetAdapter ta = (TrajetAdapter) myLV.getAdapter();
												ta.notifyDataSetChanged();
												mSlidingMenu.toggleRightDrawer();
											}
										})
								.setNegativeButton("Non",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				});
				mSlidingMenu.toggleRightDrawer();
			}else{
				Intent toCWSTrajet = new Intent(Welcome.this,CMWTrajet.class);
				toCWSTrajet.putExtra("trajet", (Parcelable)tj);
				toCWSTrajet.putExtra("MODE", "Modification");
				startActivity(toCWSTrajet);
			}
			
		}
	}
}
