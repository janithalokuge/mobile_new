package com.example.janitha.myapplication.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.janitha.myapplication.MainActivity;
import com.example.janitha.myapplication.LocationWeatherNotification;
import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Siri on 1/12/2016.
 */

// Handle the callback on the Intent.
public class ExitWorkLocationFenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FenceState fenceState = FenceState.extract(intent);

        Log.d("ExitWorkLocFence_BR", "Fence Receiver Received "+fenceState.getCurrentState()+" "+fenceState.getFenceKey());


        if (TextUtils.equals(fenceState.getFenceKey(), "exitWorkLocationFenceKey")) {
            String str;

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    //When TRUE
                    str = "Left WorkLoc";
                    LocationWeatherNotification.notify(context.getApplicationContext(),"You entered home location", 4);
                    Log.i("ExitWorkLocFence_BR", "Fence Enter == TRUE");
                    break;
                case FenceState.FALSE:
                    //When False
                    str = "In or outside WorkLoc";
                    Log.i("ExitWorkLocFence_BR", "Fence Enter == FLASE");
                    break;

                default:
                    str = "Couldn't Detect!!!";
                    Log.i("ExitWorkLocFence_BR", "Fence Enter == UNKNOWN");
                    break;
            }

            try {
//                MainActivity.getInstace().updateTheTextViewenter(str, context);
                //TODO

            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("ExitWorkLocFence_BR", "Main Activity = Null !");
            }
        }
    }

}
