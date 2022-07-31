package com.example.todolist;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private static final String INDEX = "index";
    private static final String DESCRIPTION = "description";
    private Description description;
    private Description descriptionParcelable;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DescriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DescriptionFragment newInstance(String param1, String param2) {
        DescriptionFragment fragment = new DescriptionFragment();
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
        if (savedInstanceState!=null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();// получаем сохраненный индекс
        if (arguments != null){
            descriptionParcelable = arguments.getParcelable(DESCRIPTION);
            DescriptionsArrayList descriptionsArrayList = DescriptionsArrayList.getInstance(requireContext());
            description = descriptionsArrayList.getDescriptionArrayList().stream().filter(n ->n.getId() == descriptionParcelable.getId()).findFirst().get();
            //description = descriptionsArrayList.getDescription(descriptionParcelable.getId()); Т.к. id соответствует индексу объекта в List, можно использовать данный подход
            TextView textViewDescription = view.findViewById(R.id.description);
            TextView textViewDescriptionName = view.findViewById(R.id.description_name);
            textViewDescription.setText(description.getDescription());
            textViewDescriptionName.setText (description.getName());
            if (isLandscape()){
                textViewDescription.setTextSize(10);
                textViewDescriptionName.setTextSize(15);
            } else{
                textViewDescription.setTextSize(15);
                textViewDescriptionName.setTextSize(20);
            }
            textViewDescriptionName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    description.setName(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            textViewDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    description.setDescription(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        if(view.findViewById(R.id.back)!=null){
            Button buttonBack = view.findViewById(R.id.back);
            buttonBack.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager()
                    .popBackStack());
        }

        Button buttonCalendar = view.findViewById(R.id.get_calendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            boolean isGetCalendar = true;
            @Override
            public void onClick(View view) {

                if (isGetCalendar){
                    buttonCalendar.setText(R.string.hideCalendar);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_calendar,CalendarFragment.newInstance(description))
                            .addToBackStack("")
                            .commit();
                    isGetCalendar = false;
                } else {
                    buttonCalendar.setText(getString(R.string.calendar));
                    getChildFragmentManager().popBackStack();
                    isGetCalendar = true;
                }
            }
        });

    }

    public static DescriptionFragment newInstance (Description description){
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(DESCRIPTION, description);
        fragment.setArguments(args);// привязываем к DescriptionFragment Bundle с сохраненным индексом (переданным из ToDoListFragment)
        return fragment;
    }

    private boolean isLandscape (){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}