package com.example.todolist;

import java.util.Calendar;

public class Description {
<<<<<<< HEAD
    private String name;
    private String description;
=======
    private final String name;
    private final String description;
>>>>>>> f65ca63 (ДЗ 6 пп. 1-6.)
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
