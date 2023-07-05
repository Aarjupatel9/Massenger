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

}








