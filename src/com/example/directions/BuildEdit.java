package com.example.directions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BuildEdit extends Activity{

	private EditText mCodeText;
	private EditText mTitleText;
	private EditText mCoord1Text;
	private EditText mCoord2Text;
	private Long mRowId;
	double lat,longi;
	String title;
	private BuildDbAdapter mDbHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 mDbHelper = new BuildDbAdapter(this);
		 mDbHelper.open();
		    
		 setContentView(R.layout.build_edit);
		 setTitle(R.string.edit_build);
		    
		 mCodeText = (EditText) findViewById(R.id.code);
		 mTitleText = (EditText) findViewById(R.id.title);
		 mCoord1Text = (EditText) findViewById(R.id.coord1);
		 mCoord2Text = (EditText) findViewById(R.id.coord2);

		 Button confirmButton = (Button) findViewById(R.id.confirm);
		 Button getDirections = (Button) findViewById(R.id.directions);
		 /*mRowId = (savedInstanceState == null) ? null :
			 (Long) savedInstanceState.getSerializable(BuildDbAdapter.KEY_ROWID);
	     if (mRowId == null) 
	     {
	         Bundle extras = getIntent().getExtras();
	         mRowId = extras != null ? extras.getLong(BuildDbAdapter.KEY_ROWID)
	                                  : null;       
	     }*/
	     	mRowId =  null;
	      	 Bundle extras = getIntent().getExtras();
	    	 if (extras != null) 
	    	 {
	    		 String code = extras.getString(BuildDbAdapter.KEY_CODE);
	    		 String title = extras.getString(BuildDbAdapter.KEY_TITLE);
	    		 String coord1 = extras.getString(BuildDbAdapter.KEY_COORD1);
	    		 String coord2 = extras.getString(BuildDbAdapter.KEY_COORD2);
	    		 mRowId = extras.getLong(BuildDbAdapter.KEY_ROWID);
		      
		      if (code != null) 
		      {
		          mCodeText.setText(code);
		      }
		      if (title != null) 
		      {
		          mTitleText.setText(title);
		      }
		      if (coord1 != null) 
		      {
		          mCoord1Text.setText(coord1);
		      }
		      if (coord2 != null) 
		      {
		          mCoord2Text.setText(coord2);
		      }
	    	
		        
		  }
	        
	        //populateFields();
	        //Log.v("reached here","reached after populate fields");
		    confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		            Bundle bundle = new Bundle();

		            bundle.putString(BuildDbAdapter.KEY_CODE, mCodeText.getText().toString());
		            bundle.putString(BuildDbAdapter.KEY_TITLE, mTitleText.getText().toString());
		            bundle.putString(BuildDbAdapter.KEY_COORD1, mCoord1Text.getText().toString());
		            bundle.putString(BuildDbAdapter.KEY_COORD2, mCoord2Text.getText().toString());
		            if (mRowId != null) {
		                bundle.putLong(BuildDbAdapter.KEY_ROWID, mRowId);
		            }
		        	
		        	/*setResult(RESULT_OK);
		        	Log.v("reached here","reached result_ok");
		            finish();*/
		           

		            Intent mIntent = new Intent();
		            mIntent.putExtras(bundle);
		            setResult(RESULT_OK, mIntent);
		            finish();
		        }
		    });
	     
		    
		    getDirections.setOnClickListener(new View.OnClickListener() {

			    public void onClick(View view) 
			    {
			    	Intent i = new Intent(BuildEdit.this,Maps.class);
			    	lat=Double.parseDouble(mCoord1Text.getText().toString());
			    	longi=Double.parseDouble(mCoord2Text.getText().toString());
			    	title=mTitleText.getText().toString();
			    	Maps.dest(lat, longi,title);
			    	startActivity(i);
			           /* Bundle bundle = new Bundle();

			            bundle.putString(BuildDbAdapter.KEY_CODE, mCodeText.getText().toString());
			            bundle.putString(BuildDbAdapter.KEY_TITLE, mTitleText.getText().toString());
			            bundle.putString(BuildDbAdapter.KEY_COORD1, mCoord1Text.getText().toString());
			            bundle.putString(BuildDbAdapter.KEY_COORD2, mCoord2Text.getText().toString());
			            if (mRowId != null) {
			                bundle.putLong(BuildDbAdapter.KEY_ROWID, mRowId);
			            }
			        	
			        	/*setResult(RESULT_OK);
			        	Log.v("reached here","reached result_ok");
			            finish();
			           

			            Intent mIntent = new Intent();
			            mIntent.putExtras(bundle);
			            setResult(RESULT_OK, mIntent);
			            finish();*/
			        }
			    });
		}

	
	/*private void populateFields() {
		if (mRowId != null) {
	        Cursor note = mDbHelper.fetchBuild(mRowId);
	        Log.v("reached here","reached back populate fields");
	        startManagingCursor(note);
	        Log.v("reached here","reached populate fields2");
	        mCodeText.setText(note.getString(
	                note.getColumnIndexOrThrow(BuildDbAdapter.KEY_CODE)));
	        mTitleText.setText(note.getString(
	                    note.getColumnIndexOrThrow(BuildDbAdapter.KEY_TITLE)));
	        mCoord1Text.setText(note.getString(
	                note.getColumnIndexOrThrow(BuildDbAdapter.KEY_COORD1)));
	        mCoord2Text.setText(note.getString(
	                note.getColumnIndexOrThrow(BuildDbAdapter.KEY_COORD2)));
	        Log.v("reached here","reached populate fields3");
	    }
	}*/
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(BuildDbAdapter.KEY_ROWID, mRowId);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        /*//populateFields();
	        if (mRowId != null) 
	        {
	        	Cursor cursor = mDbHelper.fetchBuild(mRowId);
	        	mRowId = cursor.getLong(0);
            	String code = cursor.getString(1);
    			String title = cursor.getString(2);
    			String coord1 = cursor.getString(3);
    			String coord2 = cursor.getString(4);
    			
			        
			  }
	     */   
	    }
	
	 private void saveState() 
	 {
		 String code = mCodeText.getText().toString();
		 String title = mTitleText.getText().toString();
	     String cord1 = mCoord1Text.getText().toString();
	     double coord1=Double.valueOf(cord1);
	     String cord2 = mCoord2Text.getText().toString();
	     double coord2=Double.valueOf(cord2);
	 
	        if (mRowId == null) {
	            long id = mDbHelper.createBuild(code, title, coord1, coord2);
	            if (id > 0) {
	                mRowId = id;
	            }
	        } else {
	            mDbHelper.updateBuild(mRowId, code, title, coord1, coord2);
	        }
	    }
}
