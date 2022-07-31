package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class DescriptionsArrayList implements Parcelable {
    private static DescriptionsArrayList INSTANCE;
    ArrayList <Description> descriptionArrayList;// хранит объекты заметок
    private final SimpleDateFormat formatter; // используется для работы с полем date заметки

    /**
     * Конструктор впервые создает List заметок с исходной информацией из ресурвов array_to_do_list
     * @param context
     */
    @SuppressLint("SimpleDateFormat")
    private DescriptionsArrayList(Context context) {
        this.descriptionArrayList = new ArrayList<>();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i<context.getResources().getStringArray(R.array.to_do_list).length; i++){
            descriptionArrayList.add(new Description(context.getResources().getStringArray(R.array.to_do_list)[i], context.getResources().getStringArray(R.array.description)[i],setDate (context.getResources().getStringArray(R.array.date_description)[i])));
        }
    }

    protected DescriptionsArrayList(Parcel in, SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    public final Creator<DescriptionsArrayList> CREATOR = new Creator<DescriptionsArrayList>() {
        @Override
        public DescriptionsArrayList createFromParcel(Parcel in) {
            return new DescriptionsArrayList(in, formatter);
        }

        @Override
        public DescriptionsArrayList[] newArray(int size) {
            return new DescriptionsArrayList[size];
        }
    };

    private Calendar setDate (String date_string){
        Calendar date = Calendar.getInstance();

        try {
            date.setTime(Objects.requireNonNull(formatter.parse(date_string)));
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static DescriptionsArrayList getInstance (Context context){
        if (INSTANCE==null)
            INSTANCE = new DescriptionsArrayList(context);
        return INSTANCE;
    }

    public ArrayList<Description> getDescriptionArrayList() {
        return descriptionArrayList;
    }

    public Description getDescription (int index){
        return descriptionArrayList.get(index);
    }

    public int size() {
        return descriptionArrayList.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
