package com.example.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DialogFragmentCalendar extends DialogFragment {

    private Calendar date;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       Bundle argument = getArguments();
       if (argument!=null)
            date = (Calendar) argument.getSerializable("DATE");//текущая дата заметки
        //надуваем view с календарем DatePicker
        View calendarView = getLayoutInflater().inflate(R.layout.fragment_calendar, null);
        DatePicker calendar = calendarView.findViewById(R.id.calendar);
        // изменяем дату заметки по нажатию
        calendar.init(date.get(Calendar.YEAR), date.get (Calendar.MONTH), date.get (Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> date.set(i, i1, i2));

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Введите новую дату")
                .setView(calendarView)
                .setPositiveButton("Сохранить", (dialogInterface, i) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("NEW_DATE", date);
                    //записываем результат
                    requireActivity().getSupportFragmentManager().setFragmentResult("KEY_NEW_DATE", bundle);
                    dismiss();
                })
                .setNegativeButton("Отмена", null)
                .create();
    }

    public static DialogFragmentCalendar newInstance(Calendar date) {
        Bundle args = new Bundle();
        args.putSerializable("DATE", date);
        DialogFragmentCalendar fragment = new DialogFragmentCalendar();
        fragment.setArguments(args);
        return fragment;
    }
}
