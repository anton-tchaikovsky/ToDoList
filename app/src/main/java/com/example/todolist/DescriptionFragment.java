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

import java.util.ArrayList;


public class DescriptionFragment extends Fragment {

    private static final String DESCRIPTION = "description";
    private Description description;
    private View viewDescriptionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // при повороте экрана закрываем DescriptionFragment, который был открыт в портретной ориентации
        if (savedInstanceState != null)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // в портретной ориентации скрываем кнопку "добавить" в toolbar и добавлем меню remove_menu
        if (!isLandscape()){
           MenuItem itemMenuExit = menu.findItem(R.id.add);
           if (itemMenuExit != null) {
               itemMenuExit.setVisible(false);
           }
           inflater.inflate(R.menu.remove_menu, menu);
       }

    }

    // настраиваем кнопку "удалить" в toolbar
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.remove) {
            alertDialogRemove(description);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //метод создает диалоговое окно для удаления заметки
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void alertDialogRemove(Description description) {

        new AlertDialog.Builder(getContext())
                .setTitle("Действительно удалить заметку?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    //запускаем snackbar c уведомлением об удалении заметки
                    snackBarRemove(viewDescriptionFragment, description.getName());
                    //запускаем метод для удаления заметки из ArrayList и изменений в recycleView
                    updateRemove();
                    //закрываем текущий DialogFragment
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
        ArrayList<Description> sharedDescriptionArrayList = ToDoListFragment.getSharedDescriptionArrayList();
        Bundle arguments = getArguments();

        if (arguments != null){
            Description descriptionParcelable = arguments.getParcelable(DESCRIPTION);

            // если заметки не созданы (через bundle передан currentDescription = null) надуваем пустой макет
            if(descriptionParcelable == null){
                viewDescriptionFragment = inflater.inflate(R.layout.fragment_description_empty, container, false);
            }
            // иначе надуваем макет текущей заметки (осуществляем ее поиск в arrayList через метод equals())
            else{
                for (int i = 0; i< sharedDescriptionArrayList.size(); i++){
                    if (descriptionParcelable.equals(sharedDescriptionArrayList.get(i))){
                        description = sharedDescriptionArrayList.get(i);
                        viewDescriptionFragment = inflater.inflate(R.layout.fragment_description, container, false);
                        break;
                    }
                }
            }
        }
        return viewDescriptionFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // находим соответствующие текстовые поля и кнопки (если они есть, т.е. если макет не пустой)
        // и обрабатываем их изменения и нажатия
            if (view.findViewById(R.id.description)!=null){
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
                       // если название заметки не введено или введены пробелы, то в заметке сохраняем ""
                       if (textViewDescriptionName.getText().toString().equals(""))
                           description.setName("");
                       else{
                           for (int j = 0; j<textViewDescriptionName.getText().toString().length(); j++)
                               if (textViewDescriptionName.getText().toString().charAt(j)!=' ')
                                   description.setName(textViewDescriptionName.getText().toString());
                               else if (j==textViewDescriptionName.getText().toString().length()-1)
                                   description.setName("");
                       }
                       //запускаем метод для изменений в recycleView
                       updateChange();
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
                       // если описание заметки не введено или введены пробелы, то в заметке сохраняем ""
                       if (textViewDescription.getText().toString().equals(""))
                           description.setDescription("");
                       else{
                           for (int j = 0; j<textViewDescription.getText().toString().length(); j++)
                               if (textViewDescription.getText().toString().charAt(j)!=' ')
                                   description.setDescription(textViewDescription.getText().toString());
                               else if (j==textViewDescription.getText().toString().length()-1)
                                   description.setDescription("");
                       }
                       //изменения в recycleView не требуются,т.к. в нем не отображается описание заметок
                   }

                   @Override
                   public void afterTextChanged(Editable editable) {

                   }
               });
           }

        // настраиваем кнопку "назад" для закрытия фрагмента DescriptionFragment через popBackStack() (кнопка есть только в портретной ориентации)
        if (view.findViewById(R.id.back) != null) {
            Button buttonBack = view.findViewById(R.id.back);
            buttonBack.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager()
                    .popBackStack());
        }

        //настраиваем кнопку "календарь"/"скрыть календарь"
        if (view.findViewById(R.id.get_calendar) != null){
            Button buttonCalendar = view.findViewById(R.id.get_calendar);
            buttonCalendar.setOnClickListener(new View.OnClickListener() {
                boolean isGetCalendar = true;

                @Override
                public void onClick(View view) {

                    // обрабатываем нажатие кнопки, если календарь закрыт
                    if (isGetCalendar) {
                        //меняем название кнопки
                        buttonCalendar.setText(R.string.hideCalendar);
                        //создаем CalendarFragment текущей заметки
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_calendar, CalendarFragment.newInstance(description))
                                .addToBackStack("")
                                .commit();
                        isGetCalendar = false;
                    }
                    // обрабатываем нажатие кнопки, если календарь открыт
                    else {
                        //меняем название кнопки
                        buttonCalendar.setText(getString(R.string.calendar));
                        //закрываем CalendarFragment текущей заметки
                        getChildFragmentManager().popBackStack();
                        isGetCalendar = true;
                    }
                }
            });
        }

    }

    //метод находит ToDoListFragment и запускает метод для изменения текущей заметки
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateChange() {
        ToDoListFragment toDoListFragment = (ToDoListFragment) requireActivity().getSupportFragmentManager().getFragments()
                .stream().filter(fragment -> fragment instanceof ToDoListFragment).findFirst().get();
        toDoListFragment.itemChanged();
    }

    //метод находит ToDoListFragment и запускает метод для удаления текущей заметки
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateRemove() {
        ToDoListFragment toDoListFragment = (ToDoListFragment) requireActivity().getSupportFragmentManager().getFragments()
                .stream().filter(fragment -> fragment instanceof ToDoListFragment).findFirst().get();
        toDoListFragment.itemRemoved();
    }

    public static DescriptionFragment newInstance(Description description) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(DESCRIPTION, description);
        fragment.setArguments(args);// привязываем к DescriptionFragment Bundle с текущей заметкой (переданным из ToDoListFragment)
        return fragment;
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // snackbar для удаления заметки
    private void snackBarRemove(View view, String name) {
        Snackbar.make(view, name + " удалена", Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();

        // При повороте удаляем текущий DescriptionFragment, чтобы одновременно не было нескольких DescriptionFragment:
        // пересозданных при повороте и созданного через метод showDescriptionLand(Description description) в ToDoListFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }
}