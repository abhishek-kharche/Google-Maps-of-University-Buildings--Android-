package com.example.directions;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class Gps extends Service implements LocationListener
{
	private Context mContext;	
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 meters
    private static final long MIN_TIME_BW_UPDATES = 100; // 1 sec
    protected LocationManager locationManager;

    public Gps(Context context) 
    {
        this.mContext = context;
        getLocation();
    }


    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            
            this.canGetLocation = true;
            
            if (isGPSEnabled)
            {
                if (location == null)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    //Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        updateGPSCoordinates();
                    }
                }
            }
        }
        catch (Exception e)
        {
        	
        }
        return location;
    }
        
        public void updateGPSCoordinates()
        {
            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        
        public void stopUsingGPS()
        {
            if (locationManager != null)
            {
                locationManager.removeUpdates(Gps.this);
            }
        }
        
        public double getLatitude()
        {
            if (location != null)
            {
                latitude = location.getLatitude();
            }
            return latitude;
        }

        /**
         * Function to get longitude
         */
        
        public boolean canGetLocation()
        {
            return this.canGetLocation;
        }
        
        @Override
        public void onLocationChanged(Location location) 
        {   
        	latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) 
        {   
        	
        }

        @Override
        public void onProviderEnabled(String provider) 
        {   
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) 
        {   
        }

        @Override
        public IBinder onBind(Intent intent) 
        {
            return null;
        }
}
