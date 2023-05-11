package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;

import java.util.List;

public class contactDetailsHolderForSync {
    List<ContactWithMassengerEntity> data;

    public contactDetailsHolderForSync( MainDatabaseClass db){

        MassegeDao massegeDao = db.massegeDao();

        List<ContactWithMassengerEntity> dataFromDatabase = massegeDao.getContactDetailsFromDatabase();
        data = dataFromDatabase;
    }

    public List<ContactWithMassengerEntity> getData(){
        return data;
    }
}
