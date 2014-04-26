package com.example.autocompleted;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	String url;
	private static final String TAG_RESULT = "predictions";
	JSONObject json;
	JSONArray contacts = null;
	AutoCompleteTextView ed;
	String[] search_text;
	ArrayList<String> names;
	ArrayAdapter<String> adp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ed=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		ed.setThreshold(0);
		names=new ArrayList<String>();
			ed.addTextChangedListener(new TextWatcher()
		  {

		   public void afterTextChanged(Editable s)
		   {

		   }

		   public void beforeTextChanged(CharSequence s, int start,
		    int count, int after)
		   {

		   }

		   public void onTextChanged(CharSequence s, int start,
		    int before, int count)
		   {
			   
			   search_text= ed.getText().toString().split(",");
			   url="https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+search_text[0]+"&components=country:uk&sensor=true&key=AIzaSyCRlWaohsKtqtmmS52ALcrYsp46ia96-js";
			   if(search_text.length<=1){
				   names=new ArrayList<String>();
				   Log.d("URL",url);
					paserdata parse=new paserdata();
					parse.execute();
			   }
			 
		   }
		  });
			
	}
	public class paserdata extends AsyncTask<Void, Integer, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			
			JSONParser jParser = new JSONParser();

			// getting JSON string from URL
			 json = jParser.getJSONFromUrl(url.toString());
			if(json !=null)
			{
			try {
				// Getting Array of Contacts
				contacts = json.getJSONArray(TAG_RESULT);
				
				for(int i = 0; i < contacts.length(); i++){
					JSONObject c = contacts.getJSONObject(i);
					String description = c.getString("description");
					Log.d("description", description);
					names.add(description);
				
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			adp=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,names);
			ed.setAdapter(adp);	

		
		}
		}

	
}
