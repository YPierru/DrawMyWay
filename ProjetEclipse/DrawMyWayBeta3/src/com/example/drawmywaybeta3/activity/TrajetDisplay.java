package com.example.drawmywaybeta3.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.example.drawmywaybeta3.TrajetAdapter;
import com.example.drawmywaybeta3.Trajet.AllTrajets;
import com.example.drawmywaybeta3.Trajet.TrajetManager;
import com.example.gmapstests.R;
import com.example.gmapstests.R.id;
import com.example.gmapstests.R.layout;

public class TrajetDisplay extends SherlockActivity {
	
	private TrajetManager myTrajetManager;
	private AllTrajets myAllTrajets;
	private ListView myLV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajet_display);
		myTrajetManager = new TrajetManager();
		myAllTrajets = myTrajetManager.loadAllTrajet();
		
		myLV = (ListView)findViewById(R.id.listView);
		
		TrajetAdapter adapter = new TrajetAdapter(this, myAllTrajets);
		myLV.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.trajet_display, menu);
		return true;
	}

}
