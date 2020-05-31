package com.example.keepfit.homePage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.keepfit.db.GoalsRepository;
import com.example.keepfit.db.MainActivityRepository;
import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private MainActivityRepository repository;

    private LiveData<Goals> activeGoal;
    private LiveData<CurrentGoal> currentGoalRecord;
    private LiveData<List<CurrentGoal>> getCountRecord;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repository = new MainActivityRepository(application);
        activeGoal = repository.getActiveGoal();
        currentGoalRecord = repository.getCurrentGoalRecord();
        getCountRecord = repository.ifRecordExists();
    }

    public LiveData<Goals> getActiveGoal(){
        return activeGoal;
    }

    public LiveData<CurrentGoal> getCurrentRecord(){
        return currentGoalRecord;
    }

    public LiveData<List<CurrentGoal>> getCount() {
        return getCountRecord;
    }

    public void addGoal(CurrentGoal goals){
        repository.addRecord(goals);
    }

    public void updateRecord(CurrentGoal goals){
        repository.updateRecord(goals);
    }

    public void updateInput(int user_input, int id){
        repository.updateInput(user_input, id);
    }
}
