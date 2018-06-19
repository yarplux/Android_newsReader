package com.shifu.user.project1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_text, container, false);

        final Button savebutton = (Button) v.findViewById(R.id.button_save);

        final EditText editText = (EditText) v.findViewById(R.id.text_add);

        final FragmentManager fragmentManager = getFragmentManager();

        savebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {

                    RecyclerViewFragment fragment_list = (RecyclerViewFragment) fragmentManager.findFragmentByTag("START");
                    fragment_list.addItem(editText.getText().toString());
                    fragmentManager.popBackStackImmediate();
                }
                else {
                    fragmentManager.popBackStackImmediate();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
