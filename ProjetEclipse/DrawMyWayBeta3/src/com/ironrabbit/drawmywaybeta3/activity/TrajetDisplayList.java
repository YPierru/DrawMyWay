package com.ironrabbit.drawmywaybeta3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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

		myLV.setOnItemLongClickListener(new ActionOnLongClickItemTrajet());
		myLV.setOnItemClickListener(new ActionOnClickItemTrajet());

		TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
		myLV.setAdapter(adapter);

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

	/*
	 * Si on reste appuyé sur un trajet, active le ActionMode
	 */
	private class ActionOnLongClickItemTrajet implements
			OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			myLV.setOnItemClickListener(null);
			startActionMode(new AnActionModeOfEpicProportions());
			return false;
		}

	}

	
	private final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		/*
		 * Ajoute les items à la barre en haut
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Used to put dark icons on light action bar
			menu.add("Delete").setIcon(R.drawable.deleteicon)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			/*menu.add("Select all").setIcon(R.drawable.selectallicon)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/

			//Affiche la checkbox pour les trajets
			hideOrSeeCheckBox(View.VISIBLE);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		/*
		 * Détermine le comporte lorsque l'on clique sur un item (en ActionMode)
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			//Récupère le nombre d'items.
			int nbItem = myLV.getCount();
			View itemView;
			CheckBox cb;
			Trajet tj;
			//Pour chaque item, s'il est coché, on le supprime
			for (int i = 0; i < nbItem; i++) {
				 itemView = (View) myLV.getChildAt(i);
				 cb = (CheckBox) itemView
						.findViewById(R.id.checkboxTrajet);
				if (cb.isChecked()) {
					cb.setChecked(false);
					 tj = (Trajet) myLV.getItemAtPosition(i);
					myAllTrajets.remove(tj);
				}
			}
			
			//Recharge la liste des items
			TrajetAdapter ta=(TrajetAdapter)myLV.getAdapter();
			ta.notifyDataSetChanged();
			
			//Ferme le ActionMode
			mode.finish();
			
			//Sauvegarde tout les trajets
			myAllTrajets.saveAllTrajet();
			
			//Redonne le comportement par défaut lors d'un click item
			myLV.setOnItemClickListener(new ActionOnClickItemTrajet());
			
			//Cache les checkbox
			hideOrSeeCheckBox(View.GONE);
			return true;
		}

		/*
		 * Ce produit lorsque l'user clique sur la croix de validation en haut à gauche
		 */
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			//Redonne le comportement par défaut lors d'un click item
			myLV.setOnItemClickListener(new ActionOnClickItemTrajet());
			
			//Cache les checkbox
			hideOrSeeCheckBox(View.GONE);
		}

		/*
		 * Cache les checkbox pour chaque item
		 */
		public void hideOrSeeCheckBox(int hos) {
			int nbItem = myLV.getCount();
			for (int i = 0; i < nbItem; i++) {
				View itemView = (View) myLV.getChildAt(i);
				itemView.findViewById(R.id.checkboxTrajet).setVisibility(hos);
			}
		}
	}

}
