package com.example.todolist;

import java.util.Calendar;

public class Description {

    private final String name;
    private final String description;
    private Calendar date;

    public Description(String name, String description, Calendar date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getDate() {
        return date;
    }
}
