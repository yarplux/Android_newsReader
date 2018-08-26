package com.shifu.user.mynewsfeed;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class DataService extends IntentService {

    public static final String ACTION="DataResponseBroadcastReceiver";
    private final static String TAG = "DataService";
    private static String apiKey=null;
    public static Disposable disposable;

    public DataService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (apiKey == null) apiKey = getApplicationContext().getResources().getString(R.string.api_key);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG,"Service running. exist:"+ActivityMain.exist);

        if (disposable != null && !disposable.isDisposed()) disposable.dispose();

        if (ActivityMain.exist == null || !ActivityMain.exist) {
            disposable = FragmentRV.getArticles(getApplicationContext(), true, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(news -> {
                        Log.d("Notification","New articles: "+news);
                        Notification(news);
                    });
        } else {
            disposable = FragmentRV.getArticles(getApplicationContext(), false, null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    private void Notification(Integer news){
        if (news > 0) {
            if (disposable != null && !disposable.isDisposed()) disposable.dispose();

            Log.i(TAG, "Create Notification");

            String msg = "Новые статьи: "+news;

            Intent intentIn = new Intent(ACTION);
            intentIn.putExtra("resultCode", Activity.RESULT_OK);
            intentIn.putExtra("toastMessage", msg);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "1")
                            .setSmallIcon(R.drawable.icons8_news_24)
                            .setContentTitle("Свежие новости")
                            .setContentText(msg);

            Intent resultIntent = new Intent(getApplicationContext(), ActivityMain.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(ActivityMain.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int mId = 0;
            nm.notify(mId, mBuilder.build());
            sendBroadcast(intentIn);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
