package com.example.keepfit.historyPage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.keepfit.R;
import com.example.keepfit.models.CurrentGoal;
import com.example.keepfit.models.Goals;

import java.util.Calendar;
import java.util.List;

public class AddEditHistory extends AppCompatActivity {
    HistoryViewModel historyViewModel;

    public static final String EXTRA_HISTORY_ID = "com.example.keepfit.EXTRA_HISTORY_ID";
    public static final String EXTRA_GOAL_NAME = "com.example.keepfit.EXTRA_GOAL_NAME";
    public static final String EXTRA_STEPS = "com.example.keepfit.EXTRA_STEPS";
    public static final String EXTRA_RECORD = "com.example.keepfit.EXTRA_RECORD";
    public static final String EXTRA_DATE = "com.example.keepfit.EXTRA_DATE";
    private TextView steps;
    private EditText date;
    private DatePickerDialog.OnDateSetListener setListener;
    private Spinner name;
    private EditText input;
    private Button Bnsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_history);

        name = findViewById(R.id.spinner);
        steps = findViewById(R.id.number_of_steps);
        date = findViewById(R.id.date);
        input = findViewById(R.id.input);
        Bnsave = findViewById(R.id.save_button);

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_HISTORY_ID)){
            actionBar.setTitle("Edit Activity");
            date.setText(intent.getStringExtra(EXTRA_DATE));
            //name.setSelection(intent.getStringExtra(EXTRA_GOAL_NAME));
            steps.setText(String.valueOf(intent.getIntExtra(EXTRA_STEPS, 1000)));
            input.setText(String.valueOf(intent.getIntExtra(EXTRA_RECORD, 1000)));
        }else {
            actionBar.setTitle("Add new Activity");
        }

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String setDate = year + "-" + month + "-" + dayOfMonth;
                date.setText(setDate);
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditHistory.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        Bnsave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveHistory();
            }
        });

        historyViewModel.getAllGoals().observe(this, new Observer<List<Goals>>() {
            @Override
            public void onChanged(final List<Goals> goals) {
                BaseAdapter goalNameAdapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return goals.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return goals.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        GoalHolder holder;
                        View goalView = convertView;
                        if (goalView == null){
                            goalView = getLayoutInflater().inflate(R.layout.goal_spinner, parent, false);

                            holder = new GoalHolder();
                            holder.goalName = goalView.findViewById(R.id.goal_name);
                            goalView.setTag(holder);
                        }else {
                            holder = (GoalHolder) goalView.getTag();
                        }

                        Goals goal = goals.get(position);
                        holder.goalName.setText(goal.getGoal_name());

                        return goalView;
                    }

                    class GoalHolder{
                        private TextView goalName;
                    }
                };
                name.setAdapter(goalNameAdapter);
                name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        steps.setText(String.valueOf(goals.get(position).getNumber_of_steps()));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

    }

    private void saveHistory(){
        final String date_input = date.getText().toString();
        final String goal_name = name.getSelectedItem().toString();
        final int stepsNo = Integer.parseInt(steps.getText().toString());
        String inputValue = input.getText().toString();
        if (!inputValue.equals("")) {
            final int record = Integer.parseInt(inputValue);

            if (date_input.trim().isEmpty() || record == 0 || input.getText().toString() == "") {
                Toast.makeText(this, "Please input a goal name and number of steps taken", Toast.LENGTH_SHORT).show();
            }

            historyViewModel.ifDateExists(date_input, getIntent().getIntExtra(EXTRA_HISTORY_ID, 0)).observe(this, new Observer<List<CurrentGoal>>() {
                @Override
                public void onChanged(List<CurrentGoal> goals) {
                    if (!goals.isEmpty()) {
                        Toast.makeText(AddEditHistory.this, "Activity for this date already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent data = new Intent();

                        data.putExtra(EXTRA_DATE, date_input);
                        data.putExtra(EXTRA_GOAL_NAME, goal_name);
                        data.putExtra(EXTRA_STEPS, stepsNo);
                        data.putExtra(EXTRA_RECORD, record);

                        int id = getIntent().getIntExtra(EXTRA_HISTORY_ID, -1);
                        if (id != -1) {
                            data.putExtra(EXTRA_HISTORY_ID, id);
                        }

                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            });
        }
    }
}
