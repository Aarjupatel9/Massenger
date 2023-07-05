package com.example.mank.LocalDatabaseFiles.entities;

import static com.example.mank.MainActivity.user_login_id;

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

    @ColumnInfo(name = "AppUserId")
    private String AppUserId;
    @ColumnInfo(name = "UserImage")
    private byte[] UserImage = null;
    @ColumnInfo(name = "MobileNumber")
    public Long MobileNumber;
    @NonNull
    @ColumnInfo(name = "CID")
    public String CID;
    @ColumnInfo(name = "DisplayName")
    public String DisplayName;
    @ColumnInfo(name = "About")
    private String About = "jai shree krushn";

    @ColumnInfo(name = "time")
    public long time;
    @ColumnInfo(name = "ImageVersion")
    private long ImageVersion=0;

    public AllContactOfUserEntity() {
    }

    public AllContactOfUserEntity(long MobileNumber, String Display_name, String CID) {
        this.MobileNumber = MobileNumber;
        this.DisplayName = Display_name;
        this.CID = CID;
        time = new Date().getTime();
        this.AppUserId = user_login_id;
    }


    public String getAppUserId() {
        return AppUserId;
    }

    public void setAppUserId(String appUserId) {
        AppUserId = appUserId;
    }

    public void setImageVersion(long v) {
        this.ImageVersion = v;
    }
    public long getImageVersion() {
        return this.ImageVersion;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getAbout() {
        return About;
    }

    @NonNull
    public void setMobileNumber(Long mobileNumber) {
        this.MobileNumber = mobileNumber;
    }

    @NonNull
    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    @NonNull
    public Long getMobileNumber() {
        return this.MobileNumber;
    }

    @NonNull
    public String getDisplayName() {
        return this.DisplayName;
    }

    public String getCID() {
        return CID;
    }

    public long getTimestamp() {
        return time;
    }

    public byte[] getUserImage() {
        return UserImage;
    }

    public void setUserImage(byte[] UserImage) {
        this.UserImage = UserImage;
    }
}
