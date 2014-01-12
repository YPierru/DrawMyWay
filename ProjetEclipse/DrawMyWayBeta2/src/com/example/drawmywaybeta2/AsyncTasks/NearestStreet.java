package com.example.drawmywaybeta2.AsyncTasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.android.gms.maps.model.LatLng;

import android.os.AsyncTask;

public class NearestStreet extends AsyncTask<LatLng, Void, Void> {

	private LatLng orig,dest;
	private static LatLng result;
	private final String URL_PATTERN="https://maps.googleapis.com/maps/api/directions/xml?sensor=true&mode=walking&";
	private Document myXmlDoc;
	
	@Override
	protected Void doInBackground(LatLng... params) {
		
		this.orig=params[0];
		this.dest=params[0];
		
		try {
			URL url = new URL(this.URL_PATTERN+"origin="+this.orig.latitude+","+this.orig.longitude+"&destination="+this.dest.latitude+","+this.dest.longitude);
			BufferedReader bfApi=new BufferedReader(new InputStreamReader(url.openStream()));
			String str=IOUtils.toString(bfApi);
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.myXmlDoc = parser.parse(new InputSource(new StringReader(str)));
			String str2=this.myXmlDoc.getElementsByTagName("start_location").item(0).getTextContent();
			Double lat=Double.valueOf(str2.split("\n")[1].trim());
			Double lng=Double.valueOf(str2.split("\n")[2].trim());
			result=new LatLng(lat, lng);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static LatLng getPoint(){
		return result;
	}

}
