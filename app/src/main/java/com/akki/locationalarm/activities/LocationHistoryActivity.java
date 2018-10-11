package com.akki.locationalarm.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.akki.locationalarm.R;
import com.akki.locationalarm.adapter.LocationHistoryAdapter;
import com.akki.locationalarm.models.LocationHistoryResponse;
import com.akki.locationalarm.models.LocationHistoryResult;
import com.akki.locationalarm.utils.AppConstants;

import java.util.List;

/*
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * This class showing device location history
* */
public class LocationHistoryActivity extends AppCompatActivity {
    private static final String TAG = LocationHistoryActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mTvNoSavedAlarm;
    private LocationHistoryResponse locationHistoryResponse;
    private List<LocationHistoryResult> mLocationHistoryList;
    private LocationHistoryAdapter locationHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);

        setToolbar();
        initViews();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.txt_user_location_history));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click: For now finishing the activity
                onBackPressed();
            }
        });
    }

    private void initViews() {
        mTvNoSavedAlarm = (TextView) findViewById(R.id.tv_no_saved_alarm);
        mRecyclerView = findViewById(R.id.recycler_view);
        locationHistoryResponse = (LocationHistoryResponse) getIntent().getSerializableExtra(AppConstants.LOCATION_HISTORY_RESULT);
        mLocationHistoryList = locationHistoryResponse.getResult();

        locationHistoryAdapter = new LocationHistoryAdapter(this, mLocationHistoryList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(locationHistoryAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setAdapter(locationHistoryAdapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
