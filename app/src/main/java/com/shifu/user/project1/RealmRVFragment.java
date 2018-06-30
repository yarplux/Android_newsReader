package com.shifu.user.project1;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


// NOTE: Возможно ошибка с id базы, когда количество добавленных id превысит макс. int значение! (счи
public class RealmRVFragment extends Fragment {

    private Realm realm;
    private RealmConfiguration config;
    protected RecyclerView mRecyclerView;
    protected RealmCustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    SwipeController swipeController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getContext());

        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        // Delete all base before update
        new RealmController(getContext(), config).Clear();

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
                    new RealmController(getContext(), config).addInfo(response.body());
                    mAdapter.notifyDataSetChanged();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(getActivity(), new SwipeControllerActions() {
            @Override
            public void onDelete(final int position) {
                //Log.d("Deleted:", Long.toString(mAdapter.getItem(position).getID()));
                new RealmController(getContext(), config).removeItemById(mAdapter.getItem(position).getID());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onEdit(final int position) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                RealmAddFragment updateFragment = new RealmAddFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                RealmModel updateRow = realm.where(RealmModel.class).equalTo("id", mAdapter.getItem(position).getID()).findFirst();
                bundle.putString("title", updateRow.getTitle());
                bundle.putString("content", updateRow.getContent());
                bundle.putString("link", updateRow.getLink());
                updateFragment.setArguments(bundle);
                transaction.replace(R.id.container, updateFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        return rootView;
    }

    public void addItem(String title, String content, String link ) {
        new RealmController(this.getContext(), config).addInfo(title, content, link);

        // DANGEROUS OPERATION, IF DB TOO MUCH!
        int number = (int)(long) realm.where(RealmModel.class).count();
        mAdapter.notifyItemInserted(number);
    }

    public void updateItem(int position, String title, String content, String link ) {
        new RealmController(this.getContext(), config).updateInfo(mAdapter.getItem(position).getID(), title, content, link);
        mAdapter.notifyDataSetChanged();
    }

}
