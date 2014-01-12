package com.example.drawmywaybeta2.AsyncTasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

public class GettingRoute extends AsyncTask<LatLng, Void, Void> {

	private LatLng origin, destination;
	private final String URL_PATTERN = "https://maps.googleapis.com/maps/api/directions/xml?sensor=true&mode=walking&";
	private Document myXmlDoc;
	private String overviewPL;
	private static ArrayList<LatLng> route;

	protected Void doInBackground(LatLng... params) {
		this.origin = params[0];
		this.destination = params[1];

		URL url = null;
		try {
			url = new URL(this.URL_PATTERN + "origin=" + this.origin.latitude
					+ "," + this.origin.longitude + "&destination="
					+ this.destination.latitude + ","
					+ this.destination.longitude);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Long currentMsBefore = System.currentTimeMillis();
		BufferedReader bfApi = null;
		try {
			bfApi = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long currentMsAfter = System.currentTimeMillis();

		// System.out.println("EXECUTION TIME="+(currentMsAfter-currentMsBefore));

		String str = null;
		try {
			str = IOUtils.toString(bfApi);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DocumentBuilder parser = null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(str);
		try {
			this.myXmlDoc = parser
					.parse(new InputSource(new StringReader(str)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.overviewPL = this.myXmlDoc
				.getElementsByTagName("overview_polyline").item(0)
				.getTextContent().trim();
		// System.out.println(this.overviewPL);
		route = decodePoly(this.overviewPL);
		return null;
	}

	private ArrayList<LatLng> decodePoly(String encoded) {

		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			LatLng ll = new LatLng(p.getLatitudeE6() / 1E6,
					p.getLongitudeE6() / 1E6);
			// System.out.println(ll.latitude+" "+ll.longitude);
			poly.add(ll);
		}

		return poly;
	}
	
	public static ArrayList<LatLng> getRoute(){
		return route;
	}
}
