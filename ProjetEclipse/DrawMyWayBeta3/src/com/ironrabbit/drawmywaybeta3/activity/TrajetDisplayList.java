package com.ironrabbit.drawmywaybeta3.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta3.Trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta3.Trajet.Trajet;
import com.ironrabbit.drawmywaybeta3.Trajet.TrajetAdapter;

/*
 * Affiche la liste des trajets sauvegardés
 */
public class TrajetDisplayList extends SherlockActivity {

	private AllTrajets myAllTrajets;
	private ListView myLV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		myAllTrajets = AllTrajets.getInstance();

		myLV = (ListView) findViewById(R.id.listView);

		// myLV.setOnItemLongClickListener(new ActionOnLongClickItemTrajet());
		myLV.setOnItemClickListener(new ActionOnClickItemTrajet());

		TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
		myLV.setAdapter(adapter);
		registerForContextMenu(myLV);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		ListView lv = (ListView) v;
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		final Trajet tj = (Trajet) lv.getItemAtPosition(acmi.position);

		MenuItem itemSupprimer = menu.add("Supprimer");
		itemSupprimer.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TrajetDisplayList.this);
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

		MenuItem itemRename = menu.add("Renommer");
		itemRename.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				final AlertDialog.Builder alert = new AlertDialog.Builder(
						TrajetDisplayList.this).setTitle("Nouveau nom");
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	/*
	 * Si on clique sur un trajet, on bascule vers TrajetDetails
	 */
	private class ActionOnClickItemTrajet implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (myAllTrajets.get(arg2).isHasBeenSave()) {
				Intent toTrajetDetails = new Intent(getApplicationContext(),
						TrajetDetails.class);
				toTrajetDetails.putExtra("position_Trajet_List", arg2);
				startActivity(toTrajetDetails);
			}
		}
	}

}
