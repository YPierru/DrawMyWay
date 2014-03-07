package com.ironrabbit.drawmywaybeta4.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ironrabbit.drawmyway.R;
import com.ironrabbit.drawmywaybeta4.route.RoutesCollection;
import com.navdrawer.SimpleSideDrawer;

public class ArrayAdapterDemo extends Activity {

	RoutesCollection rc;
	ListView lv;
	SimpleSideDrawer mSSD;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_route);
		mSSD = new SimpleSideDrawer(this);
		mSSD.setRightBehindContentView(R.layout.side_menu_listroutes);
		rc = RoutesCollection.getInstance();
		lv = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				rc.getRouteNameList());
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("DEBUUUUUUUG", rc.get(position).getName());
				Log.d("DEBUUUUUUUG", rc.get(position).getDateCreation());
				Log.d("DEBUUUUUUUG", rc.get(position).getStartAddress());
				Log.d("DEBUUUUUUUG", rc.get(position).getEndAddress());
			}

		});
	}
}