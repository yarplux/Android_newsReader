package com.shifu.user.mynewsfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DataBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent= new Intent(context,DataService.class);
        context.startService(serviceIntent);
    }
}
