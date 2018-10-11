package com.akki.locationalarm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akki.locationalarm.R;
import com.akki.locationalarm.models.LocationHistoryResult;

import java.util.List;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.ItemRecyclerViewHolder> {

    private final String TAG = LocationHistoryAdapter.class.getName();

    private Context mContext;
    private List<LocationHistoryResult> mLocationHistoryList;

    public class ItemRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView mLocationId, mLocationLatitude, mLocationLongitude;

        ItemRecyclerViewHolder(View view) {
            super(view);
            mLocationId = view.findViewById(R.id.tv_location_id);
            mLocationLatitude = view.findViewById(R.id.tv_location_latitude);
            mLocationLongitude = view.findViewById(R.id.tv_location_longitude);
        }
    }

    public LocationHistoryAdapter(Context context, List<LocationHistoryResult> locationHistoryList) {
        this.mContext = context;
        this.mLocationHistoryList = locationHistoryList;

    }

    @Override
    public LocationHistoryAdapter.ItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_history_row, parent, false);

        return new LocationHistoryAdapter.ItemRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LocationHistoryAdapter.ItemRecyclerViewHolder holder, final int position) {

        final LocationHistoryResult locationHistoryResult = mLocationHistoryList.get(position);

        holder.mLocationId.setText(String.valueOf(mLocationHistoryList.get(position).getLocationId()));
        holder.mLocationLatitude.setText(mLocationHistoryList.get(position).getLatitude());
        holder.mLocationLongitude.setText(mLocationHistoryList.get(position).getLongitude());

        holder.itemView.setTag(locationHistoryResult);

    }

    @Override
    public int getItemCount() {
        return mLocationHistoryList.size();
    }
}
