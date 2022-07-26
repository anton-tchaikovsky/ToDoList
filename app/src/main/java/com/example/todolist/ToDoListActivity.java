package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ToDoListActivity extends AppCompatActivity {

    private static final String KEY_ARRAY_LIST = "keyArrayList";
    private DescriptionsArrayList descriptionsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list_activity_main);
        descriptionsArrayList = DescriptionsArrayList.getInstance(this);// при первом запуске приложения создаем List заметок с исходной информацией из ресурвов array_to_do_list

        // при первом запуске активизируем фрагмент ToDoListFragment()
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ToDoListFragment())
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_ARRAY_LIST, descriptionsArrayList);// сохраняем List заметок
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        descriptionsArrayList = savedInstanceState.getParcelable(KEY_ARRAY_LIST);// получаем сохраненный List заметок
        super.onRestoreInstanceState(savedInstanceState);
    }
}