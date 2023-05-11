package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.db;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;

public class holdLoginData {

    loginDetailsEntity data;

    public holdLoginData(){
        MassegeDao massegeDao = db.massegeDao();
        data = massegeDao.getLoginDetailsFromDatabase();

    }

    public loginDetailsEntity getData(){
        return data;
    }
}
