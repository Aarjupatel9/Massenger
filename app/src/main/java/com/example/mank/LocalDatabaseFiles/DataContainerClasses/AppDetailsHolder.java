package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.SetupFirstTimeEntity;

import java.util.Calendar;
import java.util.Date;

public class AppDetailsHolder {
    SetupFirstTimeEntity setupFirstTimeEntity;
    MassegeDao massegeDao;

    public AppDetailsHolder(MainDatabaseClass db) {
        massegeDao = db.massegeDao();
        setupFirstTimeEntity = massegeDao.getLastAppOpenEntity();
    }

    public void addThisDetails() {
        Date date = new Date();
        SetupFirstTimeEntity new_details = new SetupFirstTimeEntity(date.getTime());
        massegeDao.addAppOpenDetails(new_details);
    }

    public SetupFirstTimeEntity getData() {
        return setupFirstTimeEntity;
    }

}
