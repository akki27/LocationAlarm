package com.akki.locationalarm.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.akki.locationalarm.R;
import com.akki.locationalarm.db.AppDatabase;
import com.akki.locationalarm.services.LocationFeedService;
import com.akki.locationalarm.utils.AppConstants;
import com.akki.locationalarm.utils.AppPreferences;
import com.akki.locationalarm.utils.AppUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

/*
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * This class show the triggered alarm
* TODO: 1. How to set vibration with alarm, 2. How to play the saved Alarm ringtone not the device's current ringtone.
* */
public class AlarmReceiverActivity extends AppCompatActivity {
    private static final String TAG = AlarmReceiverActivity.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private CoordinatorLayout mAlarmViewLayout;
    private TextView mTvAlarmName, mAlarmDescription;

    private boolean mIsAlarmRepeat = false;
    private boolean mIsAlarmVibrate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm_receiver);

        mAlarmViewLayout = (CoordinatorLayout) findViewById(R.id.layout_receive_alarm);


        String alarmTitle = getIntent().getStringExtra(AppConstants.ALARM_TITLE_KEY);
        String alarmDescription = getIntent().getStringExtra(AppConstants.ALARM_DESCRIPTION_KEY);
        String alarmRingtone = getIntent().getStringExtra(AppConstants.ALARM_RINGTONE_KEY);

        mIsAlarmRepeat = getIntent() != null
                && getIntent().getStringExtra(AppConstants.ALARM_ISREPEAT_KEY).equalsIgnoreCase("YES");
        mIsAlarmVibrate = getIntent() != null
                && getIntent().getStringExtra(AppConstants.ALARM_ISVIBRATE_KEY).equalsIgnoreCase("YES");

        mTvAlarmName = (TextView) findViewById(R.id.tv_alarm_name);
        mAlarmDescription = (TextView) findViewById(R.id.tv_alarm_Description);

        mTvAlarmName.setText(alarmTitle);
        mAlarmDescription.setText(alarmDescription);

        Button stopAlarm = (Button) findViewById(R.id.btn_stop_alarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                //finish();
                if(!mIsAlarmRepeat) {
                    toggleAlarmStatus();
                } else {
                    snoozeAlarm();
                }
            }
        });

        playSound(this, getAlarmUri(alarmRingtone));

    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            AppUtils.showErrorMessage(mAlarmViewLayout, "OOPS...Something wen wrong!!", android.R.color.holo_red_light);
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    /* TODO: How to set RingTone, Vibrate */
    private Uri getAlarmUri(String alarmRingTone) {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private void snoozeAlarm() {
        AppPreferences.saveAlarmTriggerTimeStamp(AlarmReceiverActivity.this, new Date(System.currentTimeMillis()));

        //TODO: Its a hack need to user work manager so that service do not gets killed by system
        Intent serviceIntent = new Intent(this, LocationFeedService.class);
        startService(serviceIntent);
        finish();
    }

    private void toggleAlarmStatus() {
        AppDatabase appDatabase = AppDatabase.getDataBase(this.getApplicationContext());
        new toggleAlarmAsync(this, appDatabase, mTvAlarmName.getText().toString(), false).execute();
    }

    private static class toggleAlarmAsync extends android.os.AsyncTask<String, Void, Void> {
        private WeakReference<AlarmReceiverActivity> activityReference;
        private AppDatabase db;
        private String mAlarmName;
        private boolean mAlarmStatus;

        toggleAlarmAsync(AlarmReceiverActivity context, AppDatabase appDatabase, String alarmName, boolean alarmStatus) {
            activityReference = new WeakReference<>(context);
            db = appDatabase;
            mAlarmName = alarmName;
            mAlarmStatus = alarmStatus;
        }

        @Override
        protected Void doInBackground(final String... params) {
            db.alarmDataModel().updateAlarmStatusByTitle(mAlarmName, mAlarmStatus);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Alarm Status Changed..Finishing the alarm activity!");
            if(activityReference.get() != null)
            activityReference.get().finish();

            AlarmReceiverActivity activity = activityReference.get();
            if (activity != null)
                activity.finish();

        }
    }
}
