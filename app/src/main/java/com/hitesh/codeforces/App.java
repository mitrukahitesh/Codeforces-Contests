package com.hitesh.codeforces;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {
    public static final String CHANNEL_1_ID = "reminder";
    public static final String CHanNEL_1_NAME = "Reminder";
    public static final String DESCRIPTION = "Codeforces Contest";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_1_ID, CHanNEL_1_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(DESCRIPTION);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }
    }
}
