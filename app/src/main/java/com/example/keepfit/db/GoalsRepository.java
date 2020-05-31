package com.example.keepfit.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.util.List;

public class GoalsRepository {

    private MyDao myDao;
    private LiveData<List<Goals>> allTheGoals;
    private LiveData<List<Goals>> ifNameExists;
    private LiveData<List<CurrentGoal>> ifDateExists;
    private LiveData<List<CurrentGoal>> history;

    public GoalsRepository(Application application){
        KeepFitDatabase database = KeepFitDatabase.getInstance(application);
        myDao = database.myDao();
        allTheGoals = myDao.getAllGoals();
        history = myDao.getHistory();
    }

    public void addGoal(Goals goals){
        new InsertGoalAsyncTask(myDao).execute(goals);
    }

    public void update(Goals goals){
        new UpdateGoalAsyncTask(myDao).execute(goals);
    }

    public void updateInactiveGoals(String goals){
        new UpdateInactiveGoalsAsyncTask(myDao).execute(goals);
    }

    public void delete(Goals goals){
        new DeleteGoalAsyncTask(myDao).execute(goals);
    }

    public void deleteHistory(){
        new DeleteHistoryAsyncTask(myDao).execute();
    }

    public LiveData<List<Goals>> getEveryGoal(){
        return allTheGoals;
    }

    public LiveData<List<Goals>> getIfNameExists(String name, int id){
        ifNameExists = myDao.ifNameExists(name, id);
        return ifNameExists;
    }

    public LiveData<List<CurrentGoal>> getIfDateExists(String date, int id){
        ifDateExists = myDao.ifDateExists(date, id);
        return ifDateExists;
    }

    public LiveData<List<CurrentGoal>> getHistory(){
        return history;
    }

    private static class InsertGoalAsyncTask extends AsyncTask<Goals, Void, Void>{
        private MyDao myDao;

        private InsertGoalAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(Goals... goals){
            myDao.addGoal(goals[0]);
            return null;
        }
    }

    private static class UpdateGoalAsyncTask extends AsyncTask<Goals, Void, Void>{
        private MyDao myDao;

        private UpdateGoalAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(Goals... goals){
            myDao.update(goals[0]);
            return null;
        }
    }

    private static class UpdateInactiveGoalsAsyncTask extends AsyncTask<String, Void, Void>{
        private MyDao myDao;

        private UpdateInactiveGoalsAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(String... goals){
            myDao.updateInactiveGoals(goals[0]);
            return null;
        }
    }

    private static class DeleteGoalAsyncTask extends AsyncTask<Goals, Void, Void>{
        private MyDao myDao;

        private DeleteGoalAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(Goals... goals){
            myDao.delete(goals[0]);
            return null;
        }
    }

    private static class DeleteHistoryAsyncTask extends AsyncTask<Void, Void, Void>{
        private MyDao myDao;

        private DeleteHistoryAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(Void... voids){
            myDao.deleteHistory();
            return null;
        }
    }
}
