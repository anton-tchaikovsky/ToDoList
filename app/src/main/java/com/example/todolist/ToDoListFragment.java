package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ToDoListFragment extends Fragment {

    private static final String CURRENT_DESCRIPTION = "current_description";
    private Description currentDescription;
    private RecyclerView recyclerView;
    private static final int DURATION = 1000;
    ListAdapter listAdapter;
    private ArrayList<Description> descriptionArrayList;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            addDescription();
            return true;
        }
        if (item.getItemId() == R.id.delete) {
            alertDialogRemove(recyclerView, currentDescription, false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list2, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        descriptionArrayList = Description.getDescriptionArrayList();
        initRecyclerView(recyclerView, descriptionArrayList);
        setHasOptionsMenu(true);
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView (RecyclerView recyclerView, ArrayList <Description> descriptionArrayList) {
        listAdapter = new ListAdapter(descriptionArrayList, this, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setHasFixedSize(true);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(DURATION);
        animator.setRemoveDuration(DURATION);
        recyclerView.setItemAnimator(animator);
        if (!isLandscape()) {
            @SuppressLint("UseRequireInsteadOfGet")
            DividerItemDecoration itemDecoration = new
                    DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);

            itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator,
                    null));
            recyclerView.addItemDecoration(itemDecoration);
        }
        listAdapter.setOnItemClickListener((view, position) -> {
            currentDescription = Description.getDescriptionArrayList().get(position);
            showDescription(currentDescription);
        });
    }

    public void updateItemRecyclerView() {
        listAdapter.notifyItemChanged(descriptionArrayList.indexOf(currentDescription));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // если заметки просматривались, получаем индекс последней просмотренной заметки
        if (savedInstanceState != null) {
            currentDescription = savedInstanceState.getParcelable(CURRENT_DESCRIPTION);
        }

        // в ландшафтной ориентации сразу отображаем необходимые фрагменты
        // TODO Доделать отображение DescriptionFragment, когда удалены все заметки
        if (isLandscape()) {
            if (currentDescription == null) {
                currentDescription = descriptionArrayList.get(0);
            }
            showDescriptionLand(currentDescription);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int menuPosition = listAdapter.getMenuPosition();
        Description contextDescription = descriptionArrayList.get(menuPosition);
        switch (item.getItemId()) {
            case R.id.change_name:
                dialogFragmentName(contextDescription.getName(), "Введите новое название заметки");
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                    contextDescription.setName(result.getString("NEW_NAME"));
                    listAdapter.notifyItemChanged(menuPosition);
                    if (isLandscape() && currentDescription == contextDescription)
                        showDescriptionLand(contextDescription);
                });
                return true;
            case R.id.change_description:
                dialogFragmentName(contextDescription.getDescription(), "Введите новое описание заметки");
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                    contextDescription.setDescription(result.getString("NEW_NAME"));
                    if (isLandscape() && currentDescription == contextDescription)
                        showDescriptionLand(contextDescription);
                });
                return true;
            case R.id.change_date:
                dialogFragmentDate(contextDescription.getDate());
                requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_DATE", getViewLifecycleOwner(), (requestKey, result) -> {
                    contextDescription.setDate((Calendar) result.getSerializable("NEW_DATE"));
                    listAdapter.notifyItemChanged(menuPosition);
                });
                return true;

            case R.id.delete:
                alertDialogRemove(recyclerView, contextDescription, true);
                return true;

            case R.id.delete_all:
                alertDialogRemoveAll();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void dialogFragmentName (String descriptionName, String title){
        DialogFragmentName.newInstance(descriptionName, title).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    private void dialogFragmentDate (Calendar date){
        DialogFragmentCalendar.newInstance(date).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void alertDialogRemove(View view, Description description, Boolean isContextMenu) {
        View alertDialogRemove = getLayoutInflater().inflate(R.layout.alert_dialog_remove, null);
        TextView textView = alertDialogRemove.findViewById(R.id.name);
        textView.setText(description.getName());
        Button button = alertDialogRemove.findViewById(R.id.details);
        button.setOnClickListener(view1 -> {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
                    if (isContextMenu)
                        listAdapter.notifyItemRemoved(listAdapter.getMenuPosition());
                    else
                        listAdapter.notifyItemRemoved(descriptionArrayList.indexOf(description));
                    descriptionArrayList.remove(description);
                    snackBarRemove(view, description.getName());
                    if (isLandscape() && currentDescription == description) {
                        currentDescription = descriptionArrayList.get(0);
                        showDescriptionLand(currentDescription);
                    }
                })
                .show();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void alertDialogRemoveAll() {
        View alertDialogRemoveAll = getLayoutInflater().inflate(R.layout.alert_dialog_remove_all, null);
        new AlertDialog.Builder(getContext())
                .setView(alertDialogRemoveAll)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    descriptionArrayList.clear();
                    snackBarRemove(recyclerView);
                    listAdapter.notifyDataSetChanged();
                })
                .show();

    }

    private void snackBarRemove(View view, String name) {
        Snackbar.make(view, name + " удалена", Snackbar.LENGTH_LONG)
                .show();
    }

    private void snackBarRemove(View view) {
        Snackbar.make(view, "Все заметки удалены", Snackbar.LENGTH_LONG)
                .show();
    }

    private void showDescriptionLand(Description description) {
        DescriptionFragment descriptionFragment = DescriptionFragment.newInstance(description);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_description, descriptionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void showDescriptionPort (Description description) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, DescriptionFragment.newInstance(description))
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void showDescription(Description description){

        if (isLandscape())
            showDescriptionLand(description);
        else
            showDescriptionPort(description);
    }

    private boolean isLandscape (){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_DESCRIPTION, currentDescription); // сохраняем объект последней просматриваемой заметки
        super.onSaveInstanceState(outState);
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    public void addDescription() {
        NewDescriptionFragment.newInstance().show(requireActivity().getSupportFragmentManager(), "NEW_DESCRIPTION_FRAGMENT");
        requireActivity().getSupportFragmentManager().setFragmentResultListener("NEW_DESCRIPTION", getViewLifecycleOwner(), (requestKey, result) -> {
            String name = result.getString("NEW_NAME");
            String details = result.getString("NEW_DETAILS");
            Calendar date = (Calendar) result.getSerializable("NEW_DATE");
            descriptionArrayList.add(new Description(name, details, date));
            listAdapter.notifyItemInserted(descriptionArrayList.size() - 1);
            recyclerView.smoothScrollToPosition(descriptionArrayList.size() - 1);
        });
    }
}