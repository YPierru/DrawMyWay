package com.ironrabbit.drawmywaybeta3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.ironrabbit.drawmyway.R;

public class Welcome extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		findViewById(R.id.NouveauTrajet).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent toMap = new Intent(Welcome.this,MyMapActivity.class);
				startActivity(toMap);
				//kikoo
			}
		});
		
		findViewById(R.id.ListeTrajet).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent toListe = new Intent(Welcome.this,TrajetDisplayList.class);
				startActivity(toListe);
			}
		});
	}
}
