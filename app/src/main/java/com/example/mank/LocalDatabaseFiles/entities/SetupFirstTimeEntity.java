package com.example.mank.LocalDatabaseFiles.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "app_open_details")
public class SetupFirstTimeEntity {
    @PrimaryKey(autoGenerate = true)
    public int appOpenNumber;

    @ColumnInfo(name = "LastOpenTime")
    private long LastOpenTime;

    public SetupFirstTimeEntity() {
        Date date = new Date();
        LastOpenTime = date.getTime();
    }

    public SetupFirstTimeEntity(long date) {
        this.LastOpenTime = date;
    }

    public long getDate() {
        return LastOpenTime;
    }

    public void setLastOpenTime(long time){
        this.LastOpenTime = time;
    }
    public long getLastOpenTime(){
        return  this.LastOpenTime;
    }
    public int getAppOpenNumber() {
        return appOpenNumber;
    }

}









