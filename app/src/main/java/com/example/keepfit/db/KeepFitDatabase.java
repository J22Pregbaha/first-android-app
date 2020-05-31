package com.example.keepfit.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

@Database(entities = {CurrentGoal.class, Goals.class}, version = 2)
public abstract class KeepFitDatabase extends RoomDatabase {

    private static KeepFitDatabase instance;

    public abstract MyDao myDao();

    public static synchronized KeepFitDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), KeepFitDatabase.class, "KeepFitdb")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }


    //Performs this on creation of app
    private static Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //to populate it with the dummy data shown below
            new PopulateDbAsyncTask(instance).execute();
        }
        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
            //to populate it with the dummy data shown below
            new PopulateDbAsyncTask(instance).execute();
        }
    };


    //To populate with dummy data
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private MyDao myDao;
        private PopulateDbAsyncTask(KeepFitDatabase db){
            myDao = db.myDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            myDao.addGoal(new Goals("Default Goal", 6000, 1));
            return null;
        }
    }
}
