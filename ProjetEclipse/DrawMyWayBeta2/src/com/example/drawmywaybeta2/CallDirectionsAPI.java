package com.example.drawmywaybeta2;

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

import com.example.drawmywaybeta2.activity.MyMapActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

public class CallDirectionsAPI {
	
	private static ArrayList<LatLng> listLatLng;
	
	public CallDirectionsAPI(LatLng o, LatLng d){
		new DownloadXML().execute(o,d);
	}
	
	public static void setListLatLng(ArrayList<LatLng> lll){
		listLatLng=lll;
		/*for (int i=0;i<listLatLng.size();i++){
			System.out.println(listLatLng.get(i).latitude+" "+listLatLng.get(i).longitude);
		}*/
	}
	
	public ArrayList<LatLng> getListLatLng(){
		return listLatLng;
	}

	
	private class DownloadXML extends AsyncTask<LatLng, Void, Void>{
		
		private LatLng origin,destination;
		private final static String URL_PATTERN="http://maps.googleapis.com/maps/api/directions/xml?sensor=true&mode=walking&";
		private Document myXmlDoc;
		private String overviewPL;
		
		protected Void doInBackground(LatLng... params) {
			this.origin=params[0];
			this.destination = params[1];
			
			URL url=null;
			try {
				url = new URL(this.URL_PATTERN+"origin="+this.origin.latitude+","+this.origin.longitude+"&destination="+this.destination.latitude+","+this.destination.longitude);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			BufferedReader bfApi=null;
			try {
				 bfApi=new BufferedReader(new InputStreamReader(url.openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String str=null;
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
			
			//System.out.println(str);
			try {
				this.myXmlDoc = parser.parse(new InputSource(new StringReader(str)));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.overviewPL = this.myXmlDoc.getElementsByTagName("overview_polyline").item(0).getTextContent().trim();
			//System.out.println(this.overviewPL);
			ArrayList<LatLng> list = decodePoly(this.overviewPL);
			CallDirectionsAPI.setListLatLng(list);
			//MyMapActivity.drawPolylineFromDirectionAPI(list);
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
				LatLng ll = new LatLng(p.getLatitudeE6()/1E6, p.getLongitudeE6()/1E6);
				//System.out.println(ll.latitude+" "+ll.longitude);
				poly.add(ll);
			}

			return poly;
		}
	}
	
}
