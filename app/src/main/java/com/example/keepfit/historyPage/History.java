package com.example.keepfit.historyPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.keepfit.R;
import com.example.keepfit.Utils.BottomNavigationViewHelper;
import com.example.keepfit.homePage.MainActivityViewModel;
import com.example.keepfit.models.CurrentGoal;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class History extends AppCompatActivity {

    private HistoryViewModel historyViewModel;
    private MainActivityViewModel mainActivityViewModel;

    private FloatingActionButton add_history_button;

    private static final String TAG = "History";
    public static final String allowEdit = "pref_add_history";

    private Context context = History.this;
    private static final int ACTIVITY_NUM = 1;

    private static final int ADD_HISTORY_REQUEST = 1;
    private static final int EDIT_HISTORY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("History");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final HistoryRecyclerAdapter adapter = new HistoryRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        add_history_button = findViewById(R.id.add_history_button);
        add_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHistory();
            }
        });

        if (String.valueOf(sharedPreferences.getBoolean(allowEdit, false)) == "true") {
            add_history_button.show();
            //Edit
            adapter.setOnItemClickListener(new HistoryRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(CurrentGoal currentGoal) {
                    Intent intent = new Intent(context, AddEditHistory.class);
                    intent.putExtra(AddEditHistory.EXTRA_HISTORY_ID, currentGoal.getId());
                    intent.putExtra(AddEditHistory.EXTRA_GOAL_NAME, currentGoal.getName());
                    intent.putExtra(AddEditHistory.EXTRA_STEPS, currentGoal.getSteps());
                    intent.putExtra(AddEditHistory.EXTRA_RECORD, currentGoal.getInput());
                    intent.putExtra(AddEditHistory.EXTRA_DATE, currentGoal.getDate());
                    startActivityForResult(intent, EDIT_HISTORY_REQUEST);
                }
            });
        } else {
            add_history_button.hide();
        }

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        historyViewModel.getHistory().observe(this, new Observer<List<CurrentGoal>>() {
            @Override
            public void onChanged(List<CurrentGoal> currentGoals) {
                //update RecyclerView
                adapter.submitList(currentGoals);
            }
        });

        setupBottomNavigationView();
    }

    public void addHistory() {
        Intent intent = new Intent(this, AddEditHistory.class);
        startActivityForResult(intent, ADD_HISTORY_REQUEST);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: Setting up Navigation View");
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.overflow_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_history:
                historyViewModel.deleteHistory();
                Toast.makeText(context, "History deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, HistorySettings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Insert
        if (requestCode == ADD_HISTORY_REQUEST && resultCode == RESULT_OK) {
            final String name = data.getStringExtra(AddEditHistory.EXTRA_GOAL_NAME);
            final int steps = data.getIntExtra(AddEditHistory.EXTRA_STEPS, 1000);
            final int record = data.getIntExtra(AddEditHistory.EXTRA_RECORD, 1000);
            final String current_date = data.getStringExtra(AddEditHistory.EXTRA_DATE);
            CurrentGoal goals = new CurrentGoal(name, steps, record, current_date);
            mainActivityViewModel.addGoal(goals);
            Toast.makeText(this, "Activity for " + current_date + " added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_HISTORY_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditHistory.EXTRA_HISTORY_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Activity can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            final String name = data.getStringExtra(AddEditHistory.EXTRA_GOAL_NAME);
            final int steps = data.getIntExtra(AddEditHistory.EXTRA_STEPS, 1000);
            final int record = data.getIntExtra(AddEditHistory.EXTRA_RECORD, 1000);
            final String current_date = data.getStringExtra(AddEditHistory.EXTRA_DATE);

            CurrentGoal goals = new CurrentGoal(name, steps, record, current_date);
            goals.setId(id);
            mainActivityViewModel.updateRecord(goals);

            Toast.makeText(this, "Activity for " + current_date + " updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Activity not added", Toast.LENGTH_SHORT).show();
        }
    }
}
