/*
Aplicació mòbil que permet implementar sistemes de pagament per generació de residus enfocats a bonificar els usuaris en base al seu bon comportament. Mitjançant la lectura de codis QR adherits als contenidors, el ciutadà pot informar proactivament al seu ajuntament de les seves actuacions de reciclatge i rebre bonificacions en base a actuacions positives.
Copyright (C) 2018  Urgellet Recicla

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Contact email meu@urgellet.cat

The GNU General Public License does not permit incorporating your program
into proprietary programs.  If your program is a subroutine library, you
may consider it more useful to permit linking proprietary applications with
the library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.  But first, please read
<https://www.gnu.org/licenses/why-not-lgpl.html>.
*/

package com.smartcitylink.urgellet.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.smartcitylink.urgellet.events.CheckSettingsEvent;
import com.smartcitylink.urgellet.events.LocationChangedEvent;

import org.greenrobot.eventbus.EventBus;

public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int     TIME_AFTER_START   = 15;  // Start on x seconds after init Scheduler
    public static final long    DEFAULT_INTERVAL   = 1000 * 60 * 1;  // Default interval for background service: 3 minutes
    public static final long 	DEFAULT_INTERVAL_FASTER = 1000 * 30 * 1;
    public static final float   ACCURACY 	= 200;
    public static final long    LOCATION_ROUTE_INTERVAL = 1000 * 60;
    public static final long    LOCATION_MAP_INTERVAL = 1000 * 120;
    IBinder mBinder = new LocalBinder();

    public static final String TAG = "TrackLocationService";
    private static final boolean DEBUG = true;
    public static final String TRACK_SERVICE_CONNECT = "brodcast_onfirebase_connected";
    public static final String KEY_TRACKID = "key_trackId";


    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private String trackId;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        startLocationService();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.i(TAG, "=== OnStartCommand ");
        // this.trackId = Storage.getTrackId(this);
        sendBroadcast(new Intent(TRACK_SERVICE_CONNECT));
        settingsrequest();
        return Service.START_STICKY;
    }

    public void onDestroy() {

        stopLocationService();
        if (DEBUG) Log.i(TAG, "=== onDestroy");
    }


    public void stopLocationService() {
        if (DEBUG) Log.i(TAG, "=== stopLocationService");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void sendDataFrame(Location location) {

        if (DEBUG) Log.i(TAG, "=== sendDataFrame -->" + location.toString());

        EventBus.getDefault().post(new LocationChangedEvent(location));

    }

    private void setupLocationForMap() {
        long fastUpdate = DEFAULT_INTERVAL_FASTER;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(fastUpdate);
    }

    private synchronized void startLocationService() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            if (DEBUG) Log.i(TAG, "=== Starting LocationServices: NON CONECTED -> CONECTED");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
            settingsrequest();
        } else if (DEBUG) Log.i(TAG, "=== GPS service started: CONECTED");
    }

    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationService();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        EventBus.getDefault().post(new CheckSettingsEvent(status));
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {

        setupLocationForMap();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }catch (SecurityException e){
            Log.e(TAG, "Security Exception -->" + e.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        EventBus.getDefault().post(new LocationChangedEvent(null));

        if(DEBUG)Log.i(TAG, "=== Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if(DEBUG)Log.i(TAG, "=== onLocationChanged: Latitude: " + this.location.getLatitude() + " Longitude: " + this.location.getLongitude());
        sendDataFrame(location);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        EventBus.getDefault().post(new LocationChangedEvent(null));

        if(DEBUG) Log.i(TAG,"onConnectionFailed:"+connectionResult.getErrorCode()+","+connectionResult.getErrorMessage());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public TrackLocationService getServerInstance() {
            return TrackLocationService.this;
        }
    }


}
