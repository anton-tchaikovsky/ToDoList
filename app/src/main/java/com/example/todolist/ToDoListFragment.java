package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ToDoListFragment extends Fragment {

    private static final String CURRENT_DESCRIPTION = "current_description";
    private Description currentDescription;
    private View dataContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // если заметки просматривались, получаем индекс последней просмотренной заметки
        if (savedInstanceState != null){
            currentDescription=savedInstanceState.getParcelable(CURRENT_DESCRIPTION);
        }
        dataContainer = view.findViewById(R.id.data_container);
        initList(dataContainer);
        // в ландшафтной ориентации сразу отображаем необходимые фрагменты
        if (isLandscape()){
            if (currentDescription == null)
                showDescriptionLand(Description.getDescriptionArrayList().get(0));
            else
                showDescriptionLand(currentDescription);
        }
    }

    /**
     * Метод создает и отображает заметки в фрагменте ToDoListFragment (общий для портретного и ландшафтного экранов)
     * @param view
     */
    private void initList (View view){
        LinearLayout layoutView = (LinearLayout) view;
        layoutView.removeAllViews();
        for (int i = 0; i< Description.getDescriptionArrayList().size(); i++){
            Description description = Description.getDescriptionArrayList().get(i);
            TextView textView = new TextView(getContext());
            textView.setText(description.getName());
            if (isLandscape())
                textView.setTextSize(15);
            else
                textView.setTextSize(20);
            textView.setTextColor(getResources().getColor(R.color.purple_700,  null));
            textView.setBackgroundColor(getResources().getColor(R.color.white, null));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.setTypeface(getResources().getFont(R.font.font_times_new_roman));
            }
            layoutView.addView(textView);
            initPopup (textView, description);


            // отработка нажатия на заметку
            textView.setOnClickListener(view1 -> {
                currentDescription = description;
                showDescription (description);
            });
        }
    }
    public void initList (){
        initList(dataContainer);
    }

    private void initPopup(View view, Description description){
        view.setOnLongClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), view1);
            requireActivity().getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.change_name:
                        dialogFragmentName (description.getName(), "Введите новое название заметки");
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> {
                            description.setName(result.getString("NEW_NAME"));
                            initList();// TODO устранить баг при повороте
                        });

                        break;
                    case R.id.change_description:
                        dialogFragmentName (description.getDescription(), "Введите новое описание заметки");
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_NAME", getViewLifecycleOwner(), (requestKey, result) -> description.setDescription(result.getString("NEW_NAME")));
                        // TODO доделать изменение в рантайме в ландшафтном представлении
                        break;
                    case R.id.change_date:
                        dialogFragmentDate(description.getDate());
                        requireActivity().getSupportFragmentManager().setFragmentResultListener("KEY_NEW_DATE", getViewLifecycleOwner(), (requestKey, result) -> description.setDate((Calendar) result.getSerializable("NEW_DATE")));
                        // TODO доделать изменение в рантайме в ландшафтном представлении
                        break;

                    case R.id.popup:
                        alertDialogRemove(view1, description);
                        break;
                    case R.id.popupAll:
                        //Description.getDescriptionArrayList().clear(); TODO доделать удаление
                        //initList();
                        break;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });

    }


    private void dialogFragmentName (String descriptionName, String title){
        DialogFragmentName.newInstance(descriptionName, title).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

    private void dialogFragmentDate (Calendar date){
        DialogFragmentCalendar.newInstance(date).show(requireActivity().getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }

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
                    Description.getDescriptionArrayList().remove(description);
                    snackBarRemove(view, description.getName());
                    initList();
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
}