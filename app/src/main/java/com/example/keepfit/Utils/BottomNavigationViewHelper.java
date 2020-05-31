package com.example.keepfit.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.keepfit.homePage.MainActivity;
import com.example.keepfit.R;
import com.example.keepfit.historyPage.History;
import com.example.keepfit.goalsPage.ViewGoals;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";
    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(context, MainActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;
                    case R.id.navigation_history:
                        Intent intent2 = new Intent(context, History.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;
                    case R.id.navigation_goals:
                        Intent intent3 = new Intent(context, ViewGoals.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
