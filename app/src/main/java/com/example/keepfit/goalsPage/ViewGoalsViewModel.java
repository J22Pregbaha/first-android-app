package com.example.keepfit.goalsPage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.keepfit.db.GoalsRepository;
import com.example.keepfit.models.Goals;

import java.util.List;

public class ViewGoalsViewModel extends AndroidViewModel {
    private GoalsRepository repository;
    private LiveData<List<Goals>> allGoals;
    private LiveData<List<Goals>> ifNameExists;

    public ViewGoalsViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalsRepository(application);
        allGoals = repository.getEveryGoal();
    }

    public void addGoal(Goals goals){
        repository.addGoal(goals);
    }

    public void update(Goals goals){
        repository.update(goals);
    }

    public void updateInactiveGoals(String name){
        repository.updateInactiveGoals(name);
    }

    public void delete(Goals goals){
        repository.delete(goals);
    }

    public LiveData<List<Goals>> getAllGoals() {
        return allGoals;
    }

    public LiveData<List<Goals>> ifNameExists(String name, int id) {
        ifNameExists = repository.getIfNameExists(name, id);
        return ifNameExists;
    }
}
