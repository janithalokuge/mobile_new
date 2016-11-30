package com.example.janitha.myapplication.broadcast_receivers;

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

public class InHomeLocationFenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        Log.d("InHomeLocFence_BR", "Fence Receiver Received "+fenceState.getCurrentState()+" "+fenceState.getFenceKey());


        if (TextUtils.equals(fenceState.getFenceKey(), "inHomeLocationFenceKey")) {
            String str;

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    str = "In HomeLoc";
                    Log.i("InHomeLocFence_BR", "Fence Enter == TRUE");
                    break;
                case FenceState.FALSE:
                    str = "Not in HomeLoc";
                    Log.i("InHomeLocFence_BR", "Fence Enter == FLASE");
                    break;

                default:
                    str = "Couldn't Detect!!!";
                    Log.i("InHomeLocFence_BR", "Fence Enter == UNKNOWN");
                    break;
            }
            try {
                MainActivity.getInstace().updateTheTextViewhome(str);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("InHomeLocFence_BR", "Main Activity = Null !");
            }
        }
    }
}
