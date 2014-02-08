package com.ironrabbit.drawmyway;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class TestMaps extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_maps);
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapTest)).getMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_maps, menu);
		return true;
	}

}
