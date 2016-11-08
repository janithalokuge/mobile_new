package com.example.janitha.myapplication.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.janitha.myapplication.MainActivity;
import com.example.janitha.myapplication.WorkLocationWeatherNotification;
import com.example.janitha.myapplication.services.FenceEnterService;
import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Siri on 11/7/2016.
 */

// Handle the callback on the Intent.
public class EnterFenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FenceState fenceState = FenceState.extract(intent);

        Log.d("EnterFence_BR", "Fence Receiver Received "+fenceState.getCurrentState()+" "+fenceState.getFenceKey());


        if (TextUtils.equals(fenceState.getFenceKey(), "enteringFenceKey")) {
            String str;

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    //When TRUE
                    str = "Entered HomeLoc";
                    WorkLocationWeatherNotification.notify(context.getApplicationContext(),"You entered home location", 4);
                    Log.i("EnterFence_BR", "Fence Enter == TRUE");
                    break;
                case FenceState.FALSE:
                    //When False
                    str = "Left HomeLoc";
                    Log.i("EnterFence_BR", "Fence Enter == FLASE");
                    break;

                default:
                    str = "Couldn't Detect!!!";
                    Log.i("EnterFence_BR", "Fence Enter == UNKNOWN");
                    break;
            }

            try {
                MainActivity.getInstace().updateTheTextViewenter(str, context);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("EnterFence_BR", "Main Activity = Null !");
            }
        }
    }

}
