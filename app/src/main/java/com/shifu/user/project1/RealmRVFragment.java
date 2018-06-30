package com.shifu.user.project1;

import android.content.Context;
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

    private MainActivity activity;
    private Realm realm;
    private RealmConfiguration config;
    protected RecyclerView mRecyclerView;
    protected RealmCustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    SwipeController swipeController = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = activity.getRealmDB();
        config = activity.getConfig();
        mAdapter = activity.getRealmCustomAdapter();
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
