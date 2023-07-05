package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.user_login_id;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;

import java.util.List;
import java.util.Objects;

public class ContactMassegeHolder {
    List<MassegeEntity> data;

    public ContactMassegeHolder(MainDatabaseClass db, String CID) {

        MassegeDao massegeDao = db.massegeDao();
        if (Objects.equals(CID, user_login_id)) {
            data = massegeDao.getSelfChat(CID, user_login_id);
        } else {
            data = massegeDao.getChat(CID , user_login_id);
        }
    }

    public List<MassegeEntity> getMassegeList() {
        return data;
    }
}
