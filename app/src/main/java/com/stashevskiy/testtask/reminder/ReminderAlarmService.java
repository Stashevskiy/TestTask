package com.stashevskiy.testtask.reminder;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.stashevskiy.testtask.ElementsActivity;
import com.stashevskiy.testtask.R;
import com.stashevskiy.testtask.data.ElementContract;

public class ReminderAlarmService extends IntentService {

    private static final String TAG = ReminderAlarmService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;

    Cursor cursor;

    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, ReminderAlarmService.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderAlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = intent.getData();


        Intent action = new Intent(this, ElementsActivity.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        if(uri != null){
            cursor = getContentResolver().query(uri, null, null, null, null);
        }


        String description = "Напоминание";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                description = cursor.getString(cursor.getColumnIndex(ElementContract.ElementEntry._ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }



        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle("Уведомление")
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentIntent(operation)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }
}
