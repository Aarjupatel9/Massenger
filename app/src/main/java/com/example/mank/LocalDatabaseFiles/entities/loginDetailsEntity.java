package com.example.mank.LocalDatabaseFiles.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "login")
public class loginDetailsEntity {
    @NonNull
    @PrimaryKey
    public String UID;
    @ColumnInfo(name = "MobileNumber")
    public Long MobileNumber;
    @ColumnInfo(name = "password")
    public String Password;
    @ColumnInfo(name = "DisplayUserName")
    public String DisplayUserName;
    @ColumnInfo(name = "About")
    public String About;

    public loginDetailsEntity() {
    }
    public loginDetailsEntity(String UID, @NonNull String Password, Long MobileNumber, String DisplayUserName , String about) {
        this.Password = Password;
        this.MobileNumber = MobileNumber;
        this.UID = UID;
        this.DisplayUserName = DisplayUserName;
        this.About = about;
    }
    public String getAbout() {
        return About;
    }
    public void setAbout(String about) {
        About = about;
    }

    @NonNull
    public void setMobileNumber(Long mobileNumber) {
        this.MobileNumber = mobileNumber;
    }

    @NonNull
    public void setPassword(String password) {
        this.Password = password;
    }

    @NonNull
    public Long getMobileNumber() {
        return this.MobileNumber;
    }

    @NonNull
    public void setUID(String UID) {
        this.UID = UID;
    }

    @NonNull
    public String getUID() {
        return this.UID;
    }

    @NonNull
    public String getPassword() {
        return this.Password;
    }

    public String getDisplayUserName() {
        return DisplayUserName;
    }

    public void setDisplayUserName(String displayUserName) {
        DisplayUserName = displayUserName;
    }
}








