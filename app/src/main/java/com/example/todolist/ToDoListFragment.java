package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ToDoListFragment extends Fragment {

    private static final String CURRENT_DESCRIPTION = "current_description";
    private Description currentDescription;
    private RecyclerView recyclerView;
    private ArrayList <Description> descriptionArrayList;

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
        initRecyclerView(recyclerView,descriptionArrayList);
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView (RecyclerView recyclerView, ArrayList <Description> descriptionArrayList){
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //recyclerView.setLayoutManager(linearLayoutManager);
        ListAdapter listAdapter = new ListAdapter(descriptionArrayList, getContext());
        recyclerView.setAdapter(listAdapter);

        if (!isLandscape()){
            @SuppressLint("UseRequireInsteadOfGet")
            DividerItemDecoration itemDecoration = new
                    DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);

        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator,
                null));
        recyclerView.addItemDecoration(itemDecoration);
        }
        listAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                currentDescription = Description.getDescriptionArrayList().get(position);
                showDescription (currentDescription);
            }
        });
        listAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemLongClick(View view, int position) {
                initPopup(view,Description.getDescriptionArrayList().get(position));
            }
        });

    }

    public void initRecyclerView (){
        initRecyclerView(recyclerView, descriptionArrayList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // если заметки просматривались, получаем индекс последней просмотренной заметки
        if (savedInstanceState != null){
            currentDescription=savedInstanceState.getParcelable(CURRENT_DESCRIPTION);
        }

        // в ландшафтной ориентации сразу отображаем необходимые фрагменты
        if (isLandscape()){
            if (currentDescription == null){
                currentDescription = Description.getDescriptionArrayList().get(0);
            }
            showDescriptionLand(currentDescription);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPopup(View view, Description description){
            PopupMenu popupMenu = new PopupMenu(requireActivity(), view);
            requireActivity().getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.change_name:
                        dialogFragmentName (description.getName(), "Введите новое название заметки");
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                            description.setName(result.getString("NEW_NAME"));
                            initRecyclerView(recyclerView, descriptionArrayList);// TODO устранить баг при повороте
                            if (isLandscape()&&currentDescription==description)
                                showDescriptionLand(description);
                        });
                        break;
                    case R.id.change_description:
                        dialogFragmentName (description.getDescription(), "Введите новое описание заметки");
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                            description.setDescription(result.getString("NEW_NAME"));
                            if (isLandscape()&&currentDescription==description)
                                showDescriptionLand(description);
                        });
                        break;
                    case R.id.change_date:
                        dialogFragmentDate(description.getDate());
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_DATE", getViewLifecycleOwner(), (requestKey, result) -> description.setDate((Calendar) result.getSerializable("NEW_DATE")));
                        // TODO доделать изменение в рантайме в ландшафтном представлении
                        break;

                    case R.id.popup:
                        alertDialogRemove(view, description);
                        break;
                    case R.id.popupAll:
                        break;
                }
                return true;
            });
            popupMenu.show();

    }

    private void dialogFragmentName (String descriptionName, String title){
        DialogFragmentName.newInstance(descriptionName, title).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    private void dialogFragmentDate (Calendar date){
        DialogFragmentCalendar.newInstance(date).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void alertDialogRemove (View view, Description description){
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
                    descriptionArrayList.remove(description);
                    snackBarRemove(view, description.getName());
                    initRecyclerView(recyclerView, descriptionArrayList);
                    if (isLandscape()&&currentDescription==description)
                        showDescriptionLand(descriptionArrayList.get(0));
                })
                .show();

    }

    private void snackBarRemove (View view, String name){
        Snackbar.make(view, name + " удалена", Snackbar.LENGTH_LONG)
                .show();
    }

    private void showDescriptionLand (Description description) {
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
}