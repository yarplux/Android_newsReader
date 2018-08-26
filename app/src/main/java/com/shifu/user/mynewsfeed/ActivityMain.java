package com.shifu.user.mynewsfeed;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityMain extends AppCompatActivity {

    public static Boolean exist;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private SwitchCompat autoupdate;

    AlarmManager am;

    private RealmRVAdapter ra;
    private RealmController rc;
    private Boolean isExit = false;

    DataResponseBroadcastReceiver broadcastReceiver;

    public static Map<String, String> categories = new HashMap <>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();


        rc = new RealmController(this);
        rc.stateInit();

        ra =  new RealmRVAdapter(rc.getArticles(), getResources());


        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);

        for (String str : getResources().getStringArray(R.array.categories)) {
            categories.put(str.substring(0, str.indexOf('|')), str.substring(str.indexOf('|')+1));
        }

        View hView = nvDrawer.getHeaderView(0);
        TextView category = hView.findViewById(R.id.category);
        String text = rc.getCategory();
        if (text == null) {
            text = "Новости без категории";
        } else {
            text = categories.get(text);
        }
        category.setText(getResources().getString(R.string.category, text));

        LinearLayout mView = (LinearLayout) nvDrawer.getMenu().findItem(R.id.update).getActionView();

        autoupdate = mView.findViewById(R.id.drawer_switch);
        autoupdate.setChecked(rc.getAutoupdate());
        if (rc.getAutoupdate()) {
            broadcastReceiver= new DataResponseBroadcastReceiver();
            IntentFilter intentFilter= new IntentFilter();
            intentFilter.addAction(DataService.ACTION);
            registerReceiver(broadcastReceiver,intentFilter);

            scheduleAlarm();
        }

        autoupdate.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            rc.setAutoupdate(isChecked);
            if (isChecked) {
                broadcastReceiver= new DataResponseBroadcastReceiver();
                IntentFilter intentFilter= new IntentFilter();
                intentFilter.addAction(DataService.ACTION);
                registerReceiver(broadcastReceiver,intentFilter);

                scheduleAlarm();
            } else {
                try {
                    if (am != null) am.cancel(scheduleAlarm());
                    Toast.makeText(this, "Автообновление новостей отключено", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Main", "AlarmManager update was not canceled. " + e.toString());
                }
            }
        });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton menu = findViewById(R.id.menu);
        menu.setImageDrawable(stylish(R.drawable.icons8_menu_24));
        menu.setOnClickListener(view -> {
            for(Fragment f : getSupportFragmentManager().getFragments()) {
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
                    rc.clear();
                    break;
            }
            return true;

        });

        ImageButton filter = findViewById(R.id.filter);
        filter.setImageDrawable(stylish(R.drawable.icons8_filter_24));

        FragmentRV fragmentRV = new FragmentRV();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragmentRV, "START")
                .commit();



    }

    private PendingIntent scheduleAlarm() {
        long startTime=System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), DataBroadcastReceiver.class)
                        .putExtra("time", 3)
                        .putExtra("task", "Выполнить!"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        am =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,startTime,AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

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

    public Drawable stylish(int resource) {
        Drawable icon = getResources().getDrawable(resource);
        icon.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP));
        return icon;
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
                            Log.d("Run", "work");
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
    protected void onResume() {
        super.onResume();
        exist = true;
        if (autoupdate.isChecked()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DataService.ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exist = false;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exist = null;
        rc.close();
    }
}
