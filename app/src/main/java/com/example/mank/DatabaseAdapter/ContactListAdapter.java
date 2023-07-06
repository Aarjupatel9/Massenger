package com.example.mank.DatabaseAdapter;

import static com.example.mank.MainActivity.MainActivityStaticContext;
import static com.example.mank.MainActivity.contactArrayList;
import static com.example.mank.MainActivity.recyclerViewAdapter;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListAdapter {

    MassegeDao massegeDao;
    Context context;
    public static ArrayList<ContactWithMassengerEntity> contactList;
    List<ContactWithMassengerEntity> data;

    public ContactListAdapter(MainDatabaseClass db) {
        massegeDao = db.massegeDao();
        data = massegeDao.getContactDetailsFromDatabase(user_login_id);
        contactList = new ArrayList<>();
        contactList.addAll(data);

        setUpLastMasseges();
        setUpProfileImages();
    }

    private void setUpLastMasseges() {
        Thread ts = new Thread(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                for (int i = 0; i < contactList.size(); i++) {
                    if(!contactList.get(i).getCID().equals(user_login_id)){
                    String massege = massegeDao.getLastInsertedMassege(contactList.get(i).getCID(), user_login_id);
                    contactList.get(i).setLastMassege(massege);
                    Log.d("log-ContactListAdapter", "massege is : " + massege+ " for CID : "+contactList.get(i).getCID()+" and appUserId : "+user_login_id);
                    }else {
                        String massege = massegeDao.getSelfLastInsertedMassege(contactList.get(i).getCID(), user_login_id);
                        contactList.get(i).setLastMassege(massege);
                        Log.d("log-ContactListAdapter-self", "massege is : " + massege+ " for CID : "+contactList.get(i).getCID()+" and appUserId : "+user_login_id);
                    }
                }
                try {
                    Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            if (recyclerViewAdapter != null) {
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
        ts.start();
    }

    private void setUpProfileImages() {
        Thread tu = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < contactList.size(); i++) {
                    String CID = contactList.get(i).getCID();
                    String imagePath = "/storage/emulated/0/Android/media/com.massenger.mank.main/Pictures/profiles/" + CID + user_login_id + ".png";
                    byte[] byteArray = null;
                    try {
                        File imageFile = new File(imagePath);
                        FileInputStream fis = new FileInputStream(imageFile);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                        fis.close();
                        bos.close();
                        byteArray = bos.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (byteArray != null) {
                        Log.d("log-ContactListAdapter", "setUserImage : after fetch image form file system : " + byteArray.length);
                        contactList.get(i).setUserImage(byteArray);
                        recyclerViewAdapterNotifyLocal();
                    }
                }
            }
        });
        tu.start();

    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    public void AddContact(ContactWithMassengerEntity newEntity) {
        Log.d("log-ContactListAdapter", "AddContact method start for " + newEntity.getDisplayName() + " , " + newEntity.getMobileNumber());
        Thread tx = new Thread(new Runnable() {
            @Override
            public void run() {
                massegeDao.SaveContactDetailsInDatabase(newEntity);
            }
        });
        tx.start();
        contactList.add(0, newEntity);
        recyclerViewAdapterNotifyLocal();
        Log.d("log-ContactListAdapter", "AddContact method end");
    }

    public ArrayList<ContactWithMassengerEntity> getContactList() {
        return contactList;
    }

    public void updateSelfUserImage(byte[] userImage) {
        Thread ti = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d("log-ContactListAdapter", "updateSelfUserImage result : " + user_login_id);
                Log.d("log-ContactListAdapter", "updateSelfUserImage result : " + userImage.length);

//                int res = massegeDao.updateImageIntoContactDetails(user_login_id, userImage, user_login_id);
//                Log.d("log-ContactListAdapter", "updateSelfUserImage result : " + res);

                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getCID().equals(user_login_id)) {
                        contactList.get(i).setUserImage(userImage);
                        recyclerViewAdapterNotifyLocal();
                        break;
                    }
                }

            }
        });
        ti.start();
    }

    @SuppressLint("RestrictedApi")
    private void recyclerViewAdapterNotifyLocal() {
        try {
            Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    if (recyclerViewAdapter != null) {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("log-ContactListAdapter", "AddContact Exception : " + e);
        }
    }

    public void updateMassegeArrivalValue(int index, ContactWithMassengerEntity contactView) {

        int prev_value = contactView.getNewMassegeArriveValue();
        contactView.setNewMassegeArriveValue(prev_value + 1);
        MainActivity.contactArrayList.set(index, contactView);
//        MainActivity.recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewAdapterNotifyLocal();
        MainActivity.ChatsRecyclerView.scrollToPosition(MainActivity.recyclerViewAdapter.getItemCountMyOwn());
    }

    public void practiceMethod(String CID, byte[] image) {
        Log.d("log-ContactListAdapter", "practiceMethod start : " + image.length);

        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).getCID().equals(CID)) {
                contactList.get(i).setUserImage(image);
                recyclerViewAdapterNotifyLocal();
                break;
            }
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    public void updatePositionOfContact(String C_ID, Context context) {
        if (contactArrayList != null) {
            for (int i = 0; i < contactArrayList.size(); i++) {
                if (contactArrayList.get(i).getCID().equals(C_ID)) {
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
        } else {
            Log.d("log-ContactListAdapter", "contactArrayList is null");
        }
    }


}
