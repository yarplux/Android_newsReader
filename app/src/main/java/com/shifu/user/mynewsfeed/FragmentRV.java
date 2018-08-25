package com.shifu.user.mynewsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentRV extends Fragment {

    private RecyclerView rv;
    private RealmRVAdapter ra = RealmRVAdapter.getInstance();
    private RealmController rc = RealmController.getInstance();
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_layout, container, false);

        rv = rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        rv.setItemViewCacheSize(20);

        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
        rv.setAdapter(ra);
    }

}
