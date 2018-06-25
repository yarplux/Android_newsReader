package com.shifu.user.project1;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class TextFragment extends Fragment {

    PassMainMenu activity;
    private Integer position;

    public interface PassMainMenu {
        Menu getMenu();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (PassMainMenu)getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_text, container, false);

        // Временно убираем пункт добавить, чтобы избежать необходимости обратных переходов по цепочке
        // или редактирования стэка операций с фрагментами
        activity.getMenu().findItem(R.id.menu_add).setVisible(false);

        final Button savebutton = (Button) v.findViewById(R.id.button_save);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.position = bundle.getInt("position");
        }


        final EditText title = (EditText) v.findViewById(R.id.add_title);
        final EditText content = (EditText) v.findViewById(R.id.add_content);
        final EditText link = (EditText) v.findViewById(R.id.add_link);

        if (position != null) {
            title.setText(bundle.getString("title"));
            content.setText(bundle.getString("content"));
            link.setText(bundle.getString("link"));
        }

        final FragmentManager fragmentManager = getFragmentManager();


        savebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (title.getText().length() > 0) {

                    RecyclerViewFragment fragment_list = (RecyclerViewFragment) fragmentManager.findFragmentByTag("START");
                    if (position == null) {
                        fragment_list.addItem(title.getText().toString(), content.getText().toString(), link.getText().toString());
                    } else {
                        fragment_list.updateItem(position,title.getText().toString(), content.getText().toString(), link.getText().toString());
                    }
                    fragmentManager.popBackStackImmediate();
                }
                else {
                    fragmentManager.popBackStackImmediate();
                }
            }
        });

        return v;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//    }

    @Override
    public void onStop() {
        super.onStop();
        activity.getMenu().findItem(R.id.menu_add).setVisible(true);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}
