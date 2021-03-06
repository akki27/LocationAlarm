package com.akki.locationalarm.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.akki.locationalarm.activities.AddNewAlarmActivity;
import com.akki.locationalarm.activities.AlarmActivity;
import com.akki.locationalarm.R;
import com.akki.locationalarm.db.AlarmItemModel;
import com.akki.locationalarm.db.AlarmViewModel;
import com.akki.locationalarm.utils.AppConstants;

import java.util.List;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public class AlarmListViewAdapter extends RecyclerView.Adapter<AlarmListViewAdapter.ItemRecyclerViewHolder> {

    private final String TAG = AlarmListViewAdapter.class.getName();

    private Context mContext;
    private List<AlarmItemModel> alarmItemModelList;
    private AlarmViewModel mViewModel;

    public interface OnAlarmClickListener {
        void onAlarmClick(AlarmItemModel alarmItemModel);
    }
    private final OnAlarmClickListener mListener;

    public class ItemRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView mAlarmName, mAlarmDescription;
        private ToggleButton mToggleAlarm;
        public LinearLayout viewForeground;

        ItemRecyclerViewHolder(View view) {
            super(view);
            mAlarmName = view.findViewById(R.id.tv_alarm_name);
            mAlarmDescription = view.findViewById(R.id.tv_alarm_Description);
            mToggleAlarm = view.findViewById(R.id.tgl_alarm_button);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public AlarmListViewAdapter(Context context, List<AlarmItemModel> alarmItemModelList, OnAlarmClickListener listener) {
        this.mContext = context;
        this.alarmItemModelList = alarmItemModelList;
        this.mListener = listener;

        mViewModel = ViewModelProviders.of((AlarmActivity)mContext).get(AlarmViewModel.class);
    }

    @Override
    public ItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_alarm_row, parent, false);

        return new ItemRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemRecyclerViewHolder holder, final int position) {

        final AlarmItemModel alarmItemModel = alarmItemModelList.get(position);

        holder.mAlarmName.setText(alarmItemModel.getTitle());
        holder.mAlarmDescription.setText(alarmItemModel.getAlarmDescription());

        if(alarmItemModel.isAlarmOn()) {
            holder.mToggleAlarm.setChecked(true);
        } else {
            holder.mToggleAlarm.setChecked(false);
        }

        holder.mToggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmItemModel.setAlarmOn(isChecked);
                mViewModel.updateAlarmStatus(alarmItemModel);
            }
        });

        holder.itemView.setTag(alarmItemModel);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddNewAlarmActivity.class);
                intent.putExtra(AppConstants.ALARM_TITLE_KEY, alarmItemModel.getTitle());
                intent.putExtra(AppConstants.ALARM_DESCRIPTION_KEY, alarmItemModel.getAlarmDescription());
                intent.putExtra(AppConstants.ALARM_LOCATION_LATITUDE, alarmItemModel.getLocationLatitude());
                intent.putExtra(AppConstants.ALARM_LOCATION_LONGITUDE, alarmItemModel.getLocationLongitude());
                intent.putExtra(AppConstants.ALARM_LOCATION_ALTITUDE, alarmItemModel.getLocationAltitude());
                intent.putExtra(AppConstants.ALARM_RINGTONE_KEY, alarmItemModel.getAlarmRingTone());
                intent.putExtra(AppConstants.ALARM_ISREPEAT_KEY, alarmItemModel.isRepeat());
                intent.putExtra(AppConstants.ALARM_REPEAT_INTERVAL, alarmItemModel.getRepeatInterval());
                intent.putExtra(AppConstants.ALARM_ISVIBRATE_KEY, alarmItemModel.isVibrate());
                intent.putExtra(AppConstants.ALARM_STATUS_KEY, alarmItemModel.isAlarmOn());
                mContext.startActivity(intent);
            }
        });*/

        /* Notification Item click event */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAlarmClick(alarmItemModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return alarmItemModelList.size();
    }

    public void addItems(List<AlarmItemModel> alarmItemModelList) {
        this.alarmItemModelList = alarmItemModelList;
        notifyDataSetChanged();
    }


    public void removeItem(int position) {
        alarmItemModelList.remove(position);

        // remove the item from recycler view
        alarmItemModelList.remove(position);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void removeItem(int position, AlarmViewModel viewModel, RecyclerView recyclerView) {
        alarmItemModelList.remove(position);

        // remove the item from recycler view
        AlarmItemModel alarmItemModel = (AlarmItemModel)recyclerView.findViewHolderForAdapterPosition(position).itemView.getTag();
        viewModel.deleteAlarm(alarmItemModel);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(AlarmItemModel alarmItemModel, int position, AlarmViewModel viewModel) {
        alarmItemModelList.add(position, alarmItemModel);
        viewModel.addAlarm(alarmItemModel);

        // notify item added by position
        notifyItemInserted(position);
    }

}
