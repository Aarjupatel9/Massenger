package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.user_login_id;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;

import java.util.List;

public class MassegeHolderForSpecificPurpose {
    List<MassegeEntity> data;

    public MassegeHolderForSpecificPurpose(MainDatabaseClass db, int code) {

        MassegeDao massegeDao = db.massegeDao();
        if (code == -1) {
            data = massegeDao.getMassegesWithStatus(-1, user_login_id);
        } else if (code == 0) {
            data = massegeDao.getMassegesWithStatus(0, user_login_id);
        }else if (code == 2) {
            data = massegeDao.getMassegeByAppUserId(user_login_id);
        }
    }

    public List<MassegeEntity> getMassegeList() {
        return data;
    }
}
