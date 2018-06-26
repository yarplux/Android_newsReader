package com.shifu.user.project1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements TextFragment.PassMainMenu {

    private Menu main_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new RecyclerViewFragment(), "START")
                .commit();

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

        switch (item.getItemId()) {
            case R.id.menu_add:
                addItem();
                return true;

            case R.id.menu_list:
                this.getSupportFragmentManager().popBackStackImmediate("START", 0);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new RecyclerViewFragment())
                        .commit();
                return true;

            case R.id.menu_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new FBFragment())
                        .addToBackStack(null)
                        .commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new TextFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Menu getMenu() {
        return main_menu;
    }



}
