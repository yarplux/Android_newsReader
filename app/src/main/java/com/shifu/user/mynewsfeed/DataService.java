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

import com.shifu.user.mynewsfeed.json.JsonArticle;
import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.State;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class DataService extends IntentService {

    public static final String ACTION="DataResponseBroadcastReceiver";
    private final static String TAG = "DataService";

    private static String apiKey=null;
    private static String category = null;

    private Disposable disposable;

    private Realm realm;

    private static ApiInterface api = ApiClient.getInstance().getApi();

    public DataService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (apiKey == null) apiKey = getApplicationContext().getResources().getString(R.string.api_key);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        State item = realm.where(State.class).findFirst();
        if (item != null && item.getCategory() != null) {
            category = item.getCategory();
        }

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (disposable != null && !disposable.isDisposed()) disposable.dispose();

        disposable = getArticles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    Log.d("Notification","New articles: "+news);
                    Notification(news);
                });
    }

    public Flowable<Integer> getArticles(){
        Map<String, String> options = new HashMap<>();
        options.put("country", "ru");
        options.put("category", category);

        Log.d("REST", "Request category: "+category);
        return api.loadNews(options, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(t-> {
                    Log.d("REST", "Failure: "+t.toString());
                    t.printStackTrace();
                })
                .filter(response -> {
                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().getStatus() != null
                            && response.body().getStatus().equals("ok")
                            && response.body().getArticles() != null) {
                        return true;
                    } else {
                        Log.e("REST error: ", (response.errorBody() == null)?null:response.errorBody().toString());
                        return false;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(response -> {
                    Integer newArticles = 0;
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Article.setLastID(realm.where(Article.class).count());
                    int i=1;
                    for (JsonArticle obj : response.body().getArticles()) {
                        RealmResults<Article> objsIn = realm.where(Article.class).equalTo("url", obj.getUrl()).findAll();
                        Log.d("Article "+(i++), /* obj.toString() + */" \nexist in base? " +Boolean.toString(objsIn!=null)+"\npublishedAt: "+obj.getPublishedAt());
                        if (objsIn.size() == 0 && obj.getPublishedAt() != null) {
                            newArticles++;
                            Article item = new Article(obj,category);
                            realm.copyToRealm(item);
                            Log.d("New Article ("+item.getUid()+")", " \n"+item.getCategory()+" "+item.getTitle()+"\npublished At: "+item.getPublishedAt());
                        } else if (objsIn.size() > 0) {
                            Boolean flag = true;
                            for (Article objIn : objsIn) {
                                if (objIn.getCategory() != null && objIn.getCategory().equals(category)) {
                                    flag = false;
                                    break;
                                } else if (objIn.getCategory() == null && category == null) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                newArticles++;
                                Log.d("Article ("+(Article.getLastID())+")", "New category: "+category+" Title: "+obj.getTitle());
                                realm.copyToRealm(new Article(obj, category));
                            }
                        }
                    }
                    realm.commitTransaction();
                    return newArticles;
                });
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
        realm.close();
        super.onDestroy();
    }
}
