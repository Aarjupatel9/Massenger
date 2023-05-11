package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.SetupFirstTimeEntity;

import java.util.Calendar;

public class AppDetailsHolder {
    SetupFirstTimeEntity data;
    MassegeDao massegeDao;

    public AppDetailsHolder(MainDatabaseClass db) {
        massegeDao = db.massegeDao();
        data = massegeDao.getAppOpenDetailsForFirstTime();
    }

    public void addThisDetails() {
        SetupFirstTimeEntity new_details = new SetupFirstTimeEntity(Calendar.getInstance().getTime());
        massegeDao.addAppOpenDetails(new_details);
    }

    public SetupFirstTimeEntity getData() {
        return data;
    }

}
