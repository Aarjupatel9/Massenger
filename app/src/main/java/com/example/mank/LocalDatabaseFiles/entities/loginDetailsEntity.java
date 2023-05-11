package com.example.mank.LocalDatabaseFiles.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "login")
public class loginDetailsEntity {
    @PrimaryKey
    public long U_ID;
    @ColumnInfo(name = "MobileNumber")
    public Long MobileNumber;
    @ColumnInfo(name = "password")
    public String Password;
    @ColumnInfo(name = "DisplayUserName")
    public String DisplayUserName;

    public loginDetailsEntity() {
    }

//    public loginDetails_entity(@NonNull String Password, Long MobileNumber) {
//        this.Password = Password;
//        this.MobileNumber = MobileNumber;
//    }

    public loginDetailsEntity(long U_ID, @NonNull String Password, Long MobileNumber, String DisplayUserName) {
        this.Password = Password;
        this.MobileNumber = MobileNumber;
        this.U_ID = U_ID;
        this.DisplayUserName = DisplayUserName;
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
    public void setU_ID(int U_ID) {
        this.U_ID = U_ID;
    }

    @NonNull
    public long getU_ID() {
        return this.U_ID;
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








