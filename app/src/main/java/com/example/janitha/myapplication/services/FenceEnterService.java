package com.example.janitha.myapplication.services;


import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.janitha.myapplication.broadcast_receivers.EnterHomeLocationFenceBroadcastReceiver;
import com.example.janitha.myapplication.broadcast_receivers.ExitHomeLocationFenceBroadcastReceiver;
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
    private SharedPreferences prefs;
    private String json;
    private Object obj;
    private static int count = 0;
    public static final String FENCE_RECEIVER_ACTION = "com.example.janitha.myapplication.FENCE_RECEIVE";

    private Location homeLocation;
    private Location workLocation;
    private int homeLocationRadius;
    private int workLocationRadius;

    public GoogleApiClient googleApiClient;
    public static Context ctx;
    private PendingIntent pendingIntent;

    private AwarenessFence enterHomeLocationFence;
    private AwarenessFence inHomeLocationFence;
    private AwarenessFence exitHomeLocationFence;
    private EnterHomeLocationFenceBroadcastReceiver enterHomeLocationFenceBroadcastReceiver;
    private InHomeLocationFenceBroadcastReceiver inHomeLocationFenceBroadcastReceiver;
    private ExitHomeLocationFenceBroadcastReceiver exitHomeLocationFenceBroadcastReceiver;

    private AwarenessFence headphoneFence;
    private HeadphoneFenceBroadcastReceiver headphoneFenceBroadcastReceiver;

    private AwarenessFence enterWorkLocationFence;
    private AwarenessFence inWorkLocationFence;
    private AwarenessFence exitWorkLocationFence;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ctx = getApplicationContext();
//        ctx = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        //Getting the home location from Shared Preferences
//        SharedPreferences prefs = ctx.getSharedPreferences("com.example.janitha.myapplication.HOME_LOCATION", Context.MODE_PRIVATE);
        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATION", null);
        obj = new Gson().fromJson(json, Location.class);

        homeLocation = (Location) obj;

        if (homeLocation == null) {
            homeLocation = new Location("Empty Home Location");
            homeLocation.setLatitude(0.0f);
            homeLocation.setLongitude(0.0f);
        }
        Log.i("FenceService", "onStartCmd  HomeLoc: Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude());

        //Getting the HomeLoc Radius
//        prefs = ctx.getSharedPreferences("com.example.janitha.myapplication.HOME_LOCATION_FENCE_RADIUS", Context.MODE_PRIVATE);
        json = prefs.getString("com.example.janitha.myapplication.HOME_LOCATION_FENCE_RADIUS", null);
        obj = new Gson().fromJson(json, Integer.class);

        if (obj != null) {
            homeLocationRadius = (int) obj;
        } else {
            homeLocationRadius = 49;
        }

        //Getting the work location from Shared Preferences
        json = prefs.getString("com.example.janitha.myapplication.WORK_LOCATION", null);
        obj = new Gson().fromJson(json, Location.class);

        workLocation = (Location) obj;

        if (workLocation == null) {
            workLocation = new Location("Empty Work Location");
            workLocation.setLatitude(0.0f);
            workLocation.setLongitude(0.0f);
        }
        Log.i("FenceService", "onStartCmd  WorkLoc: Lat:" + workLocation.getLatitude() + " Long:" + workLocation.getLongitude());

        //Getting the WorkLoc Radius
        json = prefs.getString("com.example.janitha.myapplication.WORK_LOCATION_FENCE_RADIUS", null);
        obj = new Gson().fromJson(json, Integer.class);

        if (obj != null) {
            workLocationRadius = (int) obj;
        } else {
            workLocationRadius = 49;
        }


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(ctx)
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
        pendingIntent = PendingIntent.getBroadcast(ctx, 10001, tempIntent, 0);

        //------------------ Creating Fences - starts ----------------------- //

        // Create a 'Home LocationFence.entering' Fence.
        try {
            enterHomeLocationFence = LocationFence.entering(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
            Log.i("FenceService", "enterHomeLocFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'Home LocationFence.in' Fence.
        try {
            inHomeLocationFence = LocationFence.in(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius, 1);
            Log.i("FenceService", "inHomeLocationFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'Home LocationFence.exit' Fence.
        try {
            exitHomeLocationFence = LocationFence.exiting(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
            Log.i("FenceService", "exitHomeLocFence  Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude()+" Radius: "+homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'Work LocationFence.entering' Fence.
        try {
            enterWorkLocationFence = LocationFence.entering(workLocation.getLatitude(), workLocation.getLongitude(), workLocationRadius);
            Log.i("FenceService", "enterWorkLocFence  Lat:" + workLocation.getLatitude() + " Long:" + workLocation.getLongitude()+" Radius: "+workLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'Work LocationFence.in' Fence.
        try {
            inWorkLocationFence = LocationFence.in(workLocation.getLatitude(), workLocation.getLongitude(), workLocationRadius, 1);
            Log.i("FenceService", "inWorkLocationFence  Lat:" + workLocation.getLatitude() + " Long:" + workLocation.getLongitude()+" Radius: "+workLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Create a 'Work LocationFence.exit' Fence.
        try {
            exitWorkLocationFence = LocationFence.exiting(workLocation.getLatitude(), workLocation.getLongitude(), workLocationRadius);
            Log.i("FenceService", "exitWorkLocFence  Lat:" + workLocation.getLatitude() + " Long:" + workLocation.getLongitude()+" Radius: "+workLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        //Create a Headphone fence
        headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        //------------------ Creating Fences - ends ----------------------- //


        //------------------ Registering Fences - Starts ----------------------- //

        registerFence("enteringHomeLocationFenceKey", enterHomeLocationFence);
        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
        registerFence("exitHomeLocationFenceKey",exitHomeLocationFence);

        registerFence("enteringWorkLocationFenceKey", enterWorkLocationFence);
        registerFence("inWorkLocationFenceKey", inWorkLocationFence);
        registerFence("exitWorkLocationFenceKey",exitWorkLocationFence);

        registerFence("headphoneFenceKey", headphoneFence);

        //------------------ Registering Fences - Ends ----------------------- //

        //------------------ Registering Broadcast Receivers - Starts ----------------------- //
        enterHomeLocationFenceBroadcastReceiver = new EnterHomeLocationFenceBroadcastReceiver();
        registerReceiver(enterHomeLocationFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        inHomeLocationFenceBroadcastReceiver = new InHomeLocationFenceBroadcastReceiver();
        registerReceiver(inHomeLocationFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        exitHomeLocationFenceBroadcastReceiver = new ExitHomeLocationFenceBroadcastReceiver();
        registerReceiver(exitHomeLocationFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));



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
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

//        registerFence("enteringFenceKey", enterHomeLocationFence);
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
//        registerFence("enteringFenceKey", enterHomeLocationFence);
//        registerFence("inHomeLocationFenceKey", inHomeLocationFence);
//        mLocationView.setText("Location received: " + location.toString());
        Log.i("FenceService","Loc_Update: "+location.toString());
    }
}



