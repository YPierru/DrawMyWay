package com.example.drawmywaybeta2.activity;

import com.example.gmapstests.R;
import com.example.gmapstests.R.layout;
import com.example.gmapstests.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TrajetSave extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trajetsave);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trajet_save, menu);
		return true;
	}

}
