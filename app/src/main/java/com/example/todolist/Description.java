package com.example.todolist;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class Description implements Parcelable {

    private  String name;
    private  String description;
    private Calendar date;
    private int id;
    private static int count;
    private static final ArrayList <Description> descriptionArrayList;

    public void setDate(Calendar date) {
        this.date = date;
    }

    public static ArrayList<Description> getDescriptionArrayList() {
        return descriptionArrayList;
    }

    {
        id = count++;
    }

    static {
        descriptionArrayList = new ArrayList<>();
        descriptionArrayList.add(Description.newDescription());
        }


    public int getId() {
        return id;
    }

    public Description(String name, String description, Calendar date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    @SuppressLint("DefaultLocale")
    public static Description newDescription (){
        String name = "";
        String description = "";
       Calendar date = Calendar.getInstance();
       return  new Description(name, description, date);
    }

    protected Description(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        date = (Calendar) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeSerializable(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Description> CREATOR = new Creator<Description>() {
        @Override
        public Description createFromParcel(Parcel in) {
            return new Description(in);
        }

        @Override
        public Description[] newArray(int size) {
            return new Description[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
