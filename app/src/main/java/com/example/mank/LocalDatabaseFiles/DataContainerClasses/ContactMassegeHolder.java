package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.user_login_id;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;

import java.util.List;

public class ContactMassegeHolder {
    List<MassegeEntity> data;

    public ContactMassegeHolder(MainDatabaseClass db, long C_ID) {

        MassegeDao massegeDao = db.massegeDao();
        if (C_ID == user_login_id) {
            data = massegeDao.getSelfChat(C_ID);
        } else {
            data = massegeDao.getChat(C_ID);
        }
    }

    public List<MassegeEntity> getMassegeList() {
        return data;
    }
}
