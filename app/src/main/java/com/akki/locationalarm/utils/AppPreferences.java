package com.akki.locationalarm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public class AppPreferences {

    public static void setLoginToken(Context context, String loginToken) {
        SharedPreferences mPrefs = context.getSharedPreferences(
                AppConstants.USER_SHARED_FILE_KEY, Context.MODE_PRIVATE);
        mPrefs.edit().putString(AppConstants.USER_LOGIN_TOKEN_KEY, loginToken)
                .apply();
    }

    public static String getLoginToken(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(
                AppConstants.USER_SHARED_FILE_KEY, Context.MODE_PRIVATE);
        return mPrefs.getString(AppConstants.USER_LOGIN_TOKEN_KEY, "");
    }

    public static void saveAlarmTriggerTimeStamp(Context context, Date date) {
        SharedPreferences mPrefs = context.getSharedPreferences(
                AppConstants.USER_SHARED_FILE_KEY, Context.MODE_PRIVATE);
        mPrefs.edit().putLong(AppConstants.ALARM_TRIGGER_TIME_KEY, date.getTime())
                .apply();
    }

    public static long getAlarmTriggerTimeStamp(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(
                AppConstants.USER_SHARED_FILE_KEY, Context.MODE_PRIVATE);
        return mPrefs.getLong(AppConstants.ALARM_TRIGGER_TIME_KEY, 0L);
    }
}
