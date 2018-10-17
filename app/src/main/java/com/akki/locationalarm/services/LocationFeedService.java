package com.akki.locationalarm.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.akki.locationalarm.R;
import com.akki.locationalarm.activities.AlarmActivity;
import com.akki.locationalarm.activities.AlarmReceiverActivity;
import com.akki.locationalarm.db.AlarmItemModel;
import com.akki.locationalarm.db.AppDatabase;
import com.akki.locationalarm.models.LocationData;
import com.akki.locationalarm.models.LocationSaveResponse;
import com.akki.locationalarm.networking.APIClient;
import com.akki.locationalarm.networking.APIInterface;
import com.akki.locationalarm.utils.AppConstants;
import com.akki.locationalarm.utils.AppPreferences;
import com.akki.locationalarm.utils.AppUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

/*
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * Location Feed service: This service will keep updating/syncing the device location to the server [API2].
* NOTE: API2 is not working for now as server returning "wrong request" and "Un-Authorized" error.
* Same is getting from Google's Postman. Need to re-check this.
* This class also compare the current device location with the saved alarm location. If matched then it trigger the alarm.
* TODO: Use WorkManager for this to optimize the task.
* */
public class LocationFeedService extends Service
{
    private static final String TAG = LocationFeedService.class.getSimpleName();
    private LocationManager mLocationManager = null;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            saveLocationToServer(mLastLocation.getAltitude(), mLastLocation.getLongitude());
            notifyAlarm(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        //super.onStartCommand(intent, flags, startId);

        showNotification();

        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        notificationIntent.setAction(AppConstants.ALARM_ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentTitle("LocationAlarm App")
                .setContentText("Doing some work...")
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(AppConstants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "channel_id";
        String channelName = "Location Alarm";
        // The user-visible description of the channel.
        String description = "Location Alarm Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // Configure the notification channel.
        channel.setDescription(description);
        channel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, AppConstants.LOCATION_INTERVAL, AppConstants.LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, AppConstants.LOCATION_INTERVAL, AppConstants.LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /* TODO: Wrong request error: need to fix ==> Request is Json array (key??) not object */
    private void saveLocationToServer(double latitude, double longitude) {
        String loginToken = AppPreferences.getLoginToken(this);
        String authorizationHeaderStr = "Bearer " + loginToken;

        Log.d(TAG, "LoginToken: " +loginToken + ":\nWithBearer: " +authorizationHeaderStr);
        String contentTypeHeader = "application/json";
        String acceptHeader = "application/json";

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        List<LocationData> locationDataList = new ArrayList<LocationData>();
        locationDataList.add(new LocationData(String.valueOf(latitude), String.valueOf(longitude)));
        Call<LocationSaveResponse> call = apiInterface.saveLocation(authorizationHeaderStr, contentTypeHeader,
                acceptHeader, locationDataList);

        call.enqueue(new Callback<LocationSaveResponse>() {
            @Override
            public void onResponse(Call<LocationSaveResponse> call, Response<LocationSaveResponse> response) {
                Log.d(TAG, "SaveLocation_onResponse: " +response.message() + ": " +response.body().getResult());
            }

            @Override
            public void onFailure(Call<LocationSaveResponse> call, Throwable t) {
                Log.d(TAG, "SaveLocation_onFailure: " +t.getMessage() + "::Cause: " +t.getCause());
            }
        });

    }

    /*
     * Read Saved Alarms from from DB and trigger the alarm for which current Lat & Long matches with its saved Lat & Long
     * */
    private void notifyAlarm(Location location) {
        AppDatabase appDatabase = AppDatabase.getDataBase(this.getApplicationContext());
        new fetchAlarmAsync(this, appDatabase, location).execute();

    }

    private static class fetchAlarmAsync extends android.os.AsyncTask<String, Void, List<AlarmItemModel>> {
        private WeakReference<LocationFeedService> activityReference;
        private AppDatabase db;
        private List<AlarmItemModel> savedAlarmList;
        private Location mLastKnownLocation;

        fetchAlarmAsync(LocationFeedService context, AppDatabase appDatabase, Location lastLocation) {
            activityReference = new WeakReference<>(context);
            db = appDatabase;
            mLastKnownLocation = lastLocation;

            savedAlarmList = new ArrayList<>();
        }

        @Override
        protected List<AlarmItemModel> doInBackground(final String... params) {
            savedAlarmList = db.alarmDataModel().getAllSavedAlarmList();
            return savedAlarmList;
        }

        @Override
        protected void onPostExecute(List<AlarmItemModel> savedAlarmList) {
            super.onPostExecute(savedAlarmList);
            activityReference.get().triggerAlarm(mLastKnownLocation, savedAlarmList);

        }
    }

    /* Trigger alarm if there is any saved alarm for which
    * 1. Alarm status is ON
    * 2. Device current location reached within the 100 meter of the proximity of the saved Alarm location.
    * 3. TODO: Handle multiple alarm at the same location:?? Do not Allow to save new alarm if one with the same lat, long, Alt already saved.
     * */
    private void triggerAlarm(Location lastLocation, List<AlarmItemModel> savedAlarmList) {
        Log.d(TAG, "savedAlarmListSize: " + savedAlarmList.size());
        AlarmItemModel alarmItemModel = null;
        if(savedAlarmList.size() > 0) {
            for(int i = 0; i < savedAlarmList.size(); i++) {
                /*double distanceDiff = AppUtils.geoDistanceBetweenTwoLocation(lastLocation.getAltitude(), Double.parseDouble(savedAlarmList.get(i).getLocationLatitude()),
                        lastLocation.getLongitude(), Double.parseDouble(savedAlarmList.get(i).getLocationLongitude()),
                        lastLocation.getAltitude(), Double.parseDouble(savedAlarmList.get(i).getLocationAltitude()));
                Log.d(TAG, "AlarmLat: " +Double.parseDouble(savedAlarmList.get(i).getLocationLatitude())
                        +"\nAlarmLong: " +Double.parseDouble(savedAlarmList.get(i).getLocationLongitude())
                        + "\nAlarmAlti: " +Double.parseDouble(savedAlarmList.get(i).getLocationAltitude())
                        + "\nLocationDifference: " +distanceDiff);

                if((int) Math.round(distanceDiff) <= 100*1000000) {
                    Log.d(TAG, "Trigger this alarm now");
                    alarmItemModel = new AlarmItemModel(savedAlarmList.get(i).getTitle(),
                            savedAlarmList.get(i).getAlarmDescription(), savedAlarmList.get(i).getLocationLatitude(),
                            savedAlarmList.get(i).getLocationLongitude(),savedAlarmList.get(i).getLocationAltitude(),
                            savedAlarmList.get(i).getAlarmRingTone(), savedAlarmList.get(i).isRepeat(),
                            savedAlarmList.get(i).getRepeatInterval(), savedAlarmList.get(i).isVibrate(),
                            savedAlarmList.get(i).isAlarmOn());
                    break;
                }*/

                Location targetLocation = new Location(LocationManager.GPS_PROVIDER);//provider name is unnecessary
                targetLocation.setLatitude(Double.parseDouble(savedAlarmList.get(i).getLocationLatitude()));//your coords of course
                targetLocation.setLongitude(Double.parseDouble(savedAlarmList.get(i).getLocationLongitude()));
                Log.d(TAG, "LocationDiff: " +AppUtils.getDistanceBetween(lastLocation, targetLocation));
                if(AppUtils.getDistanceBetween(lastLocation, targetLocation) <= 100) {
                    Log.d(TAG, "Trigger this alarm now");
                    alarmItemModel = new AlarmItemModel(savedAlarmList.get(i).getTitle(),
                            savedAlarmList.get(i).getAlarmDescription(), savedAlarmList.get(i).getLocationLatitude(),
                            savedAlarmList.get(i).getLocationLongitude(),savedAlarmList.get(i).getLocationAltitude(),
                            savedAlarmList.get(i).getAlarmRingTone(), savedAlarmList.get(i).isRepeat(),
                            savedAlarmList.get(i).getRepeatInterval(), savedAlarmList.get(i).isVibrate(),
                            savedAlarmList.get(i).isAlarmOn());
                    break;
                }
            }
        }

        if(alarmItemModel != null && alarmItemModel.isAlarmOn()
                && AppUtils.isAlarmThresholdTimeElapsed(getApplicationContext(), alarmItemModel.getRepeatInterval())) {
            playAlarm(alarmItemModel);
        }
    }

    /* Open Alarm receiver screen */
    public void playAlarm(AlarmItemModel alarmItemModel) {
        Log.d(TAG, "Play Alarm for: " +alarmItemModel.getTitle());
        try {
            Intent intent = new Intent(getApplicationContext(), AlarmReceiverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AppConstants.ALARM_TITLE_KEY, alarmItemModel.getTitle());
            intent.putExtra(AppConstants.ALARM_DESCRIPTION_KEY, alarmItemModel.getAlarmDescription());
            intent.putExtra(AppConstants.ALARM_RINGTONE_KEY, alarmItemModel.getAlarmRingTone());
            intent.putExtra(AppConstants.ALARM_ISREPEAT_KEY, alarmItemModel.isRepeat());
            intent.putExtra(AppConstants.ALARM_ISVIBRATE_KEY, alarmItemModel.isVibrate());
            startActivity(intent);

            //Create an offset from the current time in which the alarm will go off.
            /*Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 1);

            //Create a new PendingIntent and add it to the AlarmManager
            Intent intent = new Intent(this, AlarmReceiverActivity.class);
            intent.putExtra(AppConstants.ALARM_TITLE_KEY, alarmItemModel.getTitle());
            intent.putExtra(AppConstants.ALARM_DESCRIPTION_KEY, alarmItemModel.getAlarmDescription());
            intent.putExtra(AppConstants.ALARM_RINGTONE_KEY, alarmItemModel.getAlarmRingTone());
            intent.putExtra(AppConstants.ALARM_ISREPEAT_KEY, alarmItemModel.isRepeat());
            intent.putExtra(AppConstants.ALARM_ISVIBRATE_KEY, alarmItemModel.isVibrate());
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am =
                    (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    pendingIntent);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
