package com.example.directions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class BuildDbAdapter {

	public static final String KEY_CODE = "code";
	public static final String KEY_TITLE = "title";
    public static final String KEY_COORD1 = "coord1";
    public static final String KEY_COORD2 = "coord2";
	public static final String KEY_ROWID = "_id";

	private static final String TAG = "BuildDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_CREATE =
	        "create table buildings (_id integer primary key autoincrement, "+ "code text not null, "
	        + "title text not null, "+"coord1 real not null, "+"coord2 real not null);";
	
	private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "buildings";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS buildings");
            onCreate(db);
        }
    }

    public BuildDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public BuildDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        Log.v("database", ""+(mDb==null));
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createBuild(String code, String title, double coord1, double coord2) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_COORD1, coord1);
        initialValues.put(KEY_COORD2, coord2);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteBuild(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllBuild() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CODE, KEY_TITLE,
                KEY_COORD1, KEY_COORD2}, null, null, null, null, null);
    }

    public Cursor fetchBuild(long rowId) throws SQLException {
    	//Log.v("reached here","reached DBadapter from populate fields");
        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CODE,
                    KEY_TITLE, KEY_COORD1, KEY_COORD2}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchBuild(String code) throws SQLException 
    {
    	open();
        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CODE,
                    KEY_TITLE, KEY_COORD1, KEY_COORD2}, KEY_CODE + " LIKE " + "'%" +code + "%' OR "+ KEY_TITLE + " LIKE " + "'%" +code + "%'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchBuild(double coord1, double coord2) throws SQLException 
    {
    	open();
    	//select top 1 _id from buildings where _id in ()
    	String query="select * from buildings where (((coord1) - (" +coord1 +"))*((coord1)- (" +coord1 +")) + ((coord2) - ("+ coord2 +" ))*((coord2) - ("+ coord2 +"))) = (select min(((coord1) - ("+ coord1+" ))*( (coord1)- ("+coord1 + " )) + ((coord2) - ("+ coord2 +"))*((coord2) - ("+ coord2+" ))) from buildings);";
    	//String query = "select _id from buildings order by (ABS(("+ coord1 +") - (coord2)) and ABS(("+ coord2 + ")- (coord2))) ASC;";
    	Cursor mCursor=mDb.rawQuery(query, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public boolean updateBuild(long rowId, String code, String title, double coord1, double coord2) {
        ContentValues args = new ContentValues();
        args.put(KEY_CODE, code);
        args.put(KEY_TITLE, title);
        args.put(KEY_COORD1, coord1);
        args.put(KEY_COORD2, coord2);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
