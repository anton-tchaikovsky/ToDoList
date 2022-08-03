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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DATE = "date";
    private Description description;
    private Description descriptionParcelable;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle argument = getArguments(); // получаем сохраненный объект
        if (argument!=null){
            descriptionParcelable = argument.getParcelable(DATE);
           // DescriptionsArrayList descriptionsArrayList = DescriptionsArrayList.getInstance(requireContext());
            description = Description.getDescriptionArrayList().stream().filter(n -> n.getId() == descriptionParcelable.getId()).findFirst().get();
            DatePicker calendar = view.findViewById(R.id.calendar);
            Calendar date = description.getDate();
            // выставляем исходную дату в DatePicker
            calendar.init(date.get(Calendar.YEAR), date.get (Calendar.MONTH), date.get (Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                        // изменяем дату заметки по нажатию
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    Calendar dateNew = Calendar.getInstance();
                    dateNew.set(i, i1, i2);
                    description.setDate(dateNew);
                }
            });
        }
    }

    public static CalendarFragment newInstance(Description description) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATE, description);
        fragment.setArguments(args);// привязываем к CalendarFragment Bundle с сохраненным объектом Description
        return fragment;
    }
}
