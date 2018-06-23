package com.shifu.user.project1;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.TreeMap;
import io.realm.Realm;

// NOTE: Возможно ошибка с id базы, когда количество добавленных id превысит макс. int значение! (счи

public class RecyclerViewFragment extends Fragment {

    private Realm realm;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    SwipeController swipeController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(getContext());
        realm = Realm.getDefaultInstance();

        if (realm.where(RealmModel.class).count() == 0) {
            for (String str : getResources().getStringArray(R.array.res_list_animals)) {
                Long number = new RealmController(this.getContext()).addInfo(str);
            }
        }

        Log.d("Loaded base:", realm.where(RealmModel.class).findAll().toString());
        Log.d("Max ID", realm.where(RealmModel.class).max("ID").toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CustomAdapter(realm.where(RealmModel.class).findAll().sort("ID"));

        mRecyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(getActivity(), new SwipeControllerActions() {
            @Override
            public void onDelete(final int position) {
                Log.d("Position to Delete: ", Integer.toString(position));
                Log.d("ID to Delete: ",Long.toString(mAdapter.ItemID(position)));
                new RealmController(getContext()).removeItemById(mAdapter.ItemID(position));
                mAdapter.notifyDataSetChanged();
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

    public void addItem(String str) {
        Long position = new RealmController(this.getContext()).addInfo(str);
        Log.d("DB", realm.where(RealmModel.class).findAll().toString());
        Log.d("Number DB:", Long.toString(realm.where(RealmModel.class).count()));
        Log.d("Position:",Long.toString(position));
        // DANGEROUS OPERATION, IF DB TOO MUCH!
        int number = (int)(long) realm.where(RealmModel.class).count();
        Log.d("Number tmap:", Long.toString(realm.where(RealmModel.class).count()));
        mAdapter.notifyItemInserted(number);
    }
}
