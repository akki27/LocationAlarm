package com.akki.locationalarm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.akki.locationalarm.activities.LoginActivity;
import com.akki.locationalarm.utils.AppConstants;
import com.akki.locationalarm.utils.AppUtils;

import androidx.work.Worker;

public class AlarmTimerService extends Worker {
    private static final String TAG = AlarmTimerService.class.getSimpleName();

    //private static final int MAX_TIME = 5 * 60 * 1000;
    private int TIMEOUT_INTERVAL = 0;
    private static final String TIMER_ACTION = "com.yatra.SessionTimer.TIMER";
    private static String ANDROID_CHANNEL_ID = "session_timer";
    Handler handler;
    CountDownTimer countDownTimer;
    Runnable timer;
    Context context;
    HandlerThread handlerThread;


    private void startTimer() {

        timer = new Runnable() {
            @Override
            public void run() {
                try {
                    countDownTimer = new CountDownTimer((long)TIMEOUT_INTERVAL, 1000) {

                        public void onTick(long millisUntilFinished) {
                            Intent intent = new Intent();
                            intent.setAction(TIMER_ACTION);
                            intent.putExtra("STATE", AppConstants.SessionTimerState.STARTED.ordinal());
                            intent.putExtra("TIME", millisUntilFinished);
                            getApplicationContext().sendBroadcast(intent);
                        }

                        public void onFinish() {
                            Log.d(TAG, "Finishing timer!");
                            Intent intent = new Intent();
                            intent.setAction(TIMER_ACTION);
                            intent.putExtra("STATE", AppConstants.SessionTimerState.STOPPED.ordinal());
                            AppUtils.setTimerState(AppConstants.SessionTimerState.STOPPED.ordinal(), getApplicationContext());
                            getApplicationContext().sendBroadcast(intent);
                            //stopSelf();
                        }
                    }.start();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        if (countDownTimer != null)
            countDownTimer.cancel();
        handler.removeCallbacks(timer);
        handler.post(timer);

    }


    @NonNull
    @Override
    public Worker.Result doWork() {
        handlerThread = new HandlerThread("Background_Thread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        handler = new Handler(looper);
        TIMEOUT_INTERVAL = getInputData().getInt("timeout", 0)* 60 * 1000;
        startTimer();
        return Worker.Result.SUCCESS;
    }

    @Override
    public void onStopped(boolean cancelled) {
        Log.v("in onStopped", "in onStopped");
        super.onStopped(cancelled);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacks(timer);
        handlerThread.quit();
    }

    /*@Override
    public void onWorkFinished(@NonNull Worker.Result result) {
        Log.v("in onworkfinished", "in onworkfinished");
        // super.onWorkFinished(result);
    }*/
}
