package com.example.keepfit.homePage;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keepfit.R;
import com.example.keepfit.Utils.BottomNavigationViewHelper;
import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.keepfit.KeepFitApp.CHANNEL_1_ID;


public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    public static final String allowNotifications = "pref_notifications";

    private MainActivityViewModel mainActivityViewModel;
    private FloatingActionButton record_button;

    private TextView goal_name, steps, percentage, today_date;
    private int recordedSteps;
    private int activeGoalPercentage;

    private EditText stepsInput;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler mHandler = new Handler();

    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home");

        today_date = findViewById(R.id.date);
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
        Date date = new Date();
        String todays_date = formatter.format(date);
        today_date.setText(todays_date);

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        notificationManager = NotificationManagerCompat.from(context);

        setupBottomNavigationView();

        stepsInput = findViewById(R.id.stepsInput);

        record_button = findViewById(R.id.record_steps);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordActivity();
            }
        });

        goal_name = findViewById(R.id.goal_name);

        steps = findViewById(R.id.steps);
        percentage = findViewById(R.id.percentage);

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mainActivityViewModel.getActiveGoal().observe(this, new Observer<Goals>() {
            @Override
            public void onChanged(Goals goals) {
                goal_name.setText(goals.getGoal_name());
                steps.setText("0/" + goals.getNumber_of_steps() + " steps");
            }
        });

        mainActivityViewModel.getCount().observe(this, new Observer<List<CurrentGoal>>() {
            @Override
            public void onChanged(List<CurrentGoal> currentGoals) {
                if (currentGoals.isEmpty()) {
                    percentage.setText("Percentage completed: 0%");
                } else {
                    mainActivityViewModel.getActiveGoal().observe(MainActivity.this, new Observer<Goals>() {
                        @Override
                        public void onChanged(Goals goals) {
                            final int stepsActive = goals.getNumber_of_steps();

                            mainActivityViewModel.getCurrentRecord().observe(MainActivity.this, new Observer<CurrentGoal>() {
                                @Override
                                public void onChanged(CurrentGoal currentGoal) {
                                    recordedSteps = currentGoal.getInput();
                                    activeGoalPercentage = (recordedSteps * 100) / stepsActive;
                                    percentage.setText("Percentage completed: " + activeGoalPercentage + "%");
                                    stepsInput.setHint("Enter additional steps");
                                    steps.setText(currentGoal.getInput() + "/" + stepsActive + " steps");

                                    //Progress Bar
                                    progressBar = findViewById(R.id.progressBar);
                                    progressBar.setMax(stepsActive);
                                    progressStatus = recordedSteps;

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setProgress(progressStatus);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: Setting up Navigation View");
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void recordActivity() {
        mainActivityViewModel.getCount().observe(this, new Observer<List<CurrentGoal>>() {
            @Override
            public void onChanged(List<CurrentGoal> currentGoals) {
                if (currentGoals.isEmpty()) {
                    mainActivityViewModel.getActiveGoal().observe(MainActivity.this, new Observer<Goals>() {
                        @Override
                        public void onChanged(Goals goals) {
                            String name = goals.getGoal_name();
                            int steps = goals.getNumber_of_steps();
                            String input = stepsInput.getText().toString();
                            if (!input.equals("")) {
                                int record = Integer.parseInt(input);

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
                                Date date = new Date();
                                String current_date = formatter.format(date);
                                int percent = (record * 100) / steps;
                                CurrentGoal currentGoal = new CurrentGoal(name, steps, record, current_date);
                                mainActivityViewModel.addGoal(currentGoal);
                                stepsInput.getText().clear();

                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                                if (String.valueOf(sharedPreferences.getBoolean(allowNotifications, false)) == "true") {
                                    sendOnChannel1(percent);
                                }

                                Toast.makeText(MainActivity.this, "Entry updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mainActivityViewModel.getCurrentRecord().observe(MainActivity.this, new Observer<CurrentGoal>() {
                        @Override
                        public void onChanged(CurrentGoal currentGoal) {
                            String input = stepsInput.getText().toString();
                            if (!input.equals("")) {
                                int currentSteps = currentGoal.getSteps();
                                int record = Integer.parseInt(input);
                                int newRecord = record + currentGoal.getInput();

                                int percent = (newRecord * 100) / currentSteps;

                                mainActivityViewModel.updateInput(newRecord, currentGoal.getId());
                                stepsInput.getText().clear();

                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                                if (String.valueOf(sharedPreferences.getBoolean(allowNotifications, false)) == "true") {
                                    sendOnChannel1(percent);
                                }

                                Toast.makeText(MainActivity.this, "Entry updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendOnChannel1(int percent) {
        int percentageCompleted = percent;
        if (percentageCompleted == 50) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_keepfit)
                    .setContentTitle("Percentage completed")
                    .setContentText("You are halfway to reaching your goal for today")
                    .build();

            notificationManager.notify(1, notification);
        }/* else if(percentageCompleted > 50 && percentageCompleted != 100){
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_keepfit)
                    .setContentTitle("Percentage completed")
                    .setContentText("You are " + percentageCompleted + "% done with your goal for today")
                    .build();

            notificationManager.notify(1, notification);
        }*/ else if (percentageCompleted >= 100) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_keepfit)
                    .setContentTitle("Percentage completed")
                    .setContentText("Congratulations! You have completed your goal for today")
                    .build();

            notificationManager.notify(1, notification);
        }
    }
}
