package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ToDoListFragment extends Fragment {

    private static final String CURRENT_DESCRIPTION = "current_description";
    static private Description currentDescription;
    private RecyclerView recyclerView;
    private static final int DURATION = 1000;
    ListAdapter listAdapter;
    private static ArrayList<Description> sharedDescriptionArrayList;
    private SharedPreferences sharedPreferences = null;
    private static final String KEY = "KEY";

    public static ArrayList<Description> getSharedDescriptionArrayList() {
        return sharedDescriptionArrayList;
    }

    // создаем меню toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    // настраиваем кнопки в меню toolbar
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            addDescription();
            return true;
        }
        if (item.getItemId() == R.id.delete) { // кнопка delete есть только в ландшафтной ориентации main_menu
            alertDialogRemove(recyclerView, currentDescription);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // создаем объект sharedPreferences
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("DATA", Context.MODE_PRIVATE);

        // считываем данные при запуске приложения
        if (savedInstanceState == null) {
            String savedData = sharedPreferences.getString(KEY, null);
            if (savedData == null || savedData.isEmpty()) {
                Toast.makeText(requireActivity(), "Нет сохраненных заметок", Toast.LENGTH_SHORT).show();
                //если нет сохраненных заметок, создаем пустой ArrayList заметок
                sharedDescriptionArrayList = new ArrayList<>();
            } else {
                try {
                    // создаем тип составного класса
                    Type type = new TypeToken<ArrayList<Description>>() {
                    }.getType();
                    // создаем ArrayList с сохраненными заметками
                    sharedDescriptionArrayList = new GsonBuilder().create().fromJson(savedData, type);
                } catch (JsonSyntaxException e) { // обрабатываем исключение на случай ошибки трансформации из строки в ArrayList
                    Toast.makeText(requireActivity(), "Ошибка чтения сохраненных заметок",
                            Toast.LENGTH_SHORT).show();
                    //если не удалось прочитать сохраненные заметки, создаем пустой ArrayList заметок
                    sharedDescriptionArrayList = new ArrayList<>();
                }
            }
            //при запуске приложения определяем текущую заметку
            if (sharedDescriptionArrayList.size() > 0)
                currentDescription = sharedDescriptionArrayList.get(0);
            else
                currentDescription = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // надуваем макет с recyclerView
        View view = inflater.inflate(R.layout.fragment_to_do_list2, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        //создаем recyclerView
        initRecyclerView(recyclerView, sharedDescriptionArrayList);
        //включаем меню toolbar
        setHasOptionsMenu(true);
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView (RecyclerView recyclerView, ArrayList <Description> descriptionArrayList) {
        // создаем адаптер и привязываем его к recyclerView
        listAdapter = new ListAdapter(descriptionArrayList, this, getContext());
        recyclerView.setAdapter(listAdapter);
        // создаем аниматор и привязываем его к recyclerView
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(DURATION);
        animator.setRemoveDuration(DURATION);
        recyclerView.setItemAnimator(animator);
        //в портретной ориентации создаем декоратор для разделения заметок в recyclerView
        if (!isLandscape()) {
            @SuppressLint("UseRequireInsteadOfGet")
            DividerItemDecoration itemDecoration = new
                    DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);

            itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator,
                    null));
            recyclerView.addItemDecoration(itemDecoration);
        }
        // обрабатываем нажатие кнопки "подробнее" на заметке в recyclerView
        listAdapter.setOnItemClickListener((view, position) -> {
            currentDescription = descriptionArrayList.get(position);
            if (isLandscape())
                showDescriptionLand(currentDescription);
            else
                showDescriptionPort(currentDescription);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // в ландшафтной ориентации сразу отображаем фрагмент текущей заметки
        // (или пустой фрагмент fragment_description_empty если заметок нет currentDescription = null)
        if (isLandscape())
            showDescriptionLand(currentDescription);
    }

    // метод изменяет текущую заметку в recyclerView (при удалении из DescriptionFragment или из CalendarFragment)
    // изменение самой заметки реализовано в DescriptionFragment и в CalendarFragment
    public void itemChanged() {
        listAdapter.notifyItemChanged(sharedDescriptionArrayList.indexOf(currentDescription));
    }

    // метод удаляет текущую заметку (при удалении из DescriptionFragment)
    public void itemRemoved() {
        itemRemoved(currentDescription);
    }

    //метод удаляет заметку description
    private void itemRemoved(Description description) {
        listAdapter.notifyItemRemoved(sharedDescriptionArrayList.indexOf(description));
        sharedDescriptionArrayList.remove(description);
        // после удаления переопределяем текущую заметку
        if (currentDescription == description) {
            if (sharedDescriptionArrayList.size() > 0)
                currentDescription = sharedDescriptionArrayList.get(0);
            else
                currentDescription = null;
        }
    }

    // метод удаляет все заметки
    private void itemRemovedAll() {
        sharedDescriptionArrayList.clear();
        listAdapter.notifyDataSetChanged();
        currentDescription = null;
    }

    // создаем контекстное меню
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    // настраиваем отработку нажатий контекстного меню
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // определяем на какой заметке в recyclerView было нажатие
        int menuPosition = listAdapter.getMenuPosition();
        Description contextDescription = sharedDescriptionArrayList.get(menuPosition);
        switch (item.getItemId()) {
            case R.id.change_name:
                // запускаем диалоговое окно для изменения названия заметки, на которой было нажатие
                dialogFragmentName(contextDescription.getName(), "Введите новое название заметки");
                // считываем результат изменения названия заметки
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                    // сохраняем новое название заметки
                    contextDescription.setName(result.getString("NEW_NAME"));
                    // изменяем заметку в recyclerView
                    listAdapter.notifyItemChanged(menuPosition);
                    // в ландшафтной ориентации, если изменяли название текущей заметки, отображаем текущий DescriptionFragment с измененным названием
                    if (isLandscape() && currentDescription == contextDescription)
                        showDescriptionLand(currentDescription);
                });
                return true;
            case R.id.change_description:
                // запускаем диалоговое окно для изменения описания заметки, на которой было нажатие
                dialogFragmentName(contextDescription.getDescription(), "Введите новое описание заметки");
                // считываем результат изменения описания заметки
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                    // сохраняем новое описание заметки
                    contextDescription.setDescription(result.getString("NEW_NAME"));
                    // изменять заметку в recyclerView не требуется, т.к. поле "описание" отсутствует
                    // в ландшафтной ориентации, если изменяли описание текущей заметки, отображаем текущий DescriptionFragment с измененным описанием
                    if (isLandscape() && currentDescription == contextDescription)
                        showDescriptionLand(currentDescription);
                });
                return true;
            case R.id.change_date:
                dialogFragmentDate(contextDescription.getDate());
                // считываем результат изменения даты заметки, на которой было нажатие
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_DATE", getViewLifecycleOwner(), (requestKey, result) -> {
                    // сохраняем новую дату заметки
                    contextDescription.setDate((Calendar) result.getSerializable("NEW_DATE"));
                    // изменяем заметку в recyclerView
                    listAdapter.notifyItemChanged(menuPosition);
                });
                return true;

            case R.id.delete:
                //удаляем заметку, на которой было нажатие
                alertDialogRemove(recyclerView, contextDescription);
                return true;

            case R.id.delete_all:
                // удаляем все заметки
                alertDialogRemoveAll();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // метод создает диалог-фрагмент для изменения названия или описания заметки
    private void dialogFragmentName (String descriptionName, String title){
        DialogFragmentName.newInstance(descriptionName, title).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    // метод создает диалог-фрагмент для изменения даты заметки
    private void dialogFragmentDate (Calendar date){
        DialogFragmentCalendar.newInstance(date).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    // метод создает диалоговое окно для удаления заметки (общий для вызова из контекстного меню и из toolbar в ландшафтной ориентации)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void alertDialogRemove(View view, Description description) {
        //создаем view c название заметки и кнопкой "подробнее"
        View alertDialogRemove = getLayoutInflater().inflate(R.layout.alert_dialog_remove, null);
        TextView textView = alertDialogRemove.findViewById(R.id.name);
        textView.setText(description.getName());
        Button button = alertDialogRemove.findViewById(R.id.details);
        //обработка нажатия на кнопку "подробнее"
        button.setOnClickListener(view1 -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            //запускаем snackbar c датой и описанием заметки
            Snackbar.make(button, formatter.format(description.getDate().getTime()) + " " + description.getDescription(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view11 -> {
                    })
                    .setTextMaxLines(5)
                    .show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Действительно удалить заметку?")
                .setView(alertDialogRemove)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    // удаляем заметку и переопределяем текущую заметку
                    itemRemoved(description);
                    // запускаем snackbar c уведомлением об удалении заметки
                    snackBarRemove(view, description.getName());
                    // если была удалена текущая заметка, в ландшафтной ориентации отображаем новый текущий DescriptionFragment
                    if (isLandscape() && description == currentDescription)
                        showDescriptionLand(currentDescription);
                })
                .show();

    }

    // метод создает диалоговое окно для удаления всех заметок
    @SuppressLint("NotifyDataSetChanged") //
    private void alertDialogRemoveAll() {
        View alertDialogRemoveAll = getLayoutInflater().inflate(R.layout.alert_dialog_remove_all, null);
        new AlertDialog.Builder(getContext())
                .setView(alertDialogRemoveAll)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    // удаляем все заметки
                    itemRemovedAll();
                    // запускаем snackbar c уведомлением об удалении всех заметок
                    snackBarRemove(recyclerView);
                    // в ландшафтной ориентации отображаем пустой макет (currentDescription = null)
                    if (isLandscape())
                        showDescriptionLand(currentDescription);
                })
                .show();

    }

    // snackbar для удаления заметки
    private void snackBarRemove(View view, String name) {
        Snackbar.make(view, name + " удалена", Snackbar.LENGTH_LONG)
                .show();
    }

    // snackbar для удаления всех заметок
    private void snackBarRemove(View view) {
        Snackbar.make(view, "Все заметки удалены", Snackbar.LENGTH_LONG)
                .show();
    }

    // метод создает фрагмент DescriptionFragment в ландшафтной ориентации
    private void showDescriptionLand(Description description) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_description, DescriptionFragment.newInstance(description))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // метод создает фрагмент DescriptionFragment в портретной ориентации
    private void showDescriptionPort (Description description) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, DescriptionFragment.newInstance(description))
                .addToBackStack("") // добавили в стек для закрытия фрагмента по кнопке "назад"
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private boolean isLandscape (){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_DESCRIPTION, currentDescription); // сохраняем объект текущей заметки
        super.onSaveInstanceState(outState);
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    // метод создает диалог-фрагмент NewDescriptionFragment для создания новой заметки
    public void addDescription() {
        // создаем диалог-фрагмент
        NewDescriptionFragment.newInstance().show(requireActivity().getSupportFragmentManager(), "NEW_DESCRIPTION_FRAGMENT");
        // получаем результат из диалога-фрагмента
        requireActivity().getSupportFragmentManager().setFragmentResultListener("NEW_DESCRIPTION", getViewLifecycleOwner(), (requestKey, result) -> {
            String name = result.getString("NEW_NAME");
            String details = result.getString("NEW_DETAILS");
            Calendar date = (Calendar) result.getSerializable("NEW_DATE");
            //создаем новую заметку и добавляем ее в ArrayList
            sharedDescriptionArrayList.add(new Description(name, details, date));
            //переопределяем текущую заметку
            currentDescription = sharedDescriptionArrayList.get(sharedDescriptionArrayList.size() - 1);
            //в ландшафтной ориентации отображаем новый текущий DescriptionFragment
            if (isLandscape())
                showDescriptionLand(currentDescription);
            //добавляем и перематываем recyclerView на новую текущую заметку
            listAdapter.notifyItemInserted(sharedDescriptionArrayList.size() - 1);
            recyclerView.smoothScrollToPosition(sharedDescriptionArrayList.size() - 1);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // сохраняем ArrayList через sharedPreferences
        sharedPreferences.edit().putString(KEY, new GsonBuilder().create().toJson(sharedDescriptionArrayList)).apply();
    }
}