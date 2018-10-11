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

}
