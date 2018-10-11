package com.akki.locationalarm.interfaces;

import android.location.Location;

public interface CurrentLocationListener {
    void onCurrentLocationReceived(Location location);
    void onPermissionDenied();
}
