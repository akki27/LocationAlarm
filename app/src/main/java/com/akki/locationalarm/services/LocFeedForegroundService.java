package com.akki.locationalarm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocFeedForegroundService extends Service {
    public LocFeedForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
