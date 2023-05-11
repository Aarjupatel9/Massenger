package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;

import java.util.List;

public class MassegeHolderForSpecificPurpose {
    List<MassegeEntity> data;

    public MassegeHolderForSpecificPurpose(MainDatabaseClass db, int code) {

        MassegeDao massegeDao = db.massegeDao();
        if (code == 1) {
            data = massegeDao.getOfflineStateMassege();
        }else if(code ==2){
            data = massegeDao.getMassegeWithMassegeId((long) 0);
        }
    }

    public List<MassegeEntity> getMassegeList() {
        return data;
    }
}
