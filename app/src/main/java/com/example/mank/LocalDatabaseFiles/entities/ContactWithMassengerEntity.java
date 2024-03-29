package com.example.mank.LocalDatabaseFiles.entities;

import static com.example.mank.MainActivity.user_login_id;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contactDetails")
public class ContactWithMassengerEntity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "index")
    private long index;
    @ColumnInfo(name = "CID")
    private String CID;
    @ColumnInfo(name = "AppUserId")
    private String AppUserId;
    @ColumnInfo(name = "MobileNumber")
    private Long MobileNumber;
    @ColumnInfo(name = "DisplayName")
    private String DisplayName;
    @ColumnInfo(name = "About")
    private String About = "jai shree krushn";
    @ColumnInfo(name = "UserImage")
    private byte[] UserImage = null;
    @ColumnInfo(name = "ProfileImageVersion")
    private long ProfileImageVersion = 0;
    @ColumnInfo(name = "PriorityRank")
    private long PriorityRank = 0;
    @ColumnInfo(name = "LocallySaved")
    private int LocallySaved = 1;
    @ColumnInfo(name = "NewMassegeArriveValue")
    private int NewMassegeArriveValue = 0;

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


    private boolean TouchEffectPass = false;
    private String lastMassege = "";




    public ContactWithMassengerEntity() {
    }
    public ContactWithMassengerEntity(long MobileNumber, String Display_name, String CID) {
        this.MobileNumber = MobileNumber;
        this.DisplayName = Display_name;
        this.CID = CID;
        this.AppUserId = user_login_id;

    }

    public ContactWithMassengerEntity(long MobileNumber, String DisplayName, String CID, long priorityRank) {
        this.MobileNumber = MobileNumber;
        this.DisplayName = DisplayName;
        this.CID = CID;
        this.PriorityRank = priorityRank;
        this.AppUserId = user_login_id;

    }

    public ContactWithMassengerEntity(long MobileNumber, String DisplayName, String CID, int LocallySaved) {
        this.MobileNumber = MobileNumber;
        this.DisplayName = DisplayName;
        this.CID = CID;
        this.LocallySaved = LocallySaved;
        this.AppUserId = user_login_id;

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
    public void setEs2(String es2) {
        this.es2 = es2;
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



    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public void setProfileImageVersion(long v) {
        this.ProfileImageVersion = v;
    }

    public long getProfileImageVersion() {
        return this.ProfileImageVersion;
    }

    public void setAppUserId(String appUserId) {
        AppUserId = appUserId;
    }

    public String getAppUserId() {
        return AppUserId;
    }


    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }


    public void setTouchEffectPass(boolean touchEffectPass) {
        TouchEffectPass = touchEffectPass;
    }

    public boolean isTouchEffectPass() {
        return TouchEffectPass;
    }

    @NonNull
    public void setMobileNumber(Long mobileNumber) {
        this.MobileNumber = mobileNumber;
    }

    @NonNull
    public void setCID(String CID) {
        this.CID = CID;
    }

    @NonNull
    public void setDisplayName(String Display_name) {
        this.DisplayName = Display_name;
    }

    @NonNull
    public Long getMobileNumber() {
        return this.MobileNumber;
    }

    @NonNull
    public String getCID() {
        return this.CID;
    }

    public int getLocallySaved() {
        return this.LocallySaved;
    }

    public void setLocallySaved(int LocallySaved) {
        this.LocallySaved = LocallySaved;
    }

    @NonNull
    public String getDisplayName() {
        return this.DisplayName;
    }

    public void setNewMassegeArriveValue(int NewMassegeArriveValue) {
        this.NewMassegeArriveValue = NewMassegeArriveValue;
    }

    public int getNewMassegeArriveValue() {
        return NewMassegeArriveValue;
    }

    public long getPriorityRank() {
        return PriorityRank;
    }

    public void setPriorityRank(int priorityRank) {
        PriorityRank = priorityRank;
    }

    public byte[] getUserImage() {
        return UserImage;
    }

    public void setUserImage(byte[] userImage) {
        UserImage = userImage;
    }

    public void setLastMassege(String massege) {
        this.lastMassege = massege;
    }

    public String getLastMassege() {
        return lastMassege;
    }
}
