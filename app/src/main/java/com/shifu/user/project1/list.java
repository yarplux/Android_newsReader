package com.shifu.user.project1;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.app.FragmentTransaction;



public class list extends ListFragment {

    NewFragment frag2;
    FragmentTransaction fTrans;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] animals = getResources().getStringArray(R.array.animals);
        ListAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, animals);
        setListAdapter(adapter);

        frag2 = new NewFragment();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, frag2);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }


}
