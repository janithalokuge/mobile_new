package com.example.janitha.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private static MainActivity ins;


    private GoogleApiClient client;
    public TextView myTextView;
    public TextView weather;
    public TextView weatherCondition;
    public TextView lon;
//    public TextView pluge;


//    private PendingIntent myPendingIntent;
//    AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
//
//    private MyFenceReceiver myFenceReceiver;

    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";
    private HeadphoneFenceBroadcastReceiver fenceReceiver;
    private PendingIntent mFencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;


        setContentView(R.layout.activity_main);

//        setContentView(R.layout.layoutName);
//        TextView textView = (TextView) findViewById(R.id.textViewName);
//        textView.setText("text you want to display");

        String myText = "0";
        myTextView = (TextView) findViewById(R.id.myTextView);
        myTextView.setText(myText);

        weather = (TextView) findViewById(R.id.weather);
        weather.setText(myText);

        weatherCondition = (TextView) findViewById(R.id.weatherCondition);
        weatherCondition.setText("Unknown conditoin sri lanka");

        lon = (TextView) findViewById(R.id.lon);
        lon.setText("location");

//        pluge = (TextView) findViewById(R.id.pluge);
//        pluge.setText("Head");




        Log.d("TAG", "onCreate() Restoring previous state");

        android.content.Context context;
        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        client.connect();

        fenceReceiver = new HeadphoneFenceBroadcastReceiver();

        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        mFencePendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                10001,
                intent,
                0);

//        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
//        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        MyFenceReceiver = new myFenceReceiver();
//        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

    }

    public static MainActivity  getInstace(){
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.pluge);
                textV1.setText(t);
            }
        });
    }


    public void setHeadState () {

    }

    private void registerFences() {
        // Create a fence.
        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .addFence("headphoneFenceKey", headphoneFence, mFencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i("e", "Fence was successfully registered.");
                        } else {
                            Log.e("e2", "Fence could not be registered: " + status);
                        }
                    }
                });

    }

    private void unregisterFence() {
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .removeFence("headphoneFenceKey")
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i("un", "Fence " + "headphoneFenceKey" + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i("un", "Fence " + "headphoneFenceKey" + " could NOT be removed.");
            }
        });
    }

    protected void onStart() {
        super.onStart();
        registerFences();
        registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFence();
        unregisterReceiver(fenceReceiver);
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("suemar", "OnConnected");

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
                        Log.d("tst", "awa");
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            Log.i("suemar", "error");
                            Log.d("tst2", "gatte na");
                            return;
                        }
                        Log.d("tst3", "gatta");
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                        Log.i("suemarstat", "headphone status" + headphoneState.getState());
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
                        Log.i("lol", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());

                        String s = "Lat ";
                        String s1 = Double.toString(location.getLatitude());
                        s = s.concat(s1);
                        s = s.concat(" Lon ");
                        String s2 = Double.toString(location.getLongitude());
                        s = s.concat(s2);

                        lon.setText(s);

                    }
                });

        Awareness.SnapshotApi.getWeather(client)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        Log.d("w", "awa");
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.i("w", "error weather");
                            return;
                        }
                        for(int i = 0; i < weatherResult.getWeather().getConditions().length; i++)
                            Log.i("w", "w " + weatherResult.getWeather().getConditions()[i]);

                        weather.setText(Float.toString(weatherResult.getWeather().getTemperature(Weather.CELSIUS)));
                        int[] array = weatherResult.getWeather().getConditions();

                        String s = "Weather: ";

                        for(int i=0;i<array.length;i++) {
                            Log.i("wc", "hi"+array[i]);


                            switch (array[i]) {
                                case 1 :
                                    s = s.concat("CONDITION_CLEAR");
                                    break;
                                case 2 :
                                    String str = "CONDITION_CLOUDY";
                                    s = s.concat(str);
                                    Log.i("wc", "awado");
//                                    s = "CONDITION_CLOUDY";


                                    break;
                                case 6 :
                                    s = s.concat("CONDITION_RAINY");
                                    break;
                                case 0 :
                                    s = s.concat("CONDITION_UNKNOWN");
                                    break;
                                case 9 :
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

class HeadphoneFenceBroadcastReceiver extends BroadcastReceiver {

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//
//
//    }

//    public TextView pluge;

//    public HeadphoneFenceBroadcastReceiver(TextView pluge) {
//
//    }


    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        Log.d("re", "Fence Receiver Received");
//        pluge = (TextView) findViewById(R.id.pluge);
//        pluge.setText("Head");

//        MainActivity currentActivity = (MainActivity)context.getApplicationContext().;
//        currentActivity.ligaInternet();

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
            try {
                String str = "State";
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i("re", "Fence > Headphones are plugged in.");
//                        pluge.setText("Head");
                        str = "pluged";

                        break;
                    case FenceState.FALSE:
                        Log.i("re", "Fence > Headphones are NOT plugged in.");
//                        pluge.setText("Head");
                        str = "unpluged";


                        break;
                    case FenceState.UNKNOWN:
                        Log.i("re", "Fence > The headphone fence is in an unknown state.");
//                        pluge.setText("Head");
                        str = "dont know";
                        break;
                }

                MainActivity.getInstace().updateTheTextView(str);

            } catch (Exception e) {

            }

        }
    }

}

//public class MyFenceReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        FenceState fenceState = FenceState.extract(intent);
//
//        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFence")) {
//            switch(fenceState.getCurrentState()) {
//                case FenceState.TRUE:
//                    Log.i(TAG, "Headphones are plugged in.");
//                    break;
//                case FenceState.FALSE:
//                    Log.i(TAG, "Headphones are NOT plugged in.");
//                    break;
//                case FenceState.UNKNOWN:
//                    Log.i(TAG, "The headphone fence is in an unknown state.");
//                    break;
//            }
//        }
//    }
//}
