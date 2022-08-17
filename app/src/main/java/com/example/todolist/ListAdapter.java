package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList <Description> descriptionArrayList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public ListAdapter (ArrayList <Description> descriptionArrayList, Context context){
        this.descriptionArrayList = descriptionArrayList;
        this.context = context;
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

        private TextView textView;
        private Button button;
        private ImageView imageView;
        private TextView textDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            if (!isLandscape()){
                button = itemView.findViewById(R.id.details);
                imageView = itemView.findViewById(R.id.image_view);
                textDate = itemView.findViewById(R.id.textDate);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (onItemClickListener !=null){
                            onItemClickListener.onItemClick(textView, position);
                        }
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int position = getAdapterPosition();
                        if (onItemLongClickListener != null){
                            onItemLongClickListener.onItemLongClick(textView,position);
                        }
                        return true;
                    }
                });
            } else{
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (onItemClickListener !=null){
                            onItemClickListener.onItemClick(textView, position);
                        }
                    }
                });

                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int position = getAdapterPosition();
                        if (onItemLongClickListener != null){
                            onItemLongClickListener.onItemLongClick(textView,position);
                        }
                        return true;
                    }
                });
            }

        }

        public void setDescription (Description description){
            textView.setText(description.getName());
            if (!isLandscape()){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                textDate.setText(formatter.format(description.getDate().getTime()));
            }
        }

        private boolean isLandscape (){
            return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        }
    }

}
