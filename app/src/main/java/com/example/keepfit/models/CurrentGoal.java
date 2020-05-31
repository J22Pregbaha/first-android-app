package com.example.keepfit.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_history", indices = {@Index(value = {"current_date"}, unique = true)})
public class CurrentGoal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "goal_name")
    private String name;

    @ColumnInfo(name = "number_of_steps")
    private int steps;

    @ColumnInfo(name = "user_input")
    private int input;

    @ColumnInfo(name = "current_date")
    private String date;

    public CurrentGoal(String name, int steps, int input, String date) {
        this.name = name;
        this.steps = steps;
        this.input = input;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSteps() {
        return steps;
    }

    public int getInput() {
        return input;
    }

    public String getDate() {
        return date;
    }
}
