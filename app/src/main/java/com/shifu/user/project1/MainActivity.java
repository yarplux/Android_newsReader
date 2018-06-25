package com.shifu.user.project1;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements TextFragment.PassMainMenu {

    public RecyclerViewFragment fragment_start;
    private Menu main_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragment_start = new RecyclerViewFragment();
        transaction.add(R.id.container, fragment_start, "START");
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.main_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add:
                addItem();
                return true;
            case R.id.menu_list:
                FragmentManager fm = this.getSupportFragmentManager();
                Log.d("Menu_Return:",fm.findFragmentByTag("START").toString());

// Альтернатива:
//                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                    fm.popBackStack();
//                }
                fm.popBackStackImmediate("START", fm.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new RecyclerViewFragment());
                transaction.commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new TextFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public Menu getMenu() {
        return main_menu;
    }



}
