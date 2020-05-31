package com.example.keepfit.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivityRepository {

    private MyDao myDao;
    private LiveData<Goals> getActiveGoal;
    private LiveData<CurrentGoal> getCurrentGoalRecord;
    private LiveData<List<CurrentGoal>> ifRecordExists;



    public MainActivityRepository(Application application){
        KeepFitDatabase database = KeepFitDatabase.getInstance(application);
        myDao = database.myDao();
        getActiveGoal = myDao.getActiveGoal();
    }

    public LiveData<Goals> getActiveGoal(){
        return getActiveGoal;
    }

    public LiveData<CurrentGoal> getCurrentGoalRecord(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        Date date = new Date();
        String current_date = formatter.format(date);
        getCurrentGoalRecord = myDao.getCurrentGoalRecord(current_date);
        return getCurrentGoalRecord;
    }

    public LiveData<List<CurrentGoal>> ifRecordExists(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        Date date = new Date();
        String current_date = formatter.format(date);
        ifRecordExists = myDao.ifRecordExists(current_date);
        return ifRecordExists;
    }

    public void addRecord(CurrentGoal goals){
        new addRecordAsyncTask(myDao).execute(goals);
    }

    public void updateRecord(CurrentGoal goals){
        new updateRecordAsyncTask(myDao).execute(goals);
    }

    public void updateInput(int user_input, int id){
        new updateInputAsyncTask(myDao).execute(user_input, id);
    }

    private static class updateInputAsyncTask extends AsyncTask<Integer, Void, Void>{
        private MyDao myDao;

        private updateInputAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(Integer... params){
            myDao.updateInput(params[0], params[1]);
            return null;
        }
    }

    private static class addRecordAsyncTask extends AsyncTask<CurrentGoal, Void, Void>{
        private MyDao myDao;

        private addRecordAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(CurrentGoal... goals){
            myDao.addRecord(goals[0]);
            return null;
        }
    }

    private static class updateRecordAsyncTask extends AsyncTask<CurrentGoal, Void, Void>{
        private MyDao myDao;

        private updateRecordAsyncTask(MyDao myDao){
            this.myDao = myDao;
        }
        @Override
        protected Void doInBackground(CurrentGoal... goals){
            myDao.updateRecord(goals[0]);
            return null;
        }
    }

}
