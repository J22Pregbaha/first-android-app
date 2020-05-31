package com.example.keepfit.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "all_goals", indices = {@Index(value = {"goal_name"}, unique = true)})
public class Goals {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "goal_name")
    private String goal_name;

    @ColumnInfo(name = "number_of_steps")
    private int number_of_steps;

    @ColumnInfo(name = "active")
    private int active;

    public Goals(String goal_name, int number_of_steps, int active) {
        this.goal_name = goal_name;
        this.number_of_steps = number_of_steps;
        this.active = active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGoal_name() {
        return goal_name;
    }

    public int getNumber_of_steps() {
        return number_of_steps;
    }

    public int getActive() {
        return active;
    }

    @NonNull
    @Override
    public String toString() {
        return goal_name;
    }
}
