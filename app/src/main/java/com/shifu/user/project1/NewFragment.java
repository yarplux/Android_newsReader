package com.shifu.user.project1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class NewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new, container, false);

        final ImageButton button = v.findViewById(R.id.backButton);
        button.setBackgroundResource(R.drawable.back);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        return v;

    }

}