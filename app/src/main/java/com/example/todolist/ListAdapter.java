package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private final ArrayList <Description> descriptionArrayList;
    private OnItemClickListener onItemClickListener;
    private final Context context;
    private final Fragment fragment;
    private int menuPosition;

    //метод возвращает номер элемента текущей карточки в recyclerView
    public int getMenuPosition() {
        return menuPosition;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //метод создает контекстное меню в фрагменте, в котором создан ListAdapter, и вешает его на itemView
    private void registerContextMenu (View itemView){
        if (fragment !=null){
            fragment.registerForContextMenu(itemView);
        }
    }

    public ListAdapter (ArrayList <Description> descriptionArrayList, Fragment fragment, Context context){
        this.descriptionArrayList = descriptionArrayList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //надувае view карточки и создаем ViewHolder(view)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_description,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //заполняем карточки из ArrayList
        holder.setDescription(descriptionArrayList.get(position));
    }


    //устанавливаем количество карточек
    @Override
    public int getItemCount() {
        return descriptionArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        private final TextView textView;
        private TextView textDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           //создаем контекстное меню и вешаем его на view карточки
            registerContextMenu(itemView);

            //находим текстовое поле для названия заметки
            textView = itemView.findViewById(R.id.textView);

            //в портретной ориентации настраиваем кнопку "подробнее" и клик контекстного меню, находим текстовое поле для даты заметки
            if (isPortrait()){
                textDate = itemView.findViewById(R.id.textDate);
                Button button = itemView.findViewById(R.id.details);
                button.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    if (onItemClickListener !=null){
                        onItemClickListener.onItemClick(textView, position);
                    }
                });

                itemView.setOnLongClickListener(view -> {
                    menuPosition = getLayoutPosition();
                    itemView.showContextMenu();
                    return true;
                });

            }
            //в портретной ориентации настраиваем клик и клик контекстного меню
            else{
                textView.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    if (onItemClickListener !=null){
                        onItemClickListener.onItemClick(textView, position);
                    }
                });

               textView.setOnLongClickListener(view -> {
                   menuPosition = getLayoutPosition();
                   itemView.showContextMenu();
                   return true;

               });
            }

        }

        //метод заполняет поля карточки (название заметки и дата) данными из заметки
        public void setDescription (Description description){
            if (description.getName().equals(""))
                textView.setText("Новая заметка");
            else
                textView.setText(description.getName());
            if (isPortrait()){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                textDate.setText(formatter.format(description.getDate().getTime()));
            }
        }

        private boolean isPortrait (){
            return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        }
    }

}
