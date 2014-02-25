package com.ironrabbit.drawmywaybeta4.asyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ironrabbit.drawmywaybeta4.trajet.downloaded.DirectionsResponse;


/*
 * R????cup????re les d????tails d'un trajet via l'API DirectionsResponse
 */
public class GettingRoute extends AsyncTask<ArrayList<LatLng>, Void, Void> {

	private LatLng origin, destination;
	private final String URL_PATTERN = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&language=fr&mode=walking&";
	private static DirectionsResponse myRoad;
	private static ArrayList<LatLng> listWayPoints;//LIste des jalons

	protected Void doInBackground(ArrayList<LatLng>... param) {

		//Construction de l'URL
		listWayPoints=param[0];
		this.origin = listWayPoints.get(0);
		this.destination = listWayPoints.get(listWayPoints.size()-1);
		listWayPoints.remove(0);
		listWayPoints.remove(listWayPoints.size()-1);
		String wayPointsStr="&waypoints=";
		
		for(int i=0;i<listWayPoints.size();i++){
			wayPointsStr+=listWayPoints.get(i).latitude+","+listWayPoints.get(i).longitude;
			if(i+1<listWayPoints.size()){
				wayPointsStr+="|";
			}
		}
		
		URL url = null;
		try {
			url = new URL(this.URL_PATTERN + "origin=" + this.origin.latitude
					+ "," + this.origin.longitude + "&destination="
					+ this.destination.latitude + ","
					+ this.destination.longitude+wayPointsStr);
			//Appel de l'API, parsing du JSON r????cup????r????
			InputStream is = url.openStream();
			String strRoad = IOUtils.toString(is);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			myRoad = gson.fromJson(strRoad, DirectionsResponse.class);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DirectionsResponse getDR(){
		return myRoad;
	}
}
