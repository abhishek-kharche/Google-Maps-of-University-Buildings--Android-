package com.example.directions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Cover extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.cover);
		 
		 Button SearchEdit = (Button) findViewById(R.id.SearchEdit);
		 Button EventInfo = (Button) findViewById(R.id.EventInfo);
		 
		 SearchEdit.setOnClickListener(new View.OnClickListener() {

			    public void onClick(View view) 
			    {
			    	Intent i = new Intent(Cover.this,Building.class);
			    	startActivity(i);
			    }
		    });
		 
		 EventInfo.setOnClickListener(new View.OnClickListener() {

			    public void onClick(View view) 
			    {
			    	Intent i = new Intent(Cover.this,EventInfo.class);
			    	startActivity(i);
			    }
		    });
	}
}
