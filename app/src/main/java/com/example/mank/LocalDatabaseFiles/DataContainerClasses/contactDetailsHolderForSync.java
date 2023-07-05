package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.user_login_id;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;

import java.util.List;

public class contactDetailsHolderForSync {
    List<AllContactOfUserEntity> allContact;
    List<AllContactOfUserEntity> connectedContact;
    List<AllContactOfUserEntity> disConnectedContact;

    public contactDetailsHolderForSync(MainDatabaseClass db) {
        MassegeDao massegeDao = db.massegeDao();
        allContact = massegeDao.getAllContactDetailsFromDB(user_login_id);
        connectedContact = massegeDao.getConnectedContactDetailsFromDB(user_login_id);
        disConnectedContact = massegeDao.getDisConnectedContactDetailsFromDB(user_login_id);
    }

    public List<AllContactOfUserEntity> getAllContact() {
        return allContact;
    }

    public List<AllContactOfUserEntity> getConnectedContact() {
        return connectedContact;
    }
    public List<AllContactOfUserEntity> getDisConnectedContact() {
        return disConnectedContact;
    }
}
