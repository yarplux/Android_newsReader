package com.shifu.user.project1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements RealmAddFragment.PassMainMenu {

    private Menu main_menu;
    private RealmRVFragment realmRVFragment;
    private RealmCustomAdapter mAdapter;
    private RealmConfiguration config;
    private Realm realm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.context = this;

        Realm.init(this);

        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        // Delete all base before update
        new RealmController(this, config).Clear();

        mAdapter =  new RealmCustomAdapter(realm.where(RealmModel.class).findAll().sort("id"));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://countryapi.gear.host")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CountriesAPI countriesAPI = retrofit.create(CountriesAPI.class);
        countriesAPI.loadRegion("Asia").enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {
                if (response.isSuccessful()) {
                    //Log.d("Get countries: ", Integer.toString(response.body().getTotalCount()));
                    new RealmController(context, config).addInfo(response.body());
                    mAdapter.notifyDataSetChanged();
                    setContentView(R.layout.activity_main);
                    Toolbar myToolbar = findViewById(R.id.my_toolbar);
                    setSupportActionBar(myToolbar);

                    realmRVFragment = new RealmRVFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, realmRVFragment, "START")
                            .commit();
                } else {
                    Log.e("REST error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public Realm getRealmDB() {return realm; }
    public RealmConfiguration getConfig() {return config;}
    public RealmCustomAdapter getRealmCustomAdapter() {return mAdapter;}

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
            case R.id.menu_add:
                addItem();
                return true;

            case R.id.menu_list:
                this.getSupportFragmentManager().popBackStackImmediate("START", 0);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, realmRVFragment)
                        .commit();
                return true;

            case R.id.menu_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new FBFragment())
                        .addToBackStack(null)
                        .commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new RealmAddFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Menu getMenu() {
        return main_menu;
    }

}
