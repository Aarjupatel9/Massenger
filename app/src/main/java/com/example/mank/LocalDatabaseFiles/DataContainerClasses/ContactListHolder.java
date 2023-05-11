package com.example.mank.LocalDatabaseFiles.DataContainerClasses;

import static com.example.mank.MainActivity.contactArrayList;
import static com.example.mank.MainActivity.recyclerViewAdapter;
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
    public  ArrayList<ContactWithMassengerEntity> MainContactList;
    private boolean pass = false;
    public ContactListHolder(MainDatabaseClass db) {

        MassegeDao massegeDao = db.massegeDao();
        data = massegeDao.getContactDetailsFromDatabase();
        MainContactList = new ArrayList<>();
        MainContactList.addAll(data);
        pass=true;
    }

    public List<ContactWithMassengerEntity> getData() {
        return data;
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    public void updatePositionOfContact(long C_ID, Context context){
        synchronized (this) {
            while (!pass){
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            pass = false;
            for (int i = 0; i < contactArrayList.size(); i++) {
                if (contactArrayList.get(i).getC_ID() == C_ID) {
                    ContactWithMassengerEntity x = contactArrayList.remove(i);
                    contactArrayList.add(0, x);
                    Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            pass=true;
            notifyAll();
        }
    }

}
