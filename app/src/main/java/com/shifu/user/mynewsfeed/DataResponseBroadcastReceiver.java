package com.shifu.user.mynewsfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DataResponseBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //get the broadcast message
        int resultCode=intent.getIntExtra("resultCode",RESULT_CANCELED);
        if (resultCode==RESULT_OK){
            String message=intent.getStringExtra("toastMessage");
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }
    }
}