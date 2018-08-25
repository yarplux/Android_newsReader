package com.shifu.user.mynewsfeed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shifu.user.mynewsfeed.json.JsonArticles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.Window.FEATURE_NO_TITLE;


public class ActivityMain extends AppCompatActivity {

    private Menu main_menu;
    private FragmentRV fragmentRV;
    private RealmRVAdapter ra;
    private RealmController rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash_layout);

        rc = new RealmController(this);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewsAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.addConverterFactory(ScalarsConverterFactory.create())
                .build();

        NewsAPI newsAPI = retrofit.create(NewsAPI.class);

        Map<String, String> options = new HashMap <>();

        options.put("language", "ru");

        newsAPI.loadNews(options, getResources().getString(R.string.api_key)).enqueue(new Callback<JsonArticles>() {
            @Override
            public void onResponse(@NonNull Call<JsonArticles> call, @NonNull Response<JsonArticles> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getStatus() != null
                        && response.body().getStatus().equals("ok")
                        && response.body().getArticles() != null) {

                    setContentView(R.layout.main_layout);

                    Toolbar myToolbar = findViewById(R.id.my_toolbar);
                    setSupportActionBar(myToolbar);

                    rc.loadArticles(response.body().getArticles());

                    verifyStoragePermissionsAndRequest();

                } else {
                    Log.e("REST error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArticles> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.main_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_list:
                this.getSupportFragmentManager().popBackStackImmediate("START", 0);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragmentRV)
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void verifyStoragePermissionsAndRequest() {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };
        int permission = ActivityCompat.checkSelfPermission(this, permissions[0]);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            loadRV();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode ==0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadRV();
        }
    }

    private void loadRV(){
        ra =  new RealmRVAdapter(rc.getArticles(), getResources());
        fragmentRV = new FragmentRV();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragmentRV, "START")
                .commit();

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && f instanceof FragmentNews) {
                ((FragmentNews) f).onBackPressed();
                break;
            } else {
                super.onBackPressed();
            }
        }
    }
}
