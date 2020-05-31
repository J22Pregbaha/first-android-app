package com.example.keepfit;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class KeepFitApp extends Application {

    public static final String CHANNEL_1_ID = "Channel for percentage completed";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel for displaying percentage completed",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is the channel for displaying percentage completed");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
