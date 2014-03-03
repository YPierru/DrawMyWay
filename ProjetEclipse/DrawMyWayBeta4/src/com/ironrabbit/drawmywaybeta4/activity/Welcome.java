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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta4.trajet.Trajet;
import com.ironrabbit.drawmywaybeta4.trajet.TrajetAdapter;
import com.navdrawer.SimpleSideDrawer;


public class Welcome extends Activity {
	
	private AllTrajets myAllTrajets;
	private ListView myLV;
	private SimpleSideDrawer mSlidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		
		mSlidingMenu=new SimpleSideDrawer(this);
		mSlidingMenu.setRightBehindContentView(R.layout.side_menu);
		
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
			registerForContextMenu(myLV);
		}
		
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		ListView lv = (ListView) v;
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		final Trajet tj = (Trajet) lv.getItemAtPosition(acmi.position);

		MenuItem itemVoirModifier = menu.add("Voir/Modifier");
		itemVoirModifier.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				/*
				 * Intent vers un écran permettant de voir et/ou modifier le trajet
				 */
				return false;
			}
		});

		if(tj.isValidate()){
			MenuItem itemGPS = menu.add("Lancer le GPS");
			itemGPS.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent toGPSRunner = new Intent(Welcome.this,
							GPSRunner.class);
					toGPSRunner.putExtra("TRAJET", (Parcelable) tj);
					startActivity(toGPSRunner);
					return false;
				}
			});
		}

		MenuItem itemRename = menu.add("Renommer");
		itemRename.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

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
				return false;
			}
		});
		
		menu.add("Supprimer").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
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
				return false;
			}
		});
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
							Intent toCreateTrajetActivity = new Intent(Welcome.this,CreateTrajet.class);
							toCreateTrajetActivity.putExtra("nouveau_trajet", (Parcelable)newTrajet);
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
	
	/*
	 * Si on clique sur un trajet, on bascule vers TrajetDetails
	 */
	private class ActionOnClickItemTrajet implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			mSlidingMenu.toggleRightDrawer();
			
			/*Trajet tj = myAllTrajets.get(arg2);
			
			if(tj.isValidate()){
				String infoTrajet = "<b>Créer le : </b>"+tj.getDateCreation()
								   +"<br><b>Modifier le : </b>"+tj.getDateDerModif();
				
				double dist = tj.getDistTotal();
				if (dist < 1000) {
					infoTrajet+="<br><b>Kilométrage : </b>"+((int) dist + "m");
				} else {
					infoTrajet+="<br><b>Kilométrage : </b>"+((dist / 1000) + "Km");
				}
				int dureeSecond = tj.getDureeTotal();
				int heures = (dureeSecond / 3600);
				int minutes = ((dureeSecond % 3600) / 60);
				
				if(heures==0){
					infoTrajet+="<br><b>Durée : </b>"+minutes+"min";
				}else{
					infoTrajet+="<br><b>Durée : </b>"+heures+"h"+minutes+"min";
				}
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Welcome.this);
				alertDialogBuilder.setTitle(tj.getName());
				alertDialogBuilder
						.setMessage(Html.fromHtml(infoTrajet))
						.setCancelable(true);
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}else{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Welcome.this);
				alertDialogBuilder.setTitle(tj.getName());
				alertDialogBuilder
						.setMessage("Trajet non terminé !")
						.setCancelable(true);
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}*/
			
		}
	}
	
	public void onResume(){
		super.onResume();
		TrajetAdapter ta = (TrajetAdapter)myLV.getAdapter();
		ta.updateData(AllTrajets.getInstance());
	}
}
