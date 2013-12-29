package com.example.directions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Building extends ListActivity {
	
	 private static final int ACTIVITY_CREATE=0;
	 private static final int ACTIVITY_EDIT=1;	    
	 private static final int INSERT_ID = Menu.FIRST;
	 private static final int DELETE_ID = Menu.FIRST + 1;
	 private static final int SEARCH_ID = Menu.FIRST + 2;
	 private BuildDbAdapter mDbHelper;
	 private Cursor mBuildCursor;
	 TextView index;
	 String[] inputArray;
	 String input,lati,longi;
	 int adj[][]= new int[53][53]; 
	 double Latitude; 
	 double Longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building);
		try
		{
			mDbHelper = new BuildDbAdapter(this);
	        mDbHelper.open();
        
	        try
	        {
	        	File file = new File("/sdcard/udelbuildings.txt");
	        	if(!file.exists()) 
	        	{
	        		file.createNewFile();
	        		BufferedReader in = new BufferedReader(new FileReader("/sdcard/UDBuildingPositions.txt")); 
	        		String line;   
	        		String code, title;
	        		double coord1,coord2;
	        		while ((line = in.readLine()) != null) 
	        		{
	        			String[] var = line.split(":");
	        			code = var[0];
	        			title = var[1];
	        			coord1 = Double.valueOf(var[2]);
	        			coord2 = Double.valueOf(var[3]);
	        			
	        			mDbHelper.createBuild(code, title, coord1, coord2);
	        			
	        		}
	        		in.close();
	        	}
	        	fillData();
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
        		br.close();*/
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        registerForContextMenu(getListView());
	}
	
	private void fillData() 
	{
        // Get all of the rows from the database and create the item list
        mBuildCursor = mDbHelper.fetchAllBuild();
        startManagingCursor(mBuildCursor);
        
        // Create an array to specify the fie3lds we want to display in the list (only TITLE)
        String[] from = new String[]{BuildDbAdapter.KEY_TITLE};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter buildings = 
        	    new SimpleCursorAdapter(this, R.layout.build_row, mBuildCursor, from, to);
        setListAdapter(buildings);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building, menu);
		menu.add(0, INSERT_ID,0, R.string.menu_insert);
		menu.add(0, SEARCH_ID,0, R.string.search);
		return true;
	}
	
	 @Override
     public boolean onMenuItemSelected(int featureId, MenuItem item) 
	 {
        switch(item.getItemId()) 
        {
        	case INSERT_ID:
        		createNote();
        		return true;
        	case SEARCH_ID:
        		searchBuild();
        		return true;
        }
	        
        return super.onMenuItemSelected(featureId, item);
    }

    private void searchBuild() 
    {
    	Intent i = new Intent(this, Search.class);
	   	startActivityForResult(i, ACTIVITY_CREATE);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) 
    {
    	switch(item.getItemId()) {
        case DELETE_ID:
        	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        	mDbHelper.deleteBuild(info.id);
	        fillData();
	        return true;
	    }
	    return super.onContextItemSelected(item);
			
	}

	private void createNote() 
	{
	   	Intent i = new Intent(this, BuildEdit.class);
	   	startActivityForResult(i, ACTIVITY_CREATE);
	}
	    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		Cursor c = mBuildCursor;
        c.moveToPosition(position);
	   	super.onListItemClick(l, v, position, id);
	   	Intent i = new Intent(this, BuildEdit.class);
	   	i.putExtra(BuildDbAdapter.KEY_ROWID, id);
	    i.putExtra(BuildDbAdapter.KEY_CODE, c.getString(
                c.getColumnIndexOrThrow(BuildDbAdapter.KEY_CODE)));
        i.putExtra(BuildDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(BuildDbAdapter.KEY_TITLE)));
        i.putExtra(BuildDbAdapter.KEY_COORD1, c.getString(
                c.getColumnIndexOrThrow(BuildDbAdapter.KEY_COORD1)));
        i.putExtra(BuildDbAdapter.KEY_COORD2, c.getString(
                c.getColumnIndexOrThrow(BuildDbAdapter.KEY_COORD2)));
	   	startActivityForResult(i, ACTIVITY_EDIT);

	 }

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	 {
	   	super.onActivityResult(requestCode, resultCode, intent);
	    fillData();	        
	}
	 
}
