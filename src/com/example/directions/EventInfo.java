package com.example.directions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EventInfo extends Activity {
	ListView mylistview;
	ArrayList<String> array_events;
	ArrayAdapter<String> listAdapter;
	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_info);
		mylistview = (ListView) findViewById(R.id.listview1);
		array_events = new ArrayList<String>();

		startLongRunningOperation();

	}

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};

	private void updateResultsInUi() {
		listAdapter = new ArrayAdapter<String>(EventInfo.this,android.R.layout.simple_list_item_1, array_events);
		mylistview.setAdapter(listAdapter);

		System.out.println("binding");
	}

	protected void startLongRunningOperation() {
		Thread downloadThread = new Thread() {
			public void run() {
				Document doc;
				try {
					doc = Jsoup.connect("http://events.udel.edu/calendar").get();
					Elements spans = doc.select("span.summary");
					Elements desc = doc.select("h4.description");

					for (Element span : spans) {
						array_events.add(span.text());
					}

					for (Element div1 : desc) {
						System.out.println(div1.text());
					}
					// System.out.print(doc.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				mHandler.post(mUpdateResults);
			}
		};
		downloadThread.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building, menu);
		return true;
	}
}