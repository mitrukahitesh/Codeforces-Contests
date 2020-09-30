package com.hitesh.codeforces;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManagerCompat;
    public static final int ID = 1;
    private String TITLE = "Contest@ ";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContestAdapter.alarmsSet.remove(intent.getIntExtra("Id", -1));
                SQLiteDatabase db = new AlarmSQLiteHelper(context).getWritableDatabase();
                db.delete(AlarmSQLiteHelper.TABLE, AlarmSQLiteHelper.CONTEST_ID + " = " + intent.getIntExtra("Id", -1), null);
            }
        }).start();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis() + (long) (60 * 60 * 1000));
        String addition;
        if(!DateFormat.is24HourFormat(context)) {
            addition = (String) DateFormat.format("hh:mm a", c);
        } else {
            addition = (String) DateFormat.format("HH:mm", c);
        }
        TITLE = TITLE + addition;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        String url = "https://www.codeforces.com/contests/" + intent.getIntExtra("Id", -1);
        Intent intentInfo = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intentInfo, 0);
        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_1_ID)
                .setContentTitle(TITLE)
                .setContentText(intent.getStringExtra("name"))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .build();
        notificationManagerCompat.notify(1, notification);
    }
}
