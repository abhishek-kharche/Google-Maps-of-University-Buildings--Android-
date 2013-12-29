package com.example.directions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Maps extends Activity implements TextToSpeech.OnInitListener
{
	double Latitude1, Longitude1;
	static double Latitude2, Longitude2;
	double LatitudeS, LongitudeS;
	private GoogleMap map;
	private BuildDbAdapter mDbHelper;
	int adj[][]= new int[53][53];
	long row;
	static String title,destination;
	LatLng CurrLoc;
	private TextToSpeech tts;
    private Button btnSpeak;
    private EditText txtText;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_build);
		map=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		Gps gpsTracker = new Gps(this);
		tts = new TextToSpeech(this, this);
		Location location=gpsTracker.getLocation();
		double x=location.getLatitude();
		double y=location.getLongitude();
		Latitude1=gpsTracker.latitude;
		Longitude1=gpsTracker.longitude;
		//Log.v("Reached here", "lati = " + Latitude1);
		//Log.v("Reached here", "longi = " + Longitude1);
		try
		{
			mDbHelper = new BuildDbAdapter(this);
	        mDbHelper.open();
	        Cursor cursor=mDbHelper.fetchBuild(Latitude1, Longitude1);
			if(cursor==null)
			{
				Log.v("Reached here", "Position Not in University");
			}
			else
			{
				if(cursor.moveToFirst())
				{
					row=cursor.getInt(0);
					title = cursor.getString(2);
					LatitudeS=cursor.getDouble(3);
					LongitudeS=cursor.getDouble(4);
					
					
					//Toast.makeText( getApplicationContext(), "New Source " + x + "    " + y ,Toast.LENGTH_SHORT ).show();	
				}
			}
			
			/*BufferedReader br = new BufferedReader(new FileReader("/sdcard/UDBuildingAdjMatrix.txt")); 
    		String line1;
    		int j=0;
    		while ((line1 = br.readLine()) != null) 
    		{
    			//char a[]= line1.toCharArray();
    			for(int i=0;i<line1.length();i++)
    			{
    				adj[j][i]=line1.charAt(i)-0x30;
    			}
    			j++;
    		}
    		br.close();
    		*/
    		
		}
		catch (Exception e)
		{
			Toast.makeText( getApplicationContext(), "NullPointerException on " ,Toast.LENGTH_SHORT ).show();
			Log.v("Reached here", "NullPointerException" + e);
		}
				
		CurrLoc = new LatLng(Latitude1,Longitude1);
		LatLng DestLoc = new LatLng(Latitude2,Longitude2);
		LatLng SourceLoc = new LatLng(LatitudeS,LongitudeS);
		map.setMyLocationEnabled(true);
		//map.addMarker(new MarkerOptions().position(CurrLoc).title("You are Here"));
		map.addMarker(new MarkerOptions().position(SourceLoc).title(title));
		map.addMarker(new MarkerOptions().position(DestLoc).title(destination));
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CurrLoc,17);
		map.animateCamera(update);
		
		String url1 = getDirectionsUrl(CurrLoc, DestLoc);
        DownloadTask downloadTask1 = new DownloadTask();
        downloadTask1.execute(url1);
        speakOut(title,destination);
        
        
       // DownloadTask downloadTask2 = new DownloadTask();
        //String url2 = getDirectionsUrl(SourceLoc, DestLoc);
        // Start downloading json data from Google Directions API
        //downloadTask2.execute(url2);
		//Toast.makeText( getApplicationContext(), "Latitude2= "+ Latitude2 + "\tLongitude2= "+ Longitude2,Toast.LENGTH_SHORT ).show();
		//Toast.makeText( getApplicationContext(), "Latitude1= "+ Latitude1 + "\tLongitude1= "+ Longitude1 ,Toast.LENGTH_SHORT ).show();
	}
	
	@Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
 
    @Override
    public void onInit(int status) {
 
        if (status == TextToSpeech.SUCCESS) {
 
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } 
            else 
            {
                speakOut(title, destination);
            }
 
        } 
        else 
        {
            Log.e("TTS", "Initilization Failed!");
        }
 
    }
 
    public void speakOut(String title, String destination) {
 
        String text = "you are near " + title + ". you choose your destination to be "+ destination+ ". Follow the road that is highlighted on the map.";
        //String text2= "your destination is "+ destination;
 
        tts.speak(text, 2, null);
        //tts.speak(text2, 2, null);
}
	public static void dest(double Lati2, double Longi2, String title)
	{
		Latitude2= Lati2;
		Longitude2=Longi2;
		destination=title;
	}
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.add(CurrLoc).addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.RED);
            }
 
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building, menu);
		return true;
	}
	
}
