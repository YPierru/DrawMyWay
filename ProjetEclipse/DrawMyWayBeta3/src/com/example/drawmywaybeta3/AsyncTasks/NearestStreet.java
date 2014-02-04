package com.example.drawmywaybeta3.AsyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import android.os.AsyncTask;
import android.util.Log;

import com.example.drawmywaybeta3.Trajet.Downloaded.DirectionsResponse;
import com.example.drawmywaybeta3.Trajet.Downloaded.MyPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NearestStreet extends AsyncTask<LatLng, Void, Void> {

	private LatLng orig, dest;
	private static LatLng result;
	private final String URL_PATTERN = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=walking&language=fr&";
	private Document myXmlDoc;

	@Override
	protected Void doInBackground(LatLng... params) {
		
		this.orig=params[0];
		this.dest=params[0];
		
		try {
			Long currentMsBefore = System.currentTimeMillis();
			URL url = new URL(this.URL_PATTERN+"origin="+this.orig.latitude+","+this.orig.longitude+"&destination="+this.dest.latitude+","+this.dest.longitude);
			InputStream is=url.openStream();
			String str=IOUtils.toString(is);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			DirectionsResponse myRoad = gson.fromJson(str, DirectionsResponse.class);
			MyPoint mp = myRoad.getRoutes().get(0).getLegs().get(0).getStart_location();
			result = new LatLng(mp.getLat(),mp.getLng());
			//Log.d("DBW2 DEBUG", result.toString());
			Long currentMsAfter = System.currentTimeMillis();
			Log.d("DMWB2 DEBUG", ""+(currentMsAfter-currentMsBefore));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static LatLng getPoint() {
		return result;
	}

}
