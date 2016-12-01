package com.example.janitha.myapplication.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.janitha.myapplication.MainActivity;
import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Siri on 1/12/2016.
 */

public class InWorkLocationFenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        Log.d("InWorkLocFence_BR", "Fence Receiver Received "+fenceState.getCurrentState()+" "+fenceState.getFenceKey());


        if (TextUtils.equals(fenceState.getFenceKey(), "inWorkLocationFenceKey")) {
            String str;

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    str = "In HomeLoc";
                    Log.i("InWorkLocFence_BR", "Fence Enter == TRUE");
                    break;
                case FenceState.FALSE:
                    str = "Not in HomeLoc";
                    Log.i("InWorkLocFence_BR", "Fence Enter == FLASE");
                    break;

                default:
                    str = "Couldn't Detect!!!";
                    Log.i("InWorkLocFence_BR", "Fence Enter == UNKNOWN");
                    break;
            }
            try {
//                MainActivity.getInstace().updateTheTextViewhome(str);
                //TODO

            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("InWorkLocFence_BR", "Main Activity = Null !");
            }
        }
    }
}
