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

    public int getMenuPosition() {
        return menuPosition;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

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
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_description,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDescription(descriptionArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return descriptionArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        private final TextView textView;
        private TextView textDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            registerContextMenu(itemView);

            textView = itemView.findViewById(R.id.textView);
            if (isPortrait()){
                Button button = itemView.findViewById(R.id.details);
                textDate = itemView.findViewById(R.id.textDate);
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

            } else{
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
