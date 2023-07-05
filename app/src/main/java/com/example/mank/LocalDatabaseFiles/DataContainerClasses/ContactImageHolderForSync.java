package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.user_login_id;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;

import java.util.List;

public class ContactImageHolderForSync {
    private List<AllContactOfUserEntity> ImageOfConnectedContact;

    public ContactImageHolderForSync(MainDatabaseClass db) {
        MassegeDao massegeDao = db.massegeDao();
        ImageOfConnectedContact = massegeDao.getConnectedContactImageList(user_login_id);
    }

    public List<AllContactOfUserEntity> getImageOfConnectedContact() {
        return ImageOfConnectedContact;
    }
}
