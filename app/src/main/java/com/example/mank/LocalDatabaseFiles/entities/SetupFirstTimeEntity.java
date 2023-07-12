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









