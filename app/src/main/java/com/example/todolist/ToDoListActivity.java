package com.example.todolist;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

public class ToDoListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list_activity_main);
        // создаем ToolBar (пустой, настраиваем кнопки в ToDoListFragment  и DescriptionFragment)
        initToolBar();
        // при первом запуске активизируем фрагмент ToDoListFragment()
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ToDoListFragment.newInstance())
                    .commit();
        }
    }

    // выход из приложения через диалоговое окно
    @Override
    public void finish() {
        alertDialogFinish();
    }

    private boolean isLandscape (){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // диалоговое окно для выхода из приложения
    private void alertDialogFinish(){
        new AlertDialog.Builder(this)
                .setMessage("Выйти из программы?")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Ок", (dialogInterface, i) -> {
                    toastFinish();
                    ToDoListActivity.super.finish();
                })
                .setCancelable(false)
                .show();
    }

    // toast при выходе из приложения
    private void toastFinish(){
        Toast.makeText(ToDoListActivity.this,"Приложение закрыто", Toast.LENGTH_LONG).show();
    }

    // метод создает toolbar
    private void initToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // в портретной ориентации создаем DrawerMenu
        if (!isLandscape())
            initDrawer (toolbar);
    }

    // метод создает DrawerMenu
    private void initDrawer (Toolbar toolbar){
        //создаем кнопку "сэндвич" на toolbar для отображения navigation_view
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //обрабатываем нажатие кнопок в navigation_view
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.about:
                    // информацию о приложении показываем через фрагмент
                    showAboutApp();
                    return true;
                case R.id.exit:
                    finish();
                    return true;
            }

            return false;
        });
    }

    //метод создает фрагмент для отображения информации о приложении
    private void showAboutApp (){
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .add (R.id.fragment_container, new AboutFragment())
                .commit();
    }

}