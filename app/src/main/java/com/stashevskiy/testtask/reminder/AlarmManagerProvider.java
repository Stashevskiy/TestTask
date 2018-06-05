package com.stashevskiy.testtask.reminder;


import android.app.AlarmManager;
import android.content.Context;

public class AlarmManagerProvider {


    private static AlarmManager sAlarmManager;

    public static synchronized void injectAlarmManager(AlarmManager alarmManager) {
        if (sAlarmManager != null) {
            throw new IllegalStateException("Alarm Manager уже установлен");
        }
        sAlarmManager = alarmManager;
    }
    /*package*/ static synchronized AlarmManager getAlarmManager(Context context) {
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return sAlarmManager;
    }
}