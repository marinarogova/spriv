package com.spriv.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class GpsService extends Service {

    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();
    public static final int LOCATION_UPDATE_TIME = 30;
    public LocationManager myLocationManager;
    public boolean w_bGpsEnabled, w_bNetworkEnabled;

    public static double w_fLatitude = 0;

    public static double w_fLongitude = 0;
    public static double w_faltitude = 0;
    public static double w_accurcry = 0;
    public static String time_stamp = "";

    Calendar calander;
    SimpleDateFormat simpledateformat, simpleTimeformat;
    String _date = "", _time = "";

    public static int MY_PERMISSION_LOCATION = 123;

    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Gps Info Service", "Service start to run.");

        simpledateformat = new SimpleDateFormat("yyyy-MM-dd ");
        simpleTimeformat = new SimpleDateFormat("HH:mm:ss");
        calander = Calendar.getInstance();
        _date = simpledateformat.format(calander.getTime());
        _time = simpleTimeformat.format(calander.getTime());

        // scheduling the current position updating task (Asynchronous)
        mTimer.schedule(doAsynchronousTask, 0, LOCATION_UPDATE_TIME);

        return super.onStartCommand(intent, flags, startId);
    }

    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {


                    initLocationListener();
                    // send receivedLocation to server

                    Log.d("LON", String.valueOf(w_accurcry));

                }
            });
        }
    };

    public void initLocationListener() {

        LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean w_bGpsEnabled = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean w_bNetworkEnabled = myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!w_bGpsEnabled && !w_bNetworkEnabled) {
            //

            //
            setMyLocation(null);
        } else {
            tryGetLocation();
        }

    }


    private void tryGetLocation() {
        LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        w_bGpsEnabled = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        w_bNetworkEnabled = myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (w_bNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, m_myLocationListener, null);
            Location locationNet = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationNet != null) {
                setMyLocation(locationNet);
                return;
            }
        }
        if (w_bGpsEnabled) {
            myLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, m_myLocationListener, null);
            Location locationGps = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGps != null) {
                setMyLocation(locationGps);
                return;
            }
        }

    }

    private void setMyLocation(Location p_location) {

        if (p_location != null) {
            w_fLatitude = p_location.getLatitude();
            w_fLongitude = p_location.getLongitude();
            w_faltitude = p_location.getAltitude();
            w_accurcry = p_location.getAccuracy();
            time_stamp = _date+ _time;
        }

       // System.out.println("Latitude: " + String.valueOf(w_fLatitude) + "Longitude: " + String.valueOf(w_fLongitude));

    }


    LocationListener m_myLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onLocationChanged(Location location) {
            setMyLocation(location);
        }
    };

}
