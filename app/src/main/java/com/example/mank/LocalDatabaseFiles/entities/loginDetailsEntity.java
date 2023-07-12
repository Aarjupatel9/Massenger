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
    @ColumnInfo(name = "es1")
    private String es1 = "";
    @ColumnInfo(name = "es2")
    private String es2 = "";
    @ColumnInfo(name = "elf2")
    private long elf2=0;
    @ColumnInfo(name = "elf1")
    private long elf1=0;
    @ColumnInfo(name = "elf3")
    private long elf3=0;
    @ColumnInfo(name = "elf4")
    private long elf4=0;

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

    public long getElf1() {
        return elf1;
    }

    public void setElf1(long elf1) {
        this.elf1 = elf1;
    }

    public long getElf2() {
        return elf2;
    }

    public long getElf3() {
        return elf3;
    }

    public long getElf4() {
        return elf4;
    }

    public String getEs1() {
        return es1;
    }

    public String getEs2() {
        return es2;
    }

    public void setEs1(String es1) {
        this.es1 = es1;
    }

    public void setElf2(long elf2) {
        this.elf2 = elf2;
    }

    public void setElf3(long elf3) {
        this.elf3 = elf3;
    }

    public void setElf4(long elf4) {
        this.elf4 = elf4;
    }

    public void setEs2(String es2) {
        this.es2 = es2;
    }



}








