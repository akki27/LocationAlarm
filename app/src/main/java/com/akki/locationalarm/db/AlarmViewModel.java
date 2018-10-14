package com.akki.locationalarm.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by v-akhilesh.chaudhary on 06/10/2018.
 */

public class AlarmViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    private final LiveData<List<AlarmItemModel>> mSavedAlarmList;

    public AlarmViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDataBase(this.getApplication());
        mSavedAlarmList = appDatabase.alarmDataModel().getAllSavedAlarm();
    }

    public LiveData<List<AlarmItemModel>> getSavedAlarmList() {
        return mSavedAlarmList;
    }

    public void addAlarm(final AlarmItemModel alarmItemModel) {
        new AlarmViewModel.addAsyncTask(appDatabase).execute(alarmItemModel);
    }

    private static class addAsyncTask extends AsyncTask<AlarmItemModel, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final AlarmItemModel... params) {
            db.alarmDataModel().saveAlarm(params[0]);
            return null;
        }
    }


    public void deleteAlarm(AlarmItemModel alarmItemModel) {
        new AlarmViewModel.deleteAsyncTask(appDatabase).execute(alarmItemModel);
    }

    private static class deleteAsyncTask extends AsyncTask<AlarmItemModel, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final AlarmItemModel... params) {
            db.alarmDataModel().deleteAlarm(params[0]);
            return null;
        }
    }

    public void updateAlarmStatus(AlarmItemModel alarmItemModel) {
        new AlarmViewModel.UpdateAsyncTask(appDatabase).execute(alarmItemModel);
    }

    private static class UpdateAsyncTask extends AsyncTask<AlarmItemModel, Void, Void> {

        private AppDatabase db;

        UpdateAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final AlarmItemModel... params) {
            db.alarmDataModel().updateAlarmStatus(String.valueOf(params[0].getId()), params[0].isAlarmOn());
            return null;
        }
    }

    /* Update Alarm data AsyncTask class */
    public void updateAlarm(AlarmItemModel alarmItemModel, String savedAlarmName) {
        new AlarmViewModel.updateAlarmAsyncTask(appDatabase, savedAlarmName).execute(alarmItemModel);
    }
    private static class updateAlarmAsyncTask extends android.os.AsyncTask<AlarmItemModel, Void, Void> {

        private AppDatabase db;
        private String mSavedAlarmName;

        updateAlarmAsyncTask(AppDatabase appDatabase, String savedAlarmName) {
            db = appDatabase;
            mSavedAlarmName = savedAlarmName;
        }

        @Override
        protected Void doInBackground(final AlarmItemModel... params) {
            db.alarmDataModel().updateAlarm(String.valueOf(params[0].getId()), String.valueOf(params[0].getTitle()),
                    String.valueOf(params[0].getAlarmDescription()), String.valueOf(params[0].getLocationLatitude()),
                    String.valueOf(params[0].getLocationLongitude()), String.valueOf(params[0].getLocationAltitude()),
                    String.valueOf(params[0].getAlarmRingTone()), String.valueOf(params[0].isRepeat()),
                    String.valueOf(params[0].getRepeatInterval()), String.valueOf(params[0].isVibrate()),
                    params[0].isAlarmOn(), mSavedAlarmName);

            return null;
        }
    }

}
