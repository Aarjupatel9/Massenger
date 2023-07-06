package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.contactArrayList;
import static com.example.mank.MainActivity.recyclerViewAdapter;
import static com.example.mank.MainActivity.user_login_id;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListHolder {

    List<ContactWithMassengerEntity> data;
    private  ArrayList<ContactWithMassengerEntity> MainContactList;
    private boolean pass = false;
    public ContactListHolder(MainDatabaseClass db) {
        MassegeDao massegeDao = db.massegeDao();
        data = massegeDao.getContactDetailsFromDatabase(user_login_id);
        MainContactList = new ArrayList<>();
        MainContactList.addAll(data);
        pass=true;
    }

    public  ArrayList<ContactWithMassengerEntity> getMainContactList(){
        return  MainContactList;
    }
    public List<ContactWithMassengerEntity> getData() {
        return data;
    }


}
