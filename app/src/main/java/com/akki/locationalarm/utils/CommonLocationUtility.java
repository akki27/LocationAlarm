package com.akki.locationalarm.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.akki.locationalarm.interfaces.CurrentLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * * Created by v-akhilesh.chaudhary on 07/10/2018.
 */
public class CommonLocationUtility implements Cloneable {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Context mContext;
    private static CommonLocationUtility instance = null;
    private CurrentLocationListener mCurrentLocationListener;
    private Activity mActivity;


    private CommonLocationUtility() {
    }

    public synchronized static CommonLocationUtility getCommonLocationUtility() {
        if (instance == null) {
            instance = new CommonLocationUtility();
        }
        return instance;
    }

    public void setCurrentLocationListener(Context mContext, CurrentLocationListener mCurrentLocationListener) {
        this.mContext = mContext;
        this.mCurrentLocationListener = mCurrentLocationListener;
        this.mActivity = (Activity) mContext;
        checkPermissions();
    }

    private void initialize() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
//        mSettingsClient = LocationServices.getSettingsClient(mContext);
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//        updatingCurrentLocation();
    }

    private void checkPermissions() {
        if (checkGooglePlayServicesAvailable()) {
            initialize();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdate();
                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.REQUEST_CURRENT_LOCATION_PERMISSION_CODE);
                }
            } else {
                startLocationUpdate();
            }
        }
    }

    private boolean checkGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(mContext);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(mActivity, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    private void startLocationUpdate() {
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                    setCurrentLocation();
                }
            }
        });
    }

    private void setCurrentLocation() {
        if (mCurrentLocationListener != null) {
            mCurrentLocationListener.onCurrentLocationReceived(mCurrentLocation);
        }
    }

    public void setOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_CURRENT_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (mCurrentLocationListener != null) {
                    mCurrentLocationListener.onPermissionDenied();
                }
                permissionDenied(requestCode, permissions);
            }
        }
    }

    private void permissionDenied(int requestCode, String permissions[]) {
        if (requestCode == AppConstants.REQUEST_CURRENT_LOCATION_PERMISSION_CODE) {
            if (permissions == null || permissions.length == 0) {
                return;
            }
            if (Build.VERSION.SDK_INT > 22) {
                boolean showRationale = mActivity.shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale) {
                    showDialogWhenDenyWithNeverAskLocationPermission(mContext);
                }
            }
        }

    }

    public void showDialogWhenDenyWithNeverAskLocationPermission(final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Enable Location Services");
        builder.setMessage("Please enable location permission to get the near of your location.");
        builder.setPositiveButton("ENABLE ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                ((AppCompatActivity) mContext).startActivityForResult(intent, AppConstants.REQUEST_CURRENT_LOCATION_PERMISSION_CODE);
            }
        });
        builder.create();
        if (!mActivity.isFinishing()) {
            builder.show();
        }
    }


    private void updatingCurrentLocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> task = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);

        task.addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdate();
            }
        });

        task.addOnFailureListener(mActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException("Clone is not supported here");
    }
}
