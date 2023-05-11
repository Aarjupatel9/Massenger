package com.example.mank.LocalDatabaseFiles.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "massege")
public class MassegeEntity {
    @PrimaryKey(autoGenerate = true)
    public long Chat_id;

    @ColumnInfo(name = "MassegeID")
    public long MassegeID;

    @ColumnInfo(name = "sender_id")
    public long SenderId;

    @ColumnInfo(name = "receiver_id")
    public long ReceiverId;

    @ColumnInfo(name = "massege")
    public String Massege;

    @ColumnInfo(name = "time_of_send")
    public long timeOfSend;

    //0 for sent to server , 1 for reached at server, 2 for reach at other side, 3 for viewed, 4 for delete, 5 for not sent to server
    @ColumnInfo(name = "massege_status")
    public int MassegeStatus;


    public MassegeEntity() {
    }

    public MassegeEntity(long UserId, long C_ID, String massege, long timeOfSend, int massegeStatus) {
        this.SenderId = UserId;
        this.ReceiverId = C_ID;
        this.Massege = massege;
        this.timeOfSend = timeOfSend;
        this.MassegeStatus = massegeStatus;
    }public MassegeEntity(long MassegeID, long UserId, long C_ID, String massege, long timeOfSend, int massegeStatus) {
        this.SenderId = UserId;
        this.ReceiverId = C_ID;
        this.Massege = massege;
        this.timeOfSend = timeOfSend;
        this.MassegeStatus = massegeStatus;
        this.MassegeID = MassegeID;
    }


    public long getMassegeID(){
        return this.MassegeID;
    }

    public long getChat_id() {
        return this.Chat_id;
    }

    public long getSenderId() {
        return SenderId;
    }

    public long getReceiverId() {
        return ReceiverId;
    }

    public String getMassege() {
        return this.Massege;
    }

    public int getMassegeStatus() {
        return this.MassegeStatus;
    }

    public long getTimeOfSend() {
        return timeOfSend;
    }

    public void setMassegeStatus(int status){
        MassegeStatus=status;
    }
}








