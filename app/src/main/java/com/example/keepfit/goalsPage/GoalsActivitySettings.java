package com.example.keepfit.goalsPage;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepfit.R;
import com.example.keepfit.homePage.SettingsFragment;

public class GoalsActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");

        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null)
                return;

            getFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();
        }
    }
}
