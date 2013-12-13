package com.example.drawmywaybeta2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.gmapstests.R;

public class MainActivity extends Activity {
	
	Button btnLaunchMap,btnTrajetSave;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		btnLaunchMap=(Button)findViewById(R.id.btn_launchMap);
		
		btnLaunchMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this,MyMapActivity.class);
				startActivity(myIntent);
			}
		});
		
		btnTrajetSave=(Button)findViewById(R.id.btn_voirTrajetSave);
		btnTrajetSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(MainActivity.this,TrajetSave.class);
				startActivity(myIntent);
				//System.out.println("founi");
			}
		});
	}

}
