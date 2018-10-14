package com.akki.locationalarm.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by v-akhilesh.chaudhary on 06/10/2018.
 */

//@Entity(tableName = "alarm_table")
@Entity(tableName = "alarm_table", indices = {@Index(value = "alarm_title",
        unique = true)})
public class AlarmItemModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "alarm_title")
    private String title;

    @ColumnInfo(name = "alarm_description")
    private String alarmDescription;

    @ColumnInfo(name = "location_latitude")
    private String locationLatitude;

    @ColumnInfo(name = "location_longitude")
    private String locationLongitude;

    @ColumnInfo(name = "location_altitude")
    private String locationAltitude;

    @ColumnInfo(name = "alarm_ringtome")
    private String alarmRingTone;

    @ColumnInfo(name = "isRepeat")
    private String isRepeat;

    @ColumnInfo(name = "repeatInterval")
    private String repeatInterval;

    @ColumnInfo(name = "isVibrate")
    private String isVibrate;

    @ColumnInfo(name = "isAlarmOn")
    private boolean isAlarmOn;

    public AlarmItemModel(String title, String alarmDescription, String locationLatitude, String locationLongitude, String locationAltitude,
                          String alarmRingTone, String isRepeat, String repeatInterval, String isVibrate, boolean isAlarmOn) {
        this.title = title;
        this.alarmDescription = alarmDescription;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAltitude = locationAltitude;
        this.alarmRingTone = alarmRingTone;
        this.isRepeat = isRepeat;
        this.repeatInterval = repeatInterval;
        this.isVibrate = isVibrate;
        this.isAlarmOn = isAlarmOn;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlarmDescription() {
        return alarmDescription;
    }

    public void setAlarmDescription(String alarmDescription) {
        this.alarmDescription = alarmDescription;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getLocationAltitude() {
        return locationAltitude;
    }

    public void setLocationAltitude(String locationAltitude) {
        this.locationAltitude = locationAltitude;
    }

    public String getAlarmRingTone() {
        return alarmRingTone;
    }

    public void setAlarmRingTone(String alarmRingTone) {
        this.alarmRingTone = alarmRingTone;
    }

    public String isRepeat() {
        return isRepeat;
    }

    public void setRepeat(String repeat) {
        isRepeat = repeat;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String isVibrate() {
        return isVibrate;
    }

    public void setVibrate(String vibrate) {
        isVibrate = vibrate;
    }

    public boolean isAlarmOn() {
        return isAlarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        isAlarmOn = alarmOn;
    }
}
