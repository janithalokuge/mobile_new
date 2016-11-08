package com.example.janitha.myapplication.services;


import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.janitha.myapplication.MainActivity;
import com.example.janitha.myapplication.WorkLocationWeatherNotification;
import com.example.janitha.myapplication.broadcast_receivers.EnterFenceBroadcastReceiver;
import com.example.janitha.myapplication.broadcast_receivers.HeadphoneFenceBroadcastReceiver;
import com.example.janitha.myapplication.broadcast_receivers.InHomeLocationFenceBroadcastReceiver;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Date;


import static android.provider.Settings.System.DATE_FORMAT;


/**
 * Created by Siri on 11/4/2016.
 */

public class FenceEnterService extends Service implements GoogleApiClient.ConnectionCallbacks,LocationListener {
    SharedPreferences prefs;
    String json;
    Object obj;
    static int count = 0;
    public static final String FENCE_RECEIVER_ACTION = "com.example.janitha.myapplication.FENCE_RECEIVE";
    LocationRequest  mLocationRequest;


    public GoogleApiClient googleApiClient;
    public static Context ctx;
    private PendingIntent pendingIntent;
    private AwarenessFence enterFence;
    private AwarenessFence inHomeLocationFence;
    private AwarenessFence headphoneFence;
    private EnterFenceBroadcastReceiver enterFenceBroadcastReceiver;
    private HeadphoneFenceBroadcastReceiver headphoneFenceBroadcastReceiver;
    private InHomeLocationFenceBroadcastReceiver inHomeLocationFenceBroadcastReceiver;

    @Override
    public void onCreate()
    {
//        super.onCreate();
////        ctx = getApplicationContext();
//        ctx = this;
//        if(ctx == null){
//            Log.e("FenceService","ERROR! : Context is NULL");
//        }
//        else{
//            Log.e("FenceService","Context is OK");
//        }
//        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//        prefs = this.getSharedPreferences("com.example.janitha.myapplication.HOME_LOCATION", Context.MODE_PRIVATE);
//        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATION", null);
//        obj = new Gson().fromJson(json, Location.class);
//
//        Location homeLocation = (Location) obj;
//
//        if (homeLocation == null) {
//            homeLocation = new Location("EmptyLocation");
//            homeLocation.setLatitude(0.0f);
//            homeLocation.setLongitude(0.0f);
//        }
//        Log.i("FenceService", "onStartCmd  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude());
//
//        //Getting the HomeLoc Radius
//        prefs = this.getSharedPreferences("com.example.janitha.myapplication.HOME_LOCATION_FENCE_RADIUS", Context.MODE_PRIVATE);
//        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATION_FENCE_RADIUS", null);
//        obj = new Gson().fromJson(json, Integer.class);
//        int homeLocationRadius;
//
//        if (obj != null) {
//            homeLocationRadius = (int) obj;
//        } else {
//            homeLocationRadius = 99;
//        }
//
//        //TODO
//
//        if (googleApiClient == null) {
//            googleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Awareness.API)
//                    .addApi(LocationServices.API)
//                    .addConnectionCallbacks(this)
//                    .build();
//
//            googleApiClient.connect();
//        }
//        if (googleApiClient != null) {
//            Log.i("FenceService", "Google API Client = OK ");
//        } else {
//            Log.e("FenceService", "Google API Client = Null ");
//        }
//
//        Intent tempIntent = new Intent(FENCE_RECEIVER_ACTION);
//        pendingIntent = PendingIntent.getBroadcast(this, 10001, tempIntent, 0);
//
//        //------------------ Creating Fences - starts ----------------------- //
//
//        // Create a 'LocationFence.entering' Fence.
//        try {
//            enterFence = LocationFence.entering(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
//            Log.i("FenceService", "enterFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//
//        // Create a 'LocationFence.in' Fence.
//        try {
//            inHomeLocationFence = LocationFence.in(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius, 1);
//            Log.i("FenceService", "inHomeLocationFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//
//        //Create a Headphone fence
//        headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
//
//        //------------------ Creating Fences - ends ----------------------- //
//
//        //------------------ Registering Fences - Starts ----------------------- //
//
//        //Registering 'LocationFence.entering' Fence
//        registerFence("enteringFenceKey", enterFence);
//
////        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
//        registerFence("headphoneFenceKey", headphoneFence);
//
//
//        //------------------ Registering Fences - Ends ----------------------- //
//
//        enterFenceBroadcastReceiver = new EnterFenceBroadcastReceiver();
//        registerReceiver(enterFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
//
//        inHomeLocationFenceBroadcastReceiver = new InHomeLocationFenceBroadcastReceiver();
////        registerReceiver(inHomeLocationFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
//
//        headphoneFenceBroadcastReceiver = new HeadphoneFenceBroadcastReceiver();
//        registerReceiver(headphoneFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ctx = this;
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences prefs = this.getSharedPreferences("com.example.janitha.myapplication.HOME_LOCATION", Context.MODE_PRIVATE);
        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATION", null);
        obj = new Gson().fromJson(json, Location.class);

        Location homeLocation = (Location) obj;

        if (homeLocation == null) {
            homeLocation = new Location("EmptyLocation");
            homeLocation.setLatitude(0.0f);
            homeLocation.setLongitude(0.0f);
        }
        Log.i("FenceService", "onStartCmd  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude());

        //Getting the HomeLoc Radius
        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATOIN_FENCE_RADIUS", null);
        obj = new Gson().fromJson(json, Integer.class);
        int homeLocationRadius;

        if (obj != null) {
            homeLocationRadius = (int) obj;
        } else {
            homeLocationRadius = 99;
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Awareness.API)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();

            googleApiClient.connect();
        }
        if (googleApiClient != null) {
            Log.i("FenceService", "Google API Client = OK ");
        } else {
            Log.e("FenceService", "Google API Client = Null ");
        }

        Intent tempIntent = new Intent(FENCE_RECEIVER_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 10001, tempIntent, 0);

        //------------------ Creating Fences - starts ----------------------- //

        // Create a 'LocationFence.entering' Fence.
        try {
            enterFence = LocationFence.entering(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
            Log.i("FenceService", "enterFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'LocationFence.in' Fence.
        try {
            inHomeLocationFence = LocationFence.in(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius, 1);
            Log.i("FenceService", "inHomeLocationFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        //Create a Headphone fence
        headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        //------------------ Creating Fences - ends ----------------------- //


        //------------------ Registering Fences - Starts ----------------------- //

        registerFence("enteringFenceKey", enterFence);
        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
        registerFence("headphoneFenceKey", headphoneFence);

        //------------------ Registering Fences - Ends ----------------------- //

        //------------------ Registering Broadcast Receivers - Starts ----------------------- //
        enterFenceBroadcastReceiver = new EnterFenceBroadcastReceiver();
        registerReceiver(enterFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        inHomeLocationFenceBroadcastReceiver = new InHomeLocationFenceBroadcastReceiver();
        registerReceiver(inHomeLocationFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        headphoneFenceBroadcastReceiver = new HeadphoneFenceBroadcastReceiver();
        registerReceiver(headphoneFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
        //------------------ Registering Broadcast Receivers - Ends ----------------------- //

        count++;
        Log.i("FenceService", "Count: " + count);

        return Service.START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

//        registerFence("enteringFenceKey", enterFence);
//        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
//        registerFence("headphoneFenceKey", headphoneFence);

//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000); // Update location every 500 milliseconds
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void registerFence(final String fenceKey, final AwarenessFence fence) {
        Awareness.FenceApi.updateFences(
                googleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(fenceKey, fence, pendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.i("FenceService", "RegisterFence: "+fenceKey+" was successfully registered.");
                            queryFence(fenceKey);
                        } else {
                            Log.e("FenceService", "RegisterFence: "+fenceKey+" could not be registered: " + status);
                        }
                    }
                });
    }

    protected void queryFence(final String fenceKey) {
        Awareness.FenceApi.queryFences(googleApiClient,
                FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e("FenceService", "queryFence: Could not query fence: " + fenceKey);
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();
                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            Log.i("FenceService", "queryFence: Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()
                                    + ", lastUpdateTime="
                                    + DATE_FORMAT.format(""+new Date(fenceState.getLastFenceUpdateTimeMillis())));
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
//        registerFence("enteringFenceKey", enterFence);
//        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
//        mLocationView.setText("Location received: " + location.toString());
        Log.i("FenceService","Loc_Update: "+location.toString());
    }
}



