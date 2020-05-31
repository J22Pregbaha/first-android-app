package com.example.keepfit.goalsPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.example.keepfit.models.Goals;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ViewGoals extends AppCompatActivity {
    private static final String TAG = "ViewGoals";

    public static final String allowEdit = "pref_goal";

    private ViewGoalsViewModel viewGoalsViewModel;
    private MainActivityViewModel mainActivityViewModel;

    private FloatingActionButton add_goal_button;

    private Context context = ViewGoals.this;
    private static final int ACTIVITY_NUM = 2;

    private static final int ADD_GOAL_REQUEST = 1;
    private static final int EDIT_GOAL_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goals);
        Toast.makeText(context, "Swipe to delete", Toast.LENGTH_SHORT).show();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Goals");

        setupBottomNavigationView();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final GoalsRecyclerAdapter adapter = new GoalsRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        viewGoalsViewModel = ViewModelProviders.of(this).get(ViewGoalsViewModel.class);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewGoalsViewModel.getAllGoals().observe(this, new Observer<List<Goals>>() {
            @Override
            public void onChanged(List<Goals> goals) {
                //update RecyclerView
                adapter.submitList(goals);
            }
        });

        add_goal_button = findViewById(R.id.add_goal_button);
        add_goal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoal();
            }
        });

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //to get the goal at the exact position where you swipe left
                if (adapter.getGoalAt(viewHolder.getAdapterPosition()).getActive() == 0){
                    viewGoalsViewModel.delete(adapter.getGoalAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    Toast.makeText(context, "Active goal cannot be deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        //Edit
        adapter.setOnItemClickListener(new GoalsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Goals goal) {
                if (String.valueOf(sharedPreferences.getBoolean(allowEdit, false)) == "true") {
                    if (goal.getActive() != 1) {
                        Intent intent = new Intent(context, AddEditGoal.class);
                        intent.putExtra(AddEditGoal.EXTRA_GOAL_ID, goal.getId());
                        intent.putExtra(AddEditGoal.EXTRA_GOAL_NAME, goal.getGoal_name());
                        intent.putExtra(AddEditGoal.EXTRA_STEPS, goal.getNumber_of_steps());
                        intent.putExtra(AddEditGoal.EXTRA_ACTIVE, goal.getActive());
                        startActivityForResult(intent, EDIT_GOAL_REQUEST);
                    }
                }
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: Setting up Navigation View");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        BottomNavigationViewHelper.enableNavigation(context, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
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
                Intent intent = new Intent(this, GoalsActivitySettings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addGoal() {
        Intent intent = new Intent(this, AddEditGoal.class);
        startActivityForResult(intent, ADD_GOAL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Insert
        if (requestCode == ADD_GOAL_REQUEST && resultCode == RESULT_OK) {
            final String name = data.getStringExtra(AddEditGoal.EXTRA_GOAL_NAME);
            final int steps = data.getIntExtra(AddEditGoal.EXTRA_STEPS, 1000);
            int active = data.getIntExtra(AddEditGoal.EXTRA_ACTIVE, 0);

            //int id = data.getIntExtra(AddEditGoal.EXTRA_GOAL_ID, -1);

            Goals goals = new Goals(name, steps, active);
            viewGoalsViewModel.addGoal(goals);

            if (active == 1) {
                viewGoalsViewModel.updateInactiveGoals(name);
                mainActivityViewModel.getCount().observe(this, new Observer<List<CurrentGoal>>() {
                    @Override
                    public void onChanged(List<CurrentGoal> currentGoals) {
                        if (!currentGoals.isEmpty()) {
                            mainActivityViewModel.getCurrentRecord().observe(ViewGoals.this, new Observer<CurrentGoal>() {
                                @Override
                                public void onChanged(CurrentGoal currentGoal) {
                                    int newRecord = currentGoal.getInput();
                                    String current_date = currentGoal.getDate();
                                    CurrentGoal goals = new CurrentGoal(name, steps, newRecord, current_date);
                                    goals.setId(currentGoal.getId());
                                    mainActivityViewModel.updateRecord(goals);
                                }
                            });
                        }
                    }
                });
            }
            Toast.makeText(this, "Goal added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_GOAL_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditGoal.EXTRA_GOAL_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Activity can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            final String name = data.getStringExtra(AddEditGoal.EXTRA_GOAL_NAME);
            final int steps = data.getIntExtra(AddEditGoal.EXTRA_STEPS, 1000);
            int active = data.getIntExtra(AddEditGoal.EXTRA_ACTIVE, 0);
            Goals goals = new Goals(name, steps, active);
            goals.setId(id);
            viewGoalsViewModel.update(goals);
            if (active==1){
                viewGoalsViewModel.updateInactiveGoals(name);
                mainActivityViewModel.getCount().observe(this, new Observer<List<CurrentGoal>>() {
                    @Override
                    public void onChanged(List<CurrentGoal> currentGoals) {
                        if (!currentGoals.isEmpty()) {
                            mainActivityViewModel.getCurrentRecord().observe(ViewGoals.this, new Observer<CurrentGoal>() {
                                @Override
                                public void onChanged(CurrentGoal currentGoal) {
                                    int newRecord = currentGoal.getInput();
                                    String current_date = currentGoal.getDate();
                                    CurrentGoal goals = new CurrentGoal(name, steps, newRecord, current_date);
                                    goals.setId(currentGoal.getId());
                                    mainActivityViewModel.updateRecord(goals);
                                }
                            });
                        }
                    }
                });
            }

            Toast.makeText(this, "Goal updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Goal not added", Toast.LENGTH_SHORT).show();
        }
    }
}
