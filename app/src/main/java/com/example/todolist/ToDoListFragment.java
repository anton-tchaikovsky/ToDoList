package com.example.todolist;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToDoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CURRENT_DESCRIPTION = "current_description";
    private Description currentDescription;
    View dataContainer;
    //DescriptionsArrayList descriptionsArrayList;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToDoListFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoListFragment newInstance(String param1, String param2) {
        ToDoListFragment fragment = new ToDoListFragment();
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
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(requireActivity(),view);
                requireActivity().getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.popup:
                                Description.getDescriptionArrayList().remove(description);
                                initList();
                                break;
                            case R.id.popupAll:
                                //Description.getDescriptionArrayList().clear(); TODO доделать удаление
                                //initList();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });

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