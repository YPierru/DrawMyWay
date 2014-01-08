package com.example.drawmywaybeta2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/*
 * Cette classe a pour rôle d'effectuer les appels à l'API direction google maps
 * Mais aussi d'extraire les données qu'elle reçoit de cette appel.
 */

public class CallDirectionsAPI {

	private LatLng origin,destination;
	private final static String URL_PATTERN="http://maps.googleapis.com/maps/api/directions/xml?sensore=true&mode=walking&";
	private Document myXmlDoc;
	private String overviewPL;
	
	public CallDirectionsAPI(LatLng o, LatLng d){
		this.origin=o;
		this.destination=d;
	}
	
	public void extractXmlFromApi() throws IOException, ParserConfigurationException, SAXException{
		URL url=new URL(this.URL_PATTERN+"origin="+this.origin.latitude+","+this.origin.longitude+"&destination="+this.destination.latitude+","+this.destination.longitude);
		AsyncTask<URL, Void, BufferedReader> task = new DownloadXML().execute(url);
		BufferedReader bfApi=null;
		try {
			bfApi = new DownloadXML().execute(url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String str = IOUtils.toString(bfApi);
		
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.myXmlDoc = parser.parse(new InputSource(new StringReader(str)));
		this.overviewPL = this.myXmlDoc.getElementsByTagName("overview_polyline").item(0).getNodeValue();
		System.out.println(this.overviewPL);
		//return this.myXmlDoc;
		// ici on Test que le Document n'est pas null, Recuperation de l'element nom
		//Element nom = (Element)myXmlDoc.getElementsByTagName("step").item(0);

		// Affichage du validator
		//String validator = nom.getAttribute("validator");
		//System.out.println(validator);
	}
	
	
	//Retourne une liste de GeoPoint en fonction de leur encodage
	/*private ArrayList<GeoPoint> decodePoly(String encoded) {

		ArrayList<GeoPoint> poly = new ArrayList<GeoPoint>();
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
			poly.add(p);
		}

		return poly;
	}*/
	
	
	private class DownloadXML extends AsyncTask<URL, Void, BufferedReader>{
		protected BufferedReader doInBackground(URL... params) {
			
			try {
				return new BufferedReader(new InputStreamReader(params[0].openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	     }
	}
	
}
