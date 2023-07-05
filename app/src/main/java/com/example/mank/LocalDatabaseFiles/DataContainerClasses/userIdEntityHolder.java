package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;

public class userIdEntityHolder {
//    userIdEntityForApp UserLoginId;
    String UserLoginId;
    long UserMobileNumber;

    public userIdEntityHolder( MainDatabaseClass db){

        MassegeDao massegeDao = db.massegeDao();

        UserLoginId = massegeDao.getUserIdFromDatabase();
        UserMobileNumber = massegeDao.getUserMobileNumber();

    }

//    public userIdEntityForApp getData(){
//        return UserLoginId;
//    }
    public String getUserLoginId(){
        return UserLoginId;
    }
    public long getUserMobileNumber(){
        return  this.UserMobileNumber;
    }
}
