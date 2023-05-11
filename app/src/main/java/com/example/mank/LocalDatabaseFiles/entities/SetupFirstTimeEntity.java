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

    @ColumnInfo(name = "openAtTime")
    @TypeConverters({DateTypeConverter.class})
    public Date date;

    public SetupFirstTimeEntity() {
    }

    public SetupFirstTimeEntity(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getAppOpenNumber() {
        return appOpenNumber;
    }
}









