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

import io.realm.Realm;
import io.realm.RealmConfiguration;

// NOTE: Возможно ошибка с id базы, когда количество добавленных id превысит макс. int значение! (счи

public class RecyclerViewFragment extends Fragment {

    private Realm realm;
    private RealmConfiguration config;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    SwipeController swipeController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getContext());

//      Для возможности миграции. На будущее. Пока предыдущая схема удаляется
//        RealmConfiguration config = new RealmConfiguration().Builder()
//                .chemaVersion(2) // Текущая версия схемы (задание 4, добавлены 2 поля)
//                .migration(new MyMigration())
//                .build();

        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        if (realm.where(RealmModel.class).count() == 0) {
            for (String title : getResources().getStringArray(R.array.res_list_animals)) {
                new RealmController(this.getContext(), config).addInfo(title, null, null);
            }
        }

//        Log.d("Loaded base:", realm.where(RealmModel.class).findAll().toString());
//        Log.d("Max ID", realm.where(RealmModel.class).max("ID").toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CustomAdapter(realm.where(RealmModel.class).findAll().sort("id"));

        mRecyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(getActivity(), new SwipeControllerActions() {
            @Override
            public void onDelete(final int position) {
                new RealmController(getContext(), config).removeItemById(mAdapter.ItemID(position));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onEdit(final int position) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                TextFragment updateFragment = new TextFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                RealmModel updateRow = realm.where(RealmModel.class).equalTo("id", mAdapter.ItemID(position)).findFirst();
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
        Long position = new RealmController(this.getContext(), config).addInfo(title, content, link);

        // DANGEROUS OPERATION, IF DB TOO MUCH!
        int number = (int)(long) realm.where(RealmModel.class).count();
        Log.d("Number tmap:", Long.toString(realm.where(RealmModel.class).count()));
        mAdapter.notifyItemInserted(number);
    }

    public void updateItem(int position, String title, String content, String link ) {
        Boolean updated = new RealmController(this.getContext(), config).updateInfo(mAdapter.ItemID(position), title, content, link);
        mAdapter.notifyDataSetChanged();
    }
}
