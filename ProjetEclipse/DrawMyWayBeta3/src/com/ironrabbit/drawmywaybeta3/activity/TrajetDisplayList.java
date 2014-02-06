package com.ironrabbit.drawmywaybeta3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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
		myAllTrajets = TrajetManager.loadAllTrajet();

		myLV = (ListView) findViewById(R.id.listView);

		myLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Trajet myTrajet = myAllTrajets.get(arg2);
				
				if(myTrajet.isHasBeenSave()){
					Intent toTrajetDetails = new Intent(getApplicationContext(), TrajetDetails.class);
					toTrajetDetails.putExtra("trajet_for_details", (Parcelable)myTrajet);
					startActivity(toTrajetDetails);
				}else{
					Intent toMyMapActivity = new Intent(getApplicationContext(), TrajetDetails.class);
					toMyMapActivity.putExtra("trajet_en_cours", (Parcelable)myTrajet);
					startActivity(toMyMapActivity);
				}
			}
		});
		
		myLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				myAM = startActionMode(new AnActionModeOfEpicProportions(arg2));
				return false;
			}
		});

		TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
		adapter.notifyDataSetChanged();
		myLV.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.trajet_display, menu);
		return true;
	}
	
	
	
	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
		
		private int position;
		
		public AnActionModeOfEpicProportions(int pos){
			position=pos;
		}
		
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar

            menu.add("Delete")
                .setIcon(R.drawable.android)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	myAllTrajets.remove(position);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

}
