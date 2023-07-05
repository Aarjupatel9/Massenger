package com.example.mank.ThreadPackages;

import static com.example.mank.MainActivity.user_login_id;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.cipher.MyCipher;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class GetUserContactDetailsFromPhone extends Thread {

    AllContactOfUserEntity allContactOfUserEntity;
    JSONArray ContactDetails;
    MyCipher mc;
    Context context;
    MainDatabaseClass db;
    MassegeDao massegeDao;
    boolean pass = false;

    //tmp
    List<AllContactOfUserEntity> allContactOfUserEntityList;

    public GetUserContactDetailsFromPhone(Context context, MainDatabaseClass db) {
        this.context = context;
        mc = new MyCipher();
        this.db = db;
        massegeDao = db.massegeDao();
        allContactOfUserEntityList = new ArrayList<>();
        ContactDetails = new JSONArray();
    }

    @Override
    public void run() {

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Log.d("log-GetUserContactDetailsFromPhone", "getContacts: total contact is " + cursor.getCount());

        if (cursor.getCount() > 0) {
            int counter = 0;
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                number = number.replaceAll("\\s", "");
                number = number.replaceAll("-", "");
                number = number.replaceAll("\\)", "");
                number = number.replaceAll("\\(", "");

                if (number.length() > 9) {
                    try {
                        if (number.charAt(0) == '+') {
                            number = number.substring(3);
                        }
                        allContactOfUserEntity = new AllContactOfUserEntity(Long.parseLong(number), display_name, "-1");
                    } catch (IndexOutOfBoundsException e) {
                        Log.d("log-GetUserContactDetailsFromPhone", "IndexOutOfBoundsException: for " + number + " || " + e);
                    } catch (Exception e) {
                        Log.d("log-GetUserContactDetailsFromPhone", "Exception: for " + number + " || " + e);
                    }
                    //makeing jsonArray
                    JSONArray jsonParam = new JSONArray();
                    jsonParam.put(mc.encrypt(counter));
                    jsonParam.put(mc.encrypt(display_name));
                    jsonParam.put(mc.encrypt(number));
                    ContactDetails.put(jsonParam);

                    allContactOfUserEntityList.add(allContactOfUserEntity);
                    List<AllContactOfUserEntity> x = massegeDao.getSelectedAllContactOfUserEntity(allContactOfUserEntity.getMobileNumber(), user_login_id);
                    if (x.size() == 0)
                        massegeDao.addAllContactOfUserEntity(allContactOfUserEntity);
                }

                counter++;
            }
        }
        pass = true;
    }

    public boolean getIsCompleted() {
        return pass;
    }

    public JSONArray getContactDetails() {
        return ContactDetails;
    }
    public List<AllContactOfUserEntity> getAllContactOfUserEntityList(){
        return allContactOfUserEntityList;
    }
}



