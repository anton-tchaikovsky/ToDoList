package com.example.todolist;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import java.util.Calendar;

public class Description implements Parcelable {

    private  String name;
    private  String description;
    private Calendar date;
    private int id;
    private static int count;

    public void setDate(Calendar date) {
        this.date = date;
    }

    {
        id = count++;
    }

    public int getId() {
        return id;
    }

    public Description(String name, String description, Calendar date) {
        this.name = name;
        this.description = description;
        this.date = date;
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

    // переопределен метод equals, т.к. при считывании ArrayList с заметками через sharedPreference и последующем добавлении
    // новых заметок могут совпадать id разных заметок
    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Description)) return false;

       Description description1 = (Description) obj;
        return this.name.equals(description1.name) && this.description.equals(description1.description) &&
                this.id == description1.id && this.date.equals(description1.date);
    }
}
