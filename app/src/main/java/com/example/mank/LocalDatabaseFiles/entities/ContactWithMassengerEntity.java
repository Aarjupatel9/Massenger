package com.example.mank.LocalDatabaseFiles.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contactDetails")
public class ContactWithMassengerEntity {

    @PrimaryKey
    @ColumnInfo(name = "C_ID")
    private long C_ID;
    @ColumnInfo(name = "MobileNumber")
    private Long MobileNumber;
    @ColumnInfo(name = "DisplayName" )
    private String Display_name;

    @ColumnInfo(name = "PriorityRank")
    private long PriorityRank = 0;
  @ColumnInfo(name = "LocallySaved")
    private int LocallySaved = 1;

    @ColumnInfo(name = "NewMassegeArriveValue")
    private int NewMassegeArriveValue = 0;
    private boolean TouchEffectPass = false;

    public ContactWithMassengerEntity() {
    }

    public ContactWithMassengerEntity(long MobileNumber, String Display_name, long C_ID) {
        this.MobileNumber = MobileNumber;
        this.Display_name = Display_name;
        this.C_ID = C_ID;
    }
    public ContactWithMassengerEntity(long MobileNumber, String Display_name, long C_ID,long priorityRank) {
        this.MobileNumber = MobileNumber;
        this.Display_name = Display_name;
        this.C_ID = C_ID;
        this.PriorityRank = priorityRank;
    }
    public ContactWithMassengerEntity(long MobileNumber, String Display_name, long C_ID,int LocallySaved) {
        this.MobileNumber = MobileNumber;
        this.Display_name = Display_name;
        this.C_ID = C_ID;
        this.LocallySaved = LocallySaved;
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
    public void setC_ID(int C_ID) {
        this.C_ID = C_ID;
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
    public long getC_ID() {
        return this.C_ID;
    }

    public int getLocallySaved(){
        return this.LocallySaved;
    }public void setLocallySaved(int LocallySaved){
        this.LocallySaved = LocallySaved;
    }
    @NonNull
    public String getDisplay_name() {
        return this.Display_name;
    }

    public  void setNewMassegeArriveValue(int NewMassegeArriveValue){
        this.NewMassegeArriveValue = NewMassegeArriveValue;
    }
    public int getNewMassegeArriveValue() {
        return  NewMassegeArriveValue;
    }

    public long getPriorityRank() {
        return PriorityRank;
    }

    public void setPriorityRank(int priorityRank) {
        PriorityRank = priorityRank;
    }
}
