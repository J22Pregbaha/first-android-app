package com.example.keepfit.historyPage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.keepfit.db.GoalsRepository;
import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private GoalsRepository repository;
    private LiveData<List<CurrentGoal>> history;
    private LiveData<List<Goals>> allGoals;
    private LiveData<List<CurrentGoal>> ifDateExists;
    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalsRepository(application);
        history = repository.getHistory();
        allGoals = repository.getEveryGoal();
    }

    public void deleteHistory(){
        repository.deleteHistory();
    }

    public LiveData<List<CurrentGoal>> getHistory() {
        return history;
    }

    public LiveData<List<CurrentGoal>> ifDateExists(String date, int id) {
        ifDateExists = repository.getIfDateExists(date, id);
        return ifDateExists;
    }

    public LiveData<List<Goals>> getAllGoals() {
        return allGoals;
    }
}
