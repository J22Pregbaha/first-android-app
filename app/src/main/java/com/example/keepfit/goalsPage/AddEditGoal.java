package com.example.keepfit.goalsPage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.keepfit.R;
import com.example.keepfit.models.Goals;

import java.util.List;

public class AddEditGoal extends AppCompatActivity {
    public static final String EXTRA_GOAL_ID = "com.example.keepfit.EXTRA_GOAL_ID";
    public static final String EXTRA_GOAL_NAME = "com.example.keepfit.EXTRA_GOAL_NAME";
    public static final String EXTRA_STEPS = "com.example.keepfit.EXTRA_STEPS";
    public static final String EXTRA_ACTIVE = "com.example.keepfit.EXTRA_ACTIVE";
    private EditText name, steps;
    private Switch aSwitch;
    private int active;
    private Button Bnsave;

    private ViewGoalsViewModel viewGoalsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        viewGoalsViewModel = ViewModelProviders.of(this).get(ViewGoalsViewModel.class);

        name = findViewById(R.id.goal_name);
        steps = findViewById(R.id.number_of_steps);
        aSwitch = findViewById(R.id.active_switch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    active = 1;
                }else{
                    active = 0;
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_GOAL_ID)){
            actionBar.setTitle("Edit Goal");
            name.setText(intent.getStringExtra(EXTRA_GOAL_NAME));
            steps.setText(String.valueOf(intent.getIntExtra(EXTRA_STEPS, 1000)));
        }else {
            actionBar.setTitle("Add new Goal");
        }

        Bnsave = findViewById(R.id.save_button);

        Bnsave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveGoal();
            }
        });
    }

    private void saveGoal(){
        final String goal_name = name.getText().toString();
        String input = steps.getText().toString();
        if (!input.equals("")) {
            final int stepsNo = Integer.parseInt(input);


            if (goal_name.trim().isEmpty() || stepsNo == 0 || steps.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please input a goal name and number of steps", Toast.LENGTH_SHORT).show();
            }

            viewGoalsViewModel.ifNameExists(goal_name, getIntent().getIntExtra(EXTRA_GOAL_ID, 0)).observe(this, new Observer<List<Goals>>() {
                @Override
                public void onChanged(List<Goals> goals) {
                    if (!goals.isEmpty()) {
                        Toast.makeText(AddEditGoal.this, "Goal name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent data = new Intent();

                        data.putExtra(EXTRA_GOAL_NAME, goal_name);
                        data.putExtra(EXTRA_STEPS, stepsNo);
                        data.putExtra(EXTRA_ACTIVE, active);

                        int id = getIntent().getIntExtra(EXTRA_GOAL_ID, -1);
                        if (id != -1) {
                            data.putExtra(EXTRA_GOAL_ID, id);
                        }

                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            });
        }
    }
}
