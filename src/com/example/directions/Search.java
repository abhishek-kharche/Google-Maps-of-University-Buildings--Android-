package com.example.directions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Search extends Activity 
{
	
	private EditText mSearchText;
	private Long mRowId;
	private BuildDbAdapter mDbHelper;
	String code, title,coord1,coord2;
	double lat, longi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		    setContentView(R.layout.search_build);
		    mSearchText = (EditText) findViewById(R.id.search);
		    mDbHelper = new BuildDbAdapter(this);
		    Button confirmButton = (Button) findViewById(R.id.confirm);
		  
		    confirmButton.setOnClickListener(new View.OnClickListener() {

		        public void onClick(View view) {
		            //Bundle bundle = new Bundle();
		            
		            String value=mSearchText.getText().toString();
		            Cursor cursor=mDbHelper.fetchBuild(value);
		            
		            if(cursor.moveToFirst())
		    		  {
		            	mRowId = cursor.getLong(0);
		            	code = cursor.getString(1);
		    			title = cursor.getString(2);
		    			coord1 = cursor.getString(3);
		    			coord2 = cursor.getString(4);
		    			try
		    			{
		    				lat= Double.parseDouble(coord1);
		    				longi= Double.parseDouble(coord2);
		    			}
		    			catch (Exception e)
		    			{
		    				Log.v("NumberFormatError", "Number Format is not proper");
		    			}
  			  
		    			Intent i = new Intent(Search.this,BuildEdit.class);
		    			//i.putExtra(BuildDbAdapter.KEY_ID, mRowId);
			  	  	    i.putExtra(BuildDbAdapter.KEY_CODE, code);
			  	  	    i.putExtra(BuildDbAdapter.KEY_TITLE, title);
			  	  	    i.putExtra(BuildDbAdapter.KEY_COORD1, coord1);
			  	  	    i.putExtra(BuildDbAdapter.KEY_COORD2, coord2);
			  	  	    startActivity(i);
				    	}  
		        }
		    });
		}
}
