package com.akki.locationalarm.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akki.locationalarm.R;
import com.akki.locationalarm.adapter.AlarmListViewAdapter;
import com.akki.locationalarm.db.AlarmItemModel;
import com.akki.locationalarm.db.AlarmViewModel;
import com.akki.locationalarm.models.LocationHistoryResponse;
import com.akki.locationalarm.networking.APIClient;
import com.akki.locationalarm.networking.APIInterface;
import com.akki.locationalarm.services.LocationFeedService;
import com.akki.locationalarm.utils.AppConstants;
import com.akki.locationalarm.utils.AppPreferences;
import com.akki.locationalarm.utils.AppUtils;
import com.akki.locationalarm.utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * A Alarm list screen that have following features:
 * 1. List of saved alarm, if any:
 *    Features available for the saved alarms:
 *    (i) Alarm name, Alarm Description, Toggle button (ON/OFF)
 *    (ii) Delete/Restore: Swipe the alarm item left to delete the alarm. Click on "UNDO" button to restore the deleted alarm
 * 2. New Alarm: Open new screen and provide options to add a new alarm
 * 3. Location History: Open new screen and show device location history data. [API3]
 */
public class AlarmActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = AlarmActivity.class.getSimpleName();

    private Button mCreateNewAlarmBtn, mShowLocationHistory;
    private TextView mTvNoSavedAlarm;

    private AlarmViewModel mViewModel;
    private List<AlarmItemModel> mAlarmItemModelList;
    private AlarmListViewAdapter alarmListViewAdapter;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        setToolbar();
        initViews();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.txt_saved_alarm));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        mTvNoSavedAlarm = (TextView) findViewById(R.id.tv_no_saved_alarm);
        mCreateNewAlarmBtn = (Button) findViewById(R.id.btn_create_new_alarm);
        mCreateNewAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open screen to create a new alarm
                startActivity(new Intent(AlarmActivity.this, AddNewAlarmActivity.class));
            }
        });

        mShowLocationHistory = (Button) findViewById(R.id.btn_location_history);
        mShowLocationHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open LocationHistory screen
                showLocationHistory();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mProgressView = findViewById(R.id.api_progress);

        mAlarmItemModelList = new ArrayList<>();
        alarmListViewAdapter = new AlarmListViewAdapter(this, mAlarmItemModelList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(alarmListViewAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setAdapter(alarmListViewAdapter);

        // adding item touch helper only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        populateListViewFromDb();
    }

    private void populateListViewFromDb() {
        mViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        mViewModel.getSavedAlarmList().observe(AlarmActivity.this, new Observer<List<AlarmItemModel>>() {
            @Override
            public void onChanged(@Nullable List<AlarmItemModel> alarmListItem) {
                alarmListViewAdapter.addItems(alarmListItem);
                mAlarmItemModelList.clear();
                mAlarmItemModelList.addAll(alarmListItem);
                Log.d(TAG, "onChanged(): " +alarmListItem.size() + "::" +alarmListItem.size());

                if(mAlarmItemModelList != null && mAlarmItemModelList.size() > 0) {
                    mTvNoSavedAlarm.setVisibility(View.GONE);
                } else {
                    mTvNoSavedAlarm.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Log.d(TAG, "onSwiped()_mAlarmItemModelListSize: " +mAlarmItemModelList.size());

        if (viewHolder instanceof AlarmListViewAdapter.ItemRecyclerViewHolder) {
            // get the removed item name to display it in snack bar
            String alarmTitle = mAlarmItemModelList.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final AlarmItemModel deletedAlarm = mAlarmItemModelList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            alarmListViewAdapter.removeItem(viewHolder.getAdapterPosition(), mViewModel, mRecyclerView);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, alarmTitle + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    alarmListViewAdapter.restoreItem(deletedAlarm, deletedIndex, mViewModel);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    /**
     * Show Device Location history
      * */
    private void showLocationHistory() {
        if(AppUtils.isNetworkAvailable(this)) {
            showProgress(true);

            String loginToken = AppPreferences.getLoginToken(this);
            String authorizationHeaderStr = "Bearer " + loginToken;
            String contentTypeHeader = "application/json";
            String acceptHeader = "application/json";
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<LocationHistoryResponse> call = apiInterface.locationHistory(authorizationHeaderStr, contentTypeHeader, acceptHeader);

            call.enqueue(new Callback<LocationHistoryResponse>() {
                @Override
                public void onResponse(Call<LocationHistoryResponse> call, Response<LocationHistoryResponse> response) {
                    Log.d(TAG, "LocationHistory_onResponse");
                    showProgress(false);

                    if(response.body().getStatus().getCode() == 200) {
                        LocationHistoryResponse locationHistoryResponse = (LocationHistoryResponse)response.body();
                        Intent intent = new Intent(AlarmActivity.this, LocationHistoryActivity.class);
                        intent.putExtra(AppConstants.LOCATION_HISTORY_RESULT, locationHistoryResponse);
                        startActivity(intent);
                    } else {
                        AppUtils.showErrorMessage(mCoordinatorLayout, response.body().getStatus().getMessage(), android.R.color.holo_red_light);
                    }
                }

                @Override
                public void onFailure(Call<LocationHistoryResponse> call, Throwable t) {
                    Log.d(TAG, "LocationHistory_onFailure");
                    showProgress(false);
                    AppUtils.showErrorMessage(mCoordinatorLayout, t.getCause().getMessage(), android.R.color.holo_red_light);
                }
            });
        } else {
            AppUtils.showErrorMessage(mCoordinatorLayout, getString(R.string.network_unavailable), android.R.color.holo_red_light);
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* TODO: Requesting LocationFeedback service */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //AppUtils.startSessionTimerService(this);
        startLocationFeedService();
    }

    /* Start device location feed service */
    private void startLocationFeedService() {
        Intent serviceIntent = new Intent(this, LocationFeedService.class);
        startService(serviceIntent);
    }
}
