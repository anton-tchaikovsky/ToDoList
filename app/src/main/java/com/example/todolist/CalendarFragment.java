package com.example.todolist;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private static final String DATE = "date";
    private Description description;
    private Description descriptionParcelable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle argument = getArguments(); // получаем сохраненный объект
        if (argument!=null){
            descriptionParcelable = argument.getParcelable(DATE);
            description = Description.getDescriptionArrayList().stream().filter(n -> n.getId() == descriptionParcelable.getId()).findFirst().get();
            DatePicker calendar = view.findViewById(R.id.calendar);
            Calendar date = description.getDate();
            // выставляем исходную дату в DatePicker
            // изменяем дату заметки по нажатию
            calendar.init(date.get(Calendar.YEAR), date.get (Calendar.MONTH), date.get (Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
                Calendar dateNew = Calendar.getInstance();
                dateNew.set(i, i1, i2);
                description.setDate(dateNew);
                update ();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update(){
        ToDoListFragment toDoListFragment = (ToDoListFragment) requireActivity().getSupportFragmentManager().getFragments()
                .stream().filter(fragment -> fragment instanceof ToDoListFragment).findFirst().get();
        toDoListFragment.initRecyclerView();
    }

    public static CalendarFragment newInstance(Description description) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATE, description);
        fragment.setArguments(args);// привязываем к CalendarFragment Bundle с сохраненным объектом Description
        return fragment;
    }
}
