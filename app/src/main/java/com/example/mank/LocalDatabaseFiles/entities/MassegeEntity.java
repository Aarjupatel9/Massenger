package com.example.mank.LocalDatabaseFiles.entities;

import static com.example.mank.MainActivity.user_login_id;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "massege", indices = {@Index(value = {"SenderId", "ReceiverId", "timeOfSend"}, unique = true)})
public class MassegeEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long chatId;

    @ColumnInfo(name = "AppUserId")
    private String AppUserId;

    @ColumnInfo(name = "SenderId")
    private String SenderId;

    @ColumnInfo(name = "ReceiverId")
    private String ReceiverId;

    @ColumnInfo(name = "massege")
    private String Massege;

    @ColumnInfo(name = "timeOfSend")
    private long timeOfSend;

    //0 for sent to server , 1 for reached at server, 2 for reach at other side, 3 for viewed, 4 for delete, 5 for not sent to server
    @ColumnInfo(name = "MassegeStatus")
    public int MassegeStatus;
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


    public MassegeEntity() {
    }
    public MassegeEntity(String UserId, String CID, String massege, long timeOfSend, int massegeStatus) {
        this.SenderId = UserId;
        this.ReceiverId = CID;
        this.Massege = massege;
        this.timeOfSend = timeOfSend;
        this.MassegeStatus = massegeStatus;
        this.AppUserId = user_login_id;
    }
    public void setAppUserId(String appUserId) {
        AppUserId = appUserId;
    }
    public String getAppUserId() {
        return AppUserId;
    }

    public long getChatId() {
        return this.chatId;
    }
    public void setChatId(long chatId){
        this.chatId = chatId;
    }
    public String getSenderId() {
        return SenderId;
    }
    public  void setSenderId(String senderId){
        this.SenderId = senderId;
    }
    public String getReceiverId() {
        return ReceiverId;
    }
    public  void setReceiverId(String receiverId){
        this.ReceiverId = receiverId;
    }
    public String getMassege() {
        return this.Massege;
    }
    public  void setMassege(String massege){
        this.Massege = massege;
    }
    public int getMassegeStatus() {
        return this.MassegeStatus;
    }
    public void setMassegeStatus(int status){
        MassegeStatus=status;
    }
    public long getTimeOfSend() {
        return timeOfSend;
    }
    public void setTimeOfSend(long timeOfSend){
        this.timeOfSend = timeOfSend;
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








