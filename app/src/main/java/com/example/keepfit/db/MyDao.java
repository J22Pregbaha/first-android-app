package com.example.keepfit.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void addGoal(Goals name);

    @Query("SELECT * FROM all_goals WHERE goal_name = :name AND id <> :id")
    LiveData<List<Goals>> ifNameExists(String name, int id);

    @Query("SELECT * FROM goal_history WHERE `current_date` = :date AND id <> :id")
    LiveData<List<CurrentGoal>> ifDateExists(String date, int id);

    @Update
    void update(Goals name);

    @Query("UPDATE all_goals SET active=0 WHERE goal_name <> :name")
    void updateInactiveGoals(String name);

    @Delete
    void delete(Goals name);

    @Insert
    void addRecord(CurrentGoal name);

    @Update
    void updateRecord(CurrentGoal name);

    @Query("UPDATE goal_history SET user_input=:user_input WHERE id=:id")
    void updateInput(int user_input, int id);

    @Query("DELETE FROM goal_history")
    void deleteHistory();

    @Query("SELECT * FROM all_goals ORDER BY active DESC")
    LiveData<List<Goals>> getAllGoals();

    @Query("SELECT * FROM goal_history ORDER BY `current_date` DESC")
    LiveData<List<CurrentGoal>> getHistory();

    @Query("SELECT * FROM all_goals WHERE active=1 LIMIT 1")
    LiveData<Goals> getActiveGoal();

    @Query("SELECT * FROM goal_history WHERE `current_date`=:date")
    LiveData<CurrentGoal> getCurrentGoalRecord(String date);

    @Query("SELECT * FROM goal_history WHERE `current_date`=:date")
    LiveData<List<CurrentGoal>> ifRecordExists(String date);

}
