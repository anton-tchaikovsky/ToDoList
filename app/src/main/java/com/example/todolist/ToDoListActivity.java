package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class ToDoListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list_activity_main);
        initToolBar();
        // при первом запуске активизируем фрагмент ToDoListFragment()
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ToDoListFragment())
                    .commit();
        }
    }

    private boolean isLandscape (){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
   /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_ARRAY_LIST, descriptionsArrayList);// сохраняем List заметок
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        descriptionsArrayList = savedInstanceState.getParcelable(KEY_ARRAY_LIST);
        super.onRestoreInstanceState(savedInstanceState);
    }*/

    private void initToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!isLandscape())
            initDrawer (toolbar);
    }

    private void initDrawer (Toolbar toolbar){
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.about:
                        showAboutApp();
                        return true;
                    case R.id.exit_add:
                        finish();
                        return true;
                }

                return false;
            }
        });


    }

    private void showAboutApp (){
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .add (R.id.fragment_container, new AboutFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.about:
                showAboutApp();
                break;
            case R.id.exit_add:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}