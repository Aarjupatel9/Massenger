package com.example.mank.LocalDatabaseFiles.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "allContactDetails")
public class AllContactOfUserEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "index")
    public int index;

    @ColumnInfo(name = "MobileNumber")
    public Long MobileNumber;

    @ColumnInfo(name = "C_ID")
    public long C_ID;
    @ColumnInfo(name = "DisplayName")
    public String Display_name;

    @ColumnInfo(name ="timestamp")
    public long timestamp;

    public AllContactOfUserEntity() {
    }

    public AllContactOfUserEntity( long MobileNumber, String Display_name,long C_ID ) {
        this.MobileNumber = MobileNumber;
        this.Display_name = Display_name;
        this.C_ID = C_ID;
        timestamp = new Date().getTime();
    }
    @NonNull
    public void setMobileNumber(Long mobileNumber) {
        this.MobileNumber = mobileNumber;
    }

    @NonNull
    public void setDisplay_name(String Display_name) {
        this.Display_name = Display_name;
    }

    @NonNull
    public Long getMobileNumber() {
        return this.MobileNumber;
    }

    @NonNull
    public String getDisplay_name() {
        return this.Display_name;
    }

    public long getC_ID() {
        return C_ID;
    }
    public  long getTimestamp(){
        return timestamp;
    }
}
