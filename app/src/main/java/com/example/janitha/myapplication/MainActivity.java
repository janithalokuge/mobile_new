package com.example.janitha.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.janitha.myapplication.async_tasks.RetrieveLocWeatherTask;
import com.example.janitha.myapplication.services.FenceEnterService;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import android.widget.Toast;


import static com.google.android.gms.common.api.GoogleApiClient.*;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks {

    private static MainActivity ins;
    public static Activity mainActivity;
    public static Context mainContext;

    public static GoogleApiClient client;
    public static Location currentLocation;

    public TextView myTextView;
    public TextView weather;
    public TextView weatherCondition;
    public TextView lon;
    public Button button_home_location;
    public TextView isUserHome;
    public TextView editText;


//    private PendingIntent myPendingIntent;
//    AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
//
//    private MyFenceReceiver myFenceReceiver;

    public static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";

    public static PendingIntent mFencePendingIntent;

    public static final String LAST_HOME_LOCATION = "com.example.janitha.myapplication.LOCATION";

    private double homeLat = 6.91823;
    private double homeLon = 79.92891;
    private double radius = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        setContentView(R.layout.activity_main);
        mainActivity = this;
        mainContext = this;

        String myText = "0";
        myTextView = (TextView) findViewById(R.id.myTextView);
        myTextView.setText(myText);

        weather = (TextView) findViewById(R.id.weather);
        weather.setText(myText);

        weatherCondition = (TextView) findViewById(R.id.weatherCondition);
        weatherCondition.setText("Unknown conditoin sri lanka");

        lon = (TextView) findViewById(R.id.lon);
        lon.setText("location");

        isUserHome = (TextView) findViewById(R.id.isUserHome);
        isUserHome.setText("Is User Home or not : ");


        editText = (TextView) findViewById(R.id.editText_message);

        button_home_location = (Button)findViewById(R.id.button_home_location);
        button_home_location.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){

                Intent intent = new Intent(MainActivity.this, HomeLocationActivity.class);
                intent.putExtra(LAST_HOME_LOCATION, AppData.HOME_LOCATION);
                startActivity(intent);
            }
        });



        Log.d("TAG", "onCreate() Restoring previous state");


        if (client == null) {
            client = new Builder(this)
                    .addApi(Awareness.API)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();

            client.connect();
        }

        if (client != null) {
            Toast.makeText(this, "Google API Client = OK ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Google API Client = Null ", Toast.LENGTH_SHORT).show();
        }

        updateLocations(this);

        // 1 = home location weather update
        // 2 = work location weather update
        new RetrieveLocWeatherTask(1).execute();


        //Start FenceEnterService Service
        Intent fenceEnterServiceIntent = new Intent(MainActivity.mainActivity, FenceEnterService.class);
        fenceEnterServiceIntent.putExtra("HomeLocation_FenceEnterStatus", "User entered Home Location area");
        if (isMyServiceRunning(FenceEnterService.class) == false) {
            getApplicationContext().startService(fenceEnterServiceIntent);
        }
    }

    public static MainActivity getInstace() {
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.headphoneStatus);
                textV1.setText(t);
                if (t.equals("pluged")) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sonyericsson.music");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }
            }
        });
    }

    public void updateTheTextViewhome(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.home);
                textV1.setText(t);
            }
        });
    }

    public void updateTheTextViewenter(final String t, final Context context) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.enter);
                textV1.setText(t);
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    Log.i("wif", "successfully wifi onned");
                }

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//                if (!audioManager) {
//                    wifiManager.setWifiEnabled(true);
//                    Log.i("wif", "successfully wifi onned");
//                }
            }
        });
    }

    public void updateEditText_message(String str) {
        editText.setText(str);
    }


    public static void registerFences(GoogleApiClient apiClient, PendingIntent pendingIntent, Location homeLocation, int homeLocationRadius) {
        // Create a fence.
//        AwarenessFence locationFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

        // Create a fence.
        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

// Register the fence to receive callbacks.
// The fence key uniquely identifies the fence.
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .addFence("headphoneFenceKey", headphoneFence, pendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i("headphoneFence", "Fence was successfully registered.");
                        } else {
                            Log.e("headphoneFence", "Fence could not be registered: " + status);
                        }
                    }
                });


        try {
            AwarenessFence homeFence = LocationFence.in(6.91823, 79.92891, 1000, 1000);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("Error_siri", "Awareness API Permission Error!!!");
        }
//        AwarenessFence homeFence = LocationFence.in(6.91199, 79.92853, 10, 1000);

//        Awareness.FenceApi.updateFences(
//                client,
//                new FenceUpdateRequest.Builder()
//                        .addFence("locationFenceKey", homeFence, mFencePendingIntent)
//                        .build())
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        if (status.isSuccess()) {
//                            Log.i("e", "Fence was successfully registered. home fence");
//                        } else {
//                            Log.e("e2", "Fence could not be registered: " + status);
//                        }
//                    }
//                });

//        AwarenessFence enterFence = LocationFence.entering(homeLat, homeLon, radius);
        AwarenessFence enterFence = null;
        try {
            Log.i("RegisterFence", "Lat:" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude());
            enterFence = LocationFence.entering(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("Error_siri", "Awareness API Permission Error!!!");
        }
        Log.i("registerFences", "EnterFence: HomeLoc:Lat" + homeLocation.getLatitude() + " Long:" + homeLocation.getLongitude() + " Radius: " + homeLocationRadius);
//        AwarenessFence homeFence = LocationFence.in(6.91199, 79.92853, 10, 1000);

        Awareness.FenceApi.updateFences(
                apiClient,
                new FenceUpdateRequest.Builder()
                        .addFence("enteringFenceKey", enterFence, pendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i("e", "Fence was successfully registered. entering fence");
                        } else {
                            Log.e("e2", "Fence could not be registered: " + status);
                        }
                    }
                });

//        AwarenessFence exitFence = LocationFence.exiting(6.91823, 79.92891,1000);
//        AwarenessFence exitFence = null;
//        try {
//            exitFence = LocationFence.exiting(homeLocation.getLatitude(), homeLocation.getLongitude(), homeLocationRadius);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            Log.e("Error_siri","Awareness API Permission Error!!!");
//        }
//        AwarenessFence homeFence = LocationFence.in(6.91199, 79.92853, 10, 1000);

//        Awareness.FenceApi.updateFences(
//                apiClient,
//                new FenceUpdateRequest.Builder()
//                        .addFence("locationFenceKey", exitFence, pendingIntent)
//                        .build())
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        if (status.isSuccess()) {
//                            Log.i("e", "Fence was successfully registered. exitinng fence");
//                        } else {
//                            Log.e("e2", "Fence could not be registered: " + status);
//                        }
//                    }
//                });

    }

//    private void unregisterFence() {
//        Awareness.FenceApi.updateFences(
//                client,
//                new FenceUpdateRequest.Builder()
//                        .removeFence("headphoneFenceKey")
//                        .build()).setResultCallback(new ResultCallbacks<Status>() {
//            @Override
//            public void onSuccess(@NonNull Status status) {
//                Log.i("un", "Fence " + "headphoneFenceKey" + " successfully removed.");
//            }
//
//            @Override
//            public void onFailure(@NonNull Status status) {
//                Log.i("un", "Fence " + "headphoneFenceKey" + " could NOT be removed.");
//            }
//        });
//    }

    public static void updateLocations(Activity currentActivity) {
        //----------------- Setup Work/Home Location and Fence Radi - Starts --------------------------//

        //SETUP HOME LOCATION
        if (AppData.getData(currentActivity, AppData.STR_HOME_LOCATOIN, Location.class) != null) {
            AppData.HOME_LOCATION = (Location) AppData.getData(currentActivity, AppData.STR_HOME_LOCATOIN, Location.class);
        } else {
            AppData.HOME_LOCATION = new Location("EmptyLocation");
            AppData.HOME_LOCATION.setLatitude(0.0f);
            AppData.HOME_LOCATION.setLongitude(0.0f);
        }
        Log.i("updateLocations(HL)", "Lat:" + AppData.HOME_LOCATION.getLatitude() + " Long:" + AppData.HOME_LOCATION.getLongitude());


        if (AppData.getData(currentActivity, AppData.STR_HOME_LOCATOIN_FENCE_RADIUS, Integer.class) != null) {
            AppData.HOME_LOCATION_FENCE_RADIUS = (int) AppData.getData(currentActivity, AppData.STR_HOME_LOCATOIN_FENCE_RADIUS, Integer.class);
        } else {
            AppData.HOME_LOCATION_FENCE_RADIUS = 100;
        }

        //SETUP WORK LOCATION
        if (AppData.getData(currentActivity, AppData.STR_WORK_LOCATOIN, Location.class) != null) {
            AppData.WORK_LOCATION = (Location) AppData.getData(currentActivity, AppData.STR_WORK_LOCATOIN, Location.class);
        } else {
            AppData.WORK_LOCATION = new Location("");
            AppData.WORK_LOCATION.setLatitude(0.0f);
            AppData.WORK_LOCATION.setLongitude(0.0f);
        }
        if (AppData.getData(currentActivity, AppData.STR_WORK_LOCATOIN_FENCE_RADIUS, Integer.class) != null) {
            AppData.WORK_LOCATION_FENCE_RADIUS = (int) AppData.getData(currentActivity, AppData.STR_WORK_LOCATOIN_FENCE_RADIUS, Integer.class);
        } else {
            AppData.WORK_LOCATION_FENCE_RADIUS = 100;
        }

        //----------------- Setup Work/Home Location and Fence Radi - Ends --------------------------//
    }

    protected void onStart() {
        client.connect();
        super.onStart();
        updateLocations(mainActivity);
//        registerFences(client, mFencePendingIntent, AppData.HOME_LOCATION, AppData.HOME_LOCATION_FENCE_RADIUS);

        //LocationWeatherNotification.notify(this, "Ammo", 4);
//        registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
//        registerReceiver(efenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        //This is moved to FenceEnterService class
//        registerReceiver(enterFenceBroadcastReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));


//        registerReceiver(hfenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        //client.disconnect();
        super.onStop();
//        unregisterFence();
//        unregisterReceiver(fenceReceiver);
//        unregisterReceiver(enterFenceBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.i("suemar", "OnConnected");

//        Awareness.FenceApi.updateFences(
//                client,
//                new FenceUpdateRequest.Builder()
//                        .addFence("headphoneFenceKey", headphoneFence, myPendingIntent)
//                        .build())
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        if (status.isSuccess()) {
//                            Log.i(TAG, "Fence was successfully registered.");
//                        } else {
//                            Log.e(TAG, "Fence could not be registered: " + status);
//                        }
//                    }
//                });

        Awareness.SnapshotApi.getHeadphoneState(client)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
//                        Log.d("tst", "awa");
                        if (!headphoneStateResult.getStatus().isSuccess()) {
//                            Log.i("suemar", "error");
//                            Log.d("tst2", "gatte na");
                            return;
                        }
//                        Log.d("tst3", "gatta");
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
//                        Log.i("suemarstat", "headphone status" + headphoneState.getState());
                        int x = headphoneState.getState();
                        String y = Integer.toString(x);
                        myTextView.setText(y);
                    }
                });

        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e("lo", "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
//                        Log.i("lol", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());

                        String s = "Lat ";
                        String s1 = Double.toString(location.getLatitude());
                        s = s.concat(s1);
                        s = s.concat(" Lon ");
                        String s2 = Double.toString(location.getLongitude());
                        s = s.concat(s2);

                        Double lat = location.getLatitude();
                        Double longitude = location.getLongitude();
//                        Location.c

                        double ltd = lat - homeLat;
                        double lgd = longitude - homeLon;

//                        Math.pow(2.4,3);

//                        double d = Math.sqrt(Math.pow(ltd, 2) + Math.pow(lgd, 2));

                        double earthRadius = 6371000; //meters
                        double dLat = Math.toRadians(lat - homeLat);
                        double dLng = Math.toRadians(longitude - homeLon);
                        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                Math.cos(Math.toRadians(homeLat)) * Math.cos(Math.toRadians(lat)) *
                                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                        float dist = (float) (earthRadius * c);

//                        return dist;

//                        float[] reulsts = Location.distanceBetween(lat, lon, lat, lon);
//                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), homeLat, homeLon);


                        if (dist < radius) {
                            isUserHome.setText("Is User Home or not : Yes");
                        } else {
                            isUserHome.setText("Is User Home or not : NO");
                        }

                        lon.setText(s);

                    }
                });

        Awareness.SnapshotApi.getWeather(client)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
//                        Log.d("w", "awa");
                        if (!weatherResult.getStatus().isSuccess()) {
//                            Log.i("w", "error weather");
                            return;
                        }
                        for (int i = 0; i < weatherResult.getWeather().getConditions().length; i++)
//                            Log.i("w", "w " + weatherResult.getWeather().getConditions()[i]);

                            weather.setText(Float.toString(weatherResult.getWeather().getTemperature(Weather.CELSIUS)));
                        int[] array = weatherResult.getWeather().getConditions();

                        String s = "Weather: ";

                        for (int i = 0; i < array.length; i++) {
//                            Log.i("wc", "hi"+array[i]);


                            switch (array[i]) {
                                case 1:
                                    s = s.concat("CONDITION_CLEAR");
                                    break;
                                case 2:
                                    String str = "CONDITION_CLOUDY";
                                    s = s.concat(str);
//                                    Log.i("wc", "awado");
//                                    s = "CONDITION_CLOUDY";
                                    break;
                                case 6:
                                    s = s.concat("CONDITION_RAINY");
                                    break;
                                case 0:
                                    s = s.concat("CONDITION_UNKNOWN");
                                    break;
                                case 9:
                                    s = s.concat("CONDITION_WINDY");
                                    break;
                                default:
                                    s = s.concat(Integer.toString(array[i]));


                            }
                        }

                        weatherCondition.setText(s);

                    }
                });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Getting the current GPS location of the user
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (currentLocation == null) {  //Then set currentLocation to default coordinates
            currentLocation = new Location("Default_Location");
            currentLocation.setLatitude(0.0f);
            currentLocation.setLongitude(0.0f);
            Log.i("LastLocation", "Set to Default Location = Lat 0.0f, Long 0.0f");
            Toast.makeText(this, "Set to Default Location = Lat 0.0f, Long 0.0f", Toast.LENGTH_SHORT).show();

        } else {
            Log.i("LastLocation", "Lat=" + currentLocation.getLatitude()
                    + "  Long=" + currentLocation.getLongitude());
            Toast.makeText(this, "Lat=" + currentLocation.getLatitude()
                    + "  Long=" + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public final static String EXTRA_MESSAGE = "com.example.janitha.myapplication.MESSAGE";

    public void sendMessage(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

