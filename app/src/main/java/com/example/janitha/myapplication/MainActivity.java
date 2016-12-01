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

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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

    public CardView cardView_homeLocationTitle;
    public ImageView imageView_homeLocationWeatherIcon;
    public TextView textView_tempHomeLoc;
    public TextView textView_homeLocWeatherDescription;

    public CardView cardView_workLocationTitle;
    public ImageView imageView_workLocationWeatherIcon;
    public TextView textView_tempWorkLoc;
    public TextView textView_workLocWeatherDescription;

    public Switch switch_serviceStatus;
    public Switch switch_trafficAlerts;
    public Switch switch_soundProfile;
    public Switch switch_wifiStatus;

    public Button button_preference;
    public Button button_workSchedule;

    public TextView textView_headphoneStatus;
    public TextView textView_wifiStatus;
    public TextView textView_soundProfileStatus;
    public TextView textView_userLocationStatus;


    public static final String LAST_HOME_LOCATION = "com.example.janitha.myapplication.LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        setContentView(R.layout.home_activity);
        mainActivity = this;
        mainContext = this;

        cardView_homeLocationTitle = (CardView) findViewById(R.id.cardView_homeLocationTitle);
        imageView_homeLocationWeatherIcon = (ImageView) findViewById(R.id.imageView_homeLocationWeatherIcon);
        textView_tempHomeLoc = (TextView) findViewById(R.id.textView_tempHomeLoc);
        textView_homeLocWeatherDescription = (TextView) findViewById(R.id.textView_homeLocWeatherDescription);

        cardView_workLocationTitle = (CardView) findViewById(R.id.cardView_workLocationTitle);
        imageView_workLocationWeatherIcon = (ImageView) findViewById(R.id.imageView_workLocationWeatherIcon);
        textView_tempWorkLoc = (TextView) findViewById(R.id.textView_tempWorkLoc);
        textView_workLocWeatherDescription = (TextView) findViewById(R.id.textView_workLocWeatherDescription);

        switch_serviceStatus = (Switch) findViewById(R.id.switch_serviceStatus);
        switch_trafficAlerts = (Switch) findViewById(R.id.switch_trafficAlerts);
        switch_soundProfile = (Switch) findViewById(R.id.switch_soundProfile);
        switch_wifiStatus = (Switch) findViewById(R.id.switch_wifiStatus);

        button_preference = (Button) findViewById(R.id.button_preference);
        button_workSchedule = (Button) findViewById(R.id.button_workSchedule);

        textView_headphoneStatus = (TextView) findViewById(R.id.textView_headphoneStatus);
        textView_wifiStatus = (TextView) findViewById(R.id.textView_wifiStatus);
        textView_soundProfileStatus = (TextView)findViewById(R.id.textView_soundProfileStatus);
        textView_userLocationStatus = (TextView) findViewById(R.id.textView_userLocationStatus);


        // card view title click actions  -- starts -- //
        cardView_homeLocationTitle.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent intent = new Intent(MainActivity.this, HomeLocationActivity.class);
                intent.putExtra(LAST_HOME_LOCATION, AppData.HOME_LOCATION);
                intent.putExtra("UserLocationType",1);
                startActivity(intent);
            }
        });

        cardView_workLocationTitle.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent intent = new Intent(MainActivity.this, HomeLocationActivity.class);
                intent.putExtra(LAST_HOME_LOCATION, AppData.WORK_LOCATION);
                intent.putExtra("UserLocationType",2);
                startActivity(intent);
            }
        });

        // card view title click actions  -- ends -- //


        // Button click actions  -- starts -- //
        button_preference.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                //please change the invoking activity to Preference Activity
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intent);
            }
        });

        button_workSchedule.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                //please change the invoking activity to Preference Activity
                Intent intent = new Intent(MainActivity.this, WorkSheduleActivity.class);
                startActivity(intent);
            }
        });
        // Button click actions  -- ends -- //


        // Switch click actions  -- starts -- //
        switch_serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO

            }
        });

        switch_trafficAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO

            }
        });

        switch_soundProfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO

            }
        });

        switch_wifiStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO

            }
        });

        // Switch click actions  -- ends -- //



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

        updateTextView_wifiStatus(this);
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
//                TextView textV1 = (TextView) findViewById(R.id.enter);
//                textV1.setText(t);
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
//                    Log.i("wif", "successfully wifi onned");
                    Toast.makeText(context, "Wifi Turned on", Toast.LENGTH_SHORT).show();
                    textView_wifiStatus.setText("ON");
                }

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                Toast.makeText(context, "Sound profile: Normal", Toast.LENGTH_SHORT).show();


                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    textView_soundProfileStatus.setText("NORMAL");
                } else {
                    textView_soundProfileStatus.setText("SILENT");
                }

//                if (!audioManager) {
//                    wifiManager.setWifiEnabled(true);
//                    Log.i("wif", "successfully wifi onned");
//                }
            }
        });
    }

    public void updateTextView_wifiStatus(final Context context) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
//                TextView textV1 = (TextView) findViewById(R.id.enter);
//                textV1.setText(t);
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                if (wifiManager.isWifiEnabled()) {
//                    wifiManager.setWifiEnabled(true);
                    Log.i("wif", "successfully wifi onned");
                    textView_wifiStatus.setText("ON");
                } else {
                    textView_wifiStatus.setText("OFF");
                }

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    textView_soundProfileStatus.setText("NORMAL");
                } else {
                    textView_soundProfileStatus.setText("SILENT");
                }

//                if (!audioManager) {
//                    wifiManager.setWifiEnabled(true);
//                    Log.i("wif", "successfully wifi onned");
//                }
            }
        });
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

