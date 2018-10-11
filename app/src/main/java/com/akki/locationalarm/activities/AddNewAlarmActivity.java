package com.akki.locationalarm.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.akki.locationalarm.R;
import com.akki.locationalarm.db.AlarmItemModel;
import com.akki.locationalarm.db.AlarmViewModel;
import com.akki.locationalarm.utils.AppConstants;
import com.akki.locationalarm.utils.AppUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Locale;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * Add new alarm screen
 * For a new alarm below features need to select/set:
 * 1. Title/Name (Mandatory)
 * 2. Description (Optional)
 * 3. Location: Latitude, Longitude and Altitude (Mandatory)
 * 4. Ringtone (Mandatory)
 * 5. IsRepeat (Mandatory)
 * 6. Repeat Interval (Mandatory) ==> If isRepeat is "YES" show this field to user else hide
 * 7. IsVibrate (Mandatory)
 */
public class AddNewAlarmActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = AddNewAlarmActivity.class.getSimpleName();

    private EditText titleEditText;
    private EditText locationLatitude;
    private EditText locationLongitude;
    private EditText locationAltitude;
    private EditText alarmDescription;
    private EditText alarmRingTone;
    private Spinner spnrVibrate;
    private Spinner spnrRepeat;
    private RelativeLayout mRlRepeatInterval;
    private Spinner spnrRepeatInterval;
    private Button setLocation;
    private FloatingActionButton setAlarm;
    private AlarmViewModel alarmViewModel;

    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private Place mPlaceData;
    private CoordinatorLayout mAddNewAlarmLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_alarm);

        setToolbar();
        initViews();
        setDataProperties();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.txt_create_new_alarm));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        mAddNewAlarmLayout = (CoordinatorLayout) findViewById(R.id.layout_add_new_alarm);
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        titleEditText = findViewById(R.id.ed_alarm_title);
        alarmDescription = (EditText) findViewById(R.id.ed_alarm_description);
        locationLatitude = findViewById(R.id.ed_location_latitude);
        locationLongitude = findViewById(R.id.ed_location_longitude);
        locationAltitude = findViewById(R.id.ed_location_altitude);

        alarmRingTone = (EditText) findViewById(R.id.ed_ringtone);
        alarmRingTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(AddNewAlarmActivity.this, RingtoneManager.TYPE_ALARM);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for location alarm:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, AppConstants.RINGTONE_REQUEST_CODE);

            }
        });

        spnrVibrate = findViewById(R.id.spnr_vibrate);
        spnrRepeat = findViewById(R.id.spnr_repeat);

        mRlRepeatInterval = (RelativeLayout) findViewById(R.id.spinner_bg_alarm_repeat_interval);
        spnrRepeatInterval = (Spinner) findViewById(R.id.spnr_repeat_interval);

        spnrRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(parentView.getItemAtPosition(position).toString().equalsIgnoreCase("YES")) {
                    mRlRepeatInterval.setVisibility(View.VISIBLE);
                } else {
                    mRlRepeatInterval.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        setLocation = (Button) findViewById(R.id.btn_set_location);
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Open Map and get Location: Latitude & Longitude
                getLocationOnMap();
            }
        });

        setAlarm = findViewById(R.id.fab);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFeatureValidated()) {
                    alarmViewModel.addAlarm(new AlarmItemModel(titleEditText.getText().toString(),
                            alarmDescription.getText().toString(), locationLatitude.getText().toString(),
                            locationLongitude.getText().toString(), locationAltitude.getText().toString(),
                            alarmRingTone.getText().toString(), spnrRepeat.getSelectedItem().toString(),
                            spnrRepeatInterval.getSelectedItem().toString(), spnrVibrate.getSelectedItem().toString(),
                            true));
                    finish();
                }
            }
        });
    }

    /* Initialize Google Api client, LocationRequest, Permission Request, etc */
    private void setDataProperties() {
        // Add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), AppConstants.ALL_PERMISSIONS_RESULT);
            }
        }

        // Build google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two call tell the new client that “this” current class will handle connection stuff
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                //this adds the LocationServices API endpoint from GooglePlayServices
                .addOnConnectionFailedListener(this)
                .build();

        // Create the LocationRequest object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(AppConstants.FASTEST_INTERVAL);

    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStop fired ..............");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            //locationTv.setText("You need to install Google Play Services to use the App properly");
            locationLatitude.setText("No Google Play Service");
            locationLongitude.setText("No Google Play Service");
            locationAltitude.setText("NO Google Play service");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (mGoogleApiClient != null  &&  mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, AppConstants.PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            locationLatitude.setText(String.valueOf(mLocation.getLatitude()));
            locationLongitude.setText(String.valueOf(mLocation.getLongitude()));
            locationAltitude.setText(String.valueOf(mLocation.getLatitude()));
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        AppUtils.showErrorMessage(mAddNewAlarmLayout, connectionResult.getErrorMessage(), android.R.color.holo_red_light);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "OnLocationChanged");
        if (location != null) {
            mLocation = location;
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            locationLatitude.setText(String.valueOf(location.getLatitude()));
            locationLongitude.setText(String.valueOf(location.getLongitude()));
            locationAltitude.setText(String.valueOf(location.getAltitude()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case AppConstants.ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(AddNewAlarmActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), AppConstants.ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                        enableGps();
                    }
                }

                break;
        }

    }

    /* Validation for the required alarm features */
    private boolean isFeatureValidated() {
        boolean allFeatureValidated = true;
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(AddNewAlarmActivity.this, "Title Missing!", Toast.LENGTH_SHORT).show();
            allFeatureValidated = false;
        }
        if (locationLatitude.getText().toString().isEmpty() || locationLongitude.getText().toString().isEmpty()
                || locationAltitude.getText().toString().isEmpty()) {
            Toast.makeText(AddNewAlarmActivity.this, "Location Missing!", Toast.LENGTH_SHORT).show();
            allFeatureValidated = false;
        }
        if (alarmRingTone.getText().toString().isEmpty()) {
            Toast.makeText(AddNewAlarmActivity.this, "Ringtone Missing!", Toast.LENGTH_SHORT).show();
            allFeatureValidated = false;
        }
        if (spnrVibrate.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(AddNewAlarmActivity.this, "Vibrate Missing!", Toast.LENGTH_SHORT).show();
            allFeatureValidated = false;
        }
        if (spnrRepeat.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(AddNewAlarmActivity.this, "Repeat Missing!", Toast.LENGTH_SHORT).show();
            allFeatureValidated = false;
        }

        return allFeatureValidated;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AppConstants.RINGTONE_REQUEST_CODE && resultCode  == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                alarmRingTone.setText(ringtone.getTitle(this));
            } else if(requestCode == AppConstants.PLACE_PICKER_REQ_CODE && resultCode  == AppConstants.LOCATION_RESULT_CODE){
                mPlaceData = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", mPlaceData.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            } else if (requestCode == AppConstants.ENABLE_GPS_CODE) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(AddNewAlarmActivity.this, "Gps enabled", Toast.LENGTH_SHORT).show();
                        if (mGoogleApiClient != null) {
                            mGoogleApiClient.connect();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(AddNewAlarmActivity.this, "Gps Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(AddNewAlarmActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void enableGps() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    AddNewAlarmActivity.this, AppConstants.ENABLE_GPS_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /* TODO: Scenario: if we need to open map and select place for Lat/Long/Alt : Need to implemnent place Api in this case */
    private void getLocationOnMap() {
        //TODO: Get Place API key place in the manifest file under <application> tag as
        //<meta-data
        //   android:name="com.google.android.geo.API_KEY"
        //   android:value="API_KEY" />
        // Ref: https://developers.google.com/places/android-sdk/signup
        /*PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), AppConstants.PLACE_PICKER_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }*/

        if (mLocation != null) {
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", mLocation.getLongitude(), mLocation.getLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } else {
            AppUtils.showErrorMessage(mAddNewAlarmLayout, "Location Data Not Available! \nPlease Wait...", android.R.color.holo_red_light);
        }

    }
}