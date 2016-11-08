package com.example.janitha.myapplication.broadcast_receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.janitha.myapplication.MainActivity;
import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Siri on 11/7/2016.
 */

public class HeadphoneFenceBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        Log.d("HeadphoneFence_BR", "Fence Receiver Received "+fenceState.getCurrentState()+" "+fenceState.getFenceKey());

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
            String str = "State";

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i("HeadphoneFence_BR", "Headphones are Plugged in.");
                    str = "plugged";
                    break;
                case FenceState.FALSE:
                    Log.i("HeadphoneFence_BR", "Headphones are NOT Plugged in.");
                    str = "unplugged";
                    break;
                case FenceState.UNKNOWN:
                    Log.i("HeadphoneFence_BR", "The headphone fence is in an unknown state.");
                    str = "Dont know";
                    break;
            }
            try {
                MainActivity.getInstace().updateTheTextView(str);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("HeadphoneFence_BR", "Main Activity = Null !");
            }
        }
    }
}
