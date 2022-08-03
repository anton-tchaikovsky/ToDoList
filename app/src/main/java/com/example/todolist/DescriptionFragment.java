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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Optional;


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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem itemMenuExit = menu.findItem(R.id.exit_add);
        if (itemMenuExit!=null){
            itemMenuExit.setVisible(false);
        }
        MenuItem itemMenuAbout = menu.findItem(R.id.about);
        if (itemMenuAbout!=null){
            itemMenuAbout.setVisible(false);
        }
        inflater.inflate(R.menu.remove_menu,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         if (item.getItemId() == R.id.remove) {
             Description.getDescriptionArrayList().remove(description);
             update();
             if (!isLandscape())
                 requireActivity().getSupportFragmentManager().popBackStack();
                return true;

         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState==null)
            setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();// получаем сохраненный индекс
        if (arguments != null){
            descriptionParcelable = arguments.getParcelable(DESCRIPTION);

            if (descriptionParcelable != null){
                Optional<Description> selectedDescription = Description.getDescriptionArrayList().stream().filter(n -> n.getId() == descriptionParcelable.getId()).findFirst();

                /*if (selectedNote.isPresent()){
                    note = selectedNote.get();
                }
                else{
                    note = Note.getNotes().get(0);
                }*/
                description = selectedDescription.orElseGet(() -> Description.getDescriptionArrayList().get(0));
            }
            //description = Description.getDescriptionArrayList().stream().filter(n ->n.getId() == descriptionParcelable.getId()).findFirst().get();

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
                    description.setName( textViewDescriptionName.getText().toString());
                    update();
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

   @RequiresApi(api = Build.VERSION_CODES.N)
   private void update (){
    ToDoListFragment toDoListFragment = (ToDoListFragment) requireActivity().getSupportFragmentManager().getFragments()
            .stream().filter(fragment -> fragment instanceof ToDoListFragment).findFirst().get();
       toDoListFragment.initList();
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