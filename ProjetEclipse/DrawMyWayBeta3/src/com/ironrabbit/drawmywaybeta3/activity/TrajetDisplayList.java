package com.ironrabbit.drawmywaybeta3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta3.Trajet.AllTrajets;
import com.ironrabbit.drawmywaybeta3.Trajet.Trajet;
import com.ironrabbit.drawmywaybeta3.Trajet.TrajetAdapter;
import com.ironrabbit.drawmywaybeta3.Trajet.TrajetManager;

public class TrajetDisplayList extends SherlockActivity {

	private AllTrajets myAllTrajets;
	private ListView myLV;
	private ActionMode myAM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		myAllTrajets = AllTrajets.getInstance();

		myLV = (ListView) findViewById(R.id.listView);

		myLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (myAllTrajets.get(arg2).isHasBeenSave()) {
					Intent toTrajetDetails = new Intent(
							getApplicationContext(), TrajetDetails.class);
					toTrajetDetails.putExtra("position_Trajet_List", arg2);
					startActivity(toTrajetDetails);
				} /*else {
					Intent toMyMapActivity = new Intent(
							getApplicationContext(), TrajetDetails.class);
					toMyMapActivity.putExtra("trajet_en_cours",
							(Parcelable) myAllTrajets);
					startActivity(toMyMapActivity);
				}*/
			}
		});

		TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
		// adapter.notifyDataSetChanged();
		myLV.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

}
