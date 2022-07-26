package com.example.todolist;

import static com.example.todolist.ToDoListFragment.INDEX;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;


public class PortActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port);

        // При переходе в ландшафтный экран активити закрывается
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            finish();
            return;
        }

        // При переходе из портретного экрана отображаются DescriptionFragment и CalendarFragment, соответствующие последней открытой заметке в портретном экране.
        // При первом запуске приложения (сразу в ландшафтном экране) отображаются DescriptionFragment и CalendarFragment, соответствующие индексу 0.
        if (savedInstanceState==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_description, DescriptionFragment.newInstance(getIntent().getExtras().getInt (INDEX)))
                    .replace(R.id.fragment_container_calendar, CalendarFragment.newInstance(getIntent().getExtras().getInt (INDEX)))
                    .commit();
        }
    }
}