package com.shifu.user.mynewsfeed;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.shifu.user.mynewsfeed.AppGlobals.*;

public class ActivityMain extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private SwitchCompat autoupdate;

    AlarmManager am;
    private Realm realm;
    private RealmRVAdapter ra;

    private Boolean isExit = false;

    Disposable disposable;


    public static Map<String, String> categories = new HashMap <>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        am =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        try {
            if (am != null) am.cancel(scheduleAlarm());
        } catch (Exception e) {
            Log.e("Main", "AlarmManager update was not canceled. " + e.toString());
        }


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);


        Realm.init(this);
        realm = Realm.getDefaultInstance();
        realm.setAutoRefresh(true);

        Boolean isAutoupdate;
        String text = "Новости без категории";

        for (String str : getResources().getStringArray(R.array.categories)) {
            categories.put(str.substring(0, str.indexOf('|')), str.substring(str.indexOf('|')+1));
        }

        RealmResults<Article> articles;
        if (realm.where(State.class).count() == 0) {
            realm.executeTransaction(trRealm -> trRealm.copyToRealm(new State()));
            isAutoupdate = false;

            articles = realm.where(Article.class)
                    .sort("uid", Sort.DESCENDING)
                    .findAll();
        } else {
            State state = realm.where(State.class).findFirst();
            isAutoupdate = state.getAutoupdate();
            if (isAutoupdate == null) isAutoupdate = false;
            if (state.getCategory() != null) text = categories.get(state.getCategory());

            articles = realm.where(Article.class)
                    .equalTo("category", state.getCategory())
                    .sort("uid", Sort.DESCENDING)
                    .findAll();
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        ra =  new RealmRVAdapter(articles, getResources());
        FragmentRV fragmentRV = new FragmentRV();

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);

        View hView = nvDrawer.getHeaderView(0);
        TextView category = hView.findViewById(R.id.category);
        category.setText(getResources().getString(R.string.category, text));

        LinearLayout mView = (LinearLayout) nvDrawer.getMenu().findItem(R.id.update).getActionView();
        autoupdate = mView.findViewById(R.id.drawer_switch);
        autoupdate.setChecked(isAutoupdate);

        disposable = Flowable.interval(15, TimeUnit.SECONDS)
                .filter(i -> autoupdate.isChecked())
                .concatMap(i -> FragmentRV.getArticles(getApplicationContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> FragmentRV.verifyStoragePermissionsAndRequest(this));

        autoupdate.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            realm.executeTransaction(trRealm -> trRealm.where(State.class).findFirst().setAutoupdate(isChecked));
            if (!isChecked) {
                if (disposable != null && !disposable.isDisposed()) disposable.dispose();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(stylish(R.drawable.icons8_menu_24, getResources()));
        toolbar.setNavigationOnClickListener(view -> {
            for (Fragment f : getSupportFragmentManager().getFragments()) {
                if (f != null && f instanceof FragmentNews) {
                    ((FragmentNews) f).onBackPressed();
                    return;
                }
            }
            mDrawer.openDrawer(nvDrawer);
        });

        nvDrawer.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.clear:
                    realm.executeTransactionAsync(trRealm -> {
                        trRealm.where(Article.class).findAll().deleteAllFromRealm();
                    });
                    mDrawer.closeDrawer(nvDrawer);
                    break;
            }
            return true;

        });

        ImageButton filter = findViewById(R.id.filter);
        filter.setImageDrawable(stylish(R.drawable.icons8_filter_24, getResources()));

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragmentRV, "START")
                .commit();
    }

    private PendingIntent scheduleAlarm() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), DataBroadcastReceiver.class)
                        .putExtra("time", 3)
                        .putExtra("task", "Выполнить!"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent);

        return pendingIntent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode ==0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            finishLoading();
        }
    }

    public void finishLoading() {
        ra.updateData();
        findViewById(R.id.imgLogo).setVisibility(View.GONE);
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && f instanceof FragmentNews) {
                ((FragmentNews) f).onBackPressed();
                break;
            } else {
                if (isExit) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(this, "Для выхода нажмите ещё раз", Toast.LENGTH_SHORT).show();
                    isExit = true;
                    Thread t = new Thread (() -> {
                        try {
                            Thread.sleep(3000);
                            isExit = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        if (realm != null && !realm.isClosed()) realm.close();
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();

        if (autoupdate.isChecked()) {
            DataResponseBroadcastReceiver broadcastReceiver= new DataResponseBroadcastReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DataService.ACTION);
            registerReceiver(broadcastReceiver, intentFilter);

            if (am == null) {
                Toast.makeText(this, "Невозможно запустить автообновление новостей в фоне", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("Main", "onDestroyUpdateInit");
                scheduleAlarm();
            }
        }
        super.onStop();
    }
}
