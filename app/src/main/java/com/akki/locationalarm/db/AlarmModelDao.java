package com.akki.locationalarm.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by v-akhilesh.chaudhary on 06/10/2018.
 */

@Dao
public interface AlarmModelDao {

    @Query("select * from alarm_table")
    LiveData<List<AlarmItemModel>> getAllSavedAlarm();

    @Query("select * from alarm_table")
    List<AlarmItemModel> getAllSavedAlarmList();

    @Query("select * from alarm_table where id = :id")
    AlarmItemModel getAlarmById(String id);

    @Insert(onConflict = REPLACE)
    void saveAlarm(AlarmItemModel alarmItemModel);

    @Delete
    void deleteAlarm(AlarmItemModel alarmItemModel);

    @Query("update alarm_table set isAlarmOn =:alarmStatus where id = :id")
    void updateAlarmStatus(String id, boolean alarmStatus);

    @Query("update alarm_table set isAlarmOn =:alarmStatus where alarm_title = :title")
    void updateAlarmStatusByTitle(String title, boolean alarmStatus);

}