package com.spriv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receiver_make_log", "Service Stops! Oops!!!!");
        context.startService(new Intent(context, ServiceNoDelay.class));

    }
}