package com.example.todolist;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Optional;

public class DescriptionFragment extends Fragment {

    private static final String DESCRIPTION = "description";
    private Description description;
    private Description descriptionParcelable;
    private View viewDescriptionFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem itemMenuExit = menu.findItem(R.id.exit_add);
        if (itemMenuExit != null) {
            itemMenuExit.setVisible(false);
        }
        MenuItem itemMenuAbout = menu.findItem(R.id.about);
        if (itemMenuAbout != null) {
            itemMenuAbout.setVisible(false);
        }
        inflater.inflate(R.menu.remove_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.remove) {
            alertDialogRemove(description);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void alertDialogRemove(Description description) {

        new AlertDialog.Builder(getContext())
                .setTitle("Действительно удалить заметку?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    Description.getDescriptionArrayList().remove(description);
                    snackBarRemove(viewDescriptionFragment, description.getName());
                    update();
                    if (!isLandscape())
                        requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Отмена", null)
                .show();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null)
            setHasOptionsMenu(true);
        viewDescriptionFragment = inflater.inflate(R.layout.fragment_description, container, false);
        return viewDescriptionFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            descriptionParcelable = arguments.getParcelable(DESCRIPTION);

            if (descriptionParcelable != null) {
                Optional<Description> selectedDescription = Description.getDescriptionArrayList().stream().filter(n -> n.getId() == descriptionParcelable.getId()).findFirst();
                description = selectedDescription.orElseGet(() -> Description.getDescriptionArrayList().get(0));
            }

            TextView textViewDescription = view.findViewById(R.id.description);
            TextView textViewDescriptionName = view.findViewById(R.id.description_name);
            textViewDescription.setText(description.getDescription());
            textViewDescriptionName.setText(description.getName());
            if (isLandscape()) {
                textViewDescription.setTextSize(10);
                textViewDescriptionName.setTextSize(15);
            } else {
                textViewDescription.setTextSize(15);
                textViewDescriptionName.setTextSize(20);
            }
            textViewDescriptionName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    description.setName(textViewDescriptionName.getText().toString());
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

        if (view.findViewById(R.id.back) != null) {
            Button buttonBack = view.findViewById(R.id.back);
            buttonBack.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager()
                    .popBackStack());
        }

        Button buttonCalendar = view.findViewById(R.id.get_calendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            boolean isGetCalendar = true;

            @Override
            public void onClick(View view) {

                if (isGetCalendar) {
                    buttonCalendar.setText(R.string.hideCalendar);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_calendar, CalendarFragment.newInstance(description))
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
    private void update() {
        ToDoListFragment toDoListFragment = (ToDoListFragment) requireActivity().getSupportFragmentManager().getFragments()
                .stream().filter(fragment -> fragment instanceof ToDoListFragment).findFirst().get();
        toDoListFragment.initList();
    }

    public static DescriptionFragment newInstance(Description description) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(DESCRIPTION, description);
        fragment.setArguments(args);// привязываем к DescriptionFragment Bundle с сохраненным индексом (переданным из ToDoListFragment)
        return fragment;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void snackBarRemove(View view, String name) {
        Snackbar.make(view, name + " удалена", Snackbar.LENGTH_LONG)
                .show();
    }

}