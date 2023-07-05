package com.example.mank.FunctionalityClasses;

import static com.example.mank.DatabaseAdapter.ContactListAdapter.contactList;
import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.contactListAdapter;
import static com.example.mank.MainActivity.saveContactProfileImageToStorage;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.MainActivity.db;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.room.Room;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import io.socket.emitter.Emitter;

public class ContactDetailsFromMassegeViewPage extends Activity {

    private TextView contact_display_name, contact_about_details, contact_mobile_number;
    private ImageView imageView;

    private String CID;
    private long ContactMobileNumber;
    private String ContactName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_details_from_massege_view_page);

        Intent intent = getIntent();
        CID = intent.getStringExtra("CID");
        ContactMobileNumber = intent.getLongExtra("ContactMobileNumber", -2);
        ContactName = intent.getStringExtra("ContactName");
        //we have to fetch data from server

        updateUserDetailsFromServer();
        socket.on("getContactDetailsForContactDetailsFromMassegeViewPage_return", onGetContactDetailsForContactDetailsFromMassegeViewPage_return);
        socket.on("updateSingleContactProfileImageToUserProfilePage", onUpdateSingleContactProfileImageToUserProfilePage);

        Thread db_work = new Thread(new Runnable() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(),
                        MainDatabaseClass.class, "MassengerDatabase").fallbackToDestructiveMigration().allowMainThreadQueries().build();

            }
        });

        contact_display_name = (TextView) findViewById(R.id.contact_display_name);
        contact_about_details = (TextView) findViewById(R.id.contact_about_details);
        contact_mobile_number = (TextView) findViewById(R.id.contact_phone_number);
        imageView = (ImageView) findViewById(R.id.CDFMVP_imageView);
        contact_mobile_number.setText(String.valueOf(ContactMobileNumber));
        contact_display_name.setText(ContactName);
        setUpProfileImage();
    }

    private void updateUserDetailsFromServer() {
        Thread ts = new Thread(new Runnable() {
            @Override
            public void run() {
                MassegeDao massegeDao = db.massegeDao();
                long profileImageVersion = massegeDao.getContactProfileImageVersion(CID, user_login_id);
                Log.d("log-ContactDetailsFromMassegeViewPage", "image update part : " + CID + " and : " + profileImageVersion);
                if (socket != null) {
                    socket.emit("getContactDetailsForContactDetailsFromMassegeViewPage", user_login_id, Contact_page_opened_id, profileImageVersion);
                }
            }
        });
        ts.start();
    }

    private void setUpProfileImage() {
        Thread ts = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean pass = true;
                for (int i = 0; i < contactList.size(); i++) {
//                    Log.d("log-ContactDetailsFromMassegeViewPage", "setUpProfileImage : contactList : " + contactList.get(i).getCID() + " and CID : " + CID);
                    if (contactList.get(i).getCID().equals(CID)) {
                        byte[] byteArray = contactList.get(i).getUserImage();
                        Log.d("log-ContactDetailsFromMassegeViewPage", "setUpProfileImage : contactList image found " + contactList.get(i).toString());
                        String displayName = contactList.get(i).getDisplayName();
                        String about = contactList.get(i).getAbout();
                        Bitmap bitmapImage = null;
                        if (byteArray != null) {
                            bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        }
                        Bitmap finalBitmapImage = bitmapImage;
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalBitmapImage != null) {

                                        imageView.setImageBitmap(finalBitmapImage);
                                    }
                                    contact_display_name.setText(displayName);
                                    contact_about_details.setText(about);
                                }
                            });


                        pass = false;
                        break;
                    }
                }
                Log.d("log-ContactDetailsFromMassegeViewPage", "setUpProfileImage :pass : " + pass);
                if (pass) {
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
                        Log.d("log-ContactDetailsFromMassegeViewPage", "setUpProfileImage : after fetch image form file system : " + byteArray.length);
                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmapImage);
                            }
                        });
                    }
                }
            }
        });
        ts.start();
    }

    private final Emitter.Listener onGetContactDetailsForContactDetailsFromMassegeViewPage_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onCheckContactOnlineStatus_return", "call: onMassegeReachReceiptFromServer enter");
            try {
                String CID = args[0].toString();
                String display_name = (String) args[1];
                String contact_about = (String) args[2];
                int ProfileImageUpdatable = Integer.parseInt(String.valueOf(args[3]));
                Log.d("log-onCheckContactOnlineStatus_return", "contact_id: " + CID);
                Log.d("log-onCheckContactOnlineStatus_return", "contact_id: " + contact_about);
                Log.d("log-onCheckContactOnlineStatus_return", "display_name: " + display_name);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contact_about_details.setText(contact_about);
                        contact_display_name.setText(display_name);
                        if (display_name == null) {
                            contact_display_name.setText("not set");
                        }
                    }
                });

                if (ProfileImageUpdatable == 1) {
                    MassegeDao massegeDao = db.massegeDao();
                    long profileImageVersion = massegeDao.getContactProfileImageVersion(CID, user_login_id);
                    JSONArray jsonArray = new JSONArray();
                    try {
                        JSONObject tmp = new JSONObject();
                        tmp.put("_id", CID);
                        tmp.put("Number", ContactMobileNumber);
                        tmp.put("ProfileImageVersion", profileImageVersion);
                        jsonArray.put(tmp);
                        Log.d("log-ContactDetailsFromMassegeViewPage", "image update part : " + jsonArray.toString());
                        socket.emit("updateProfileImages", user_login_id, jsonArray, 2);
                    } catch (Exception exception) {
                        Log.d("log-ContactListAdapter-Exception", exception.toString());
                    }
                }

            } catch (Exception e) {
                Log.d("log-onCheckContactOnlineStatus_return-exception", "Exception arive : " + e);
            }
        }
    };
    private final Emitter.Listener onUpdateSingleContactProfileImageToUserProfilePage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onUpdateSingleContactProfileImage", "onUpdateSingleContactProfileImage || start ");
            String userId = String.valueOf(args[0]);
            String id = String.valueOf(args[1]);
            long ProfileImageVersion = Long.parseLong(String.valueOf(args[3]));
            String profileImageBase64 = (String) args[2];
            try {
                byte[] profileImageByteArray = Base64.decode(profileImageBase64, Base64.DEFAULT);

                if (profileImageByteArray.length > 0) {
                    synchronized (this) {
                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length);
                        contactListAdapter.practiceMethod(id, profileImageByteArray);// to update contactList
                        if (saveContactProfileImageToStorage(id, profileImageByteArray)) {
                            Log.d("log-saveImageToInternalStorage", "Saved image of size : " + profileImageByteArray.length + " and resolution : " + bitmapImage.getWidth() + "*" + bitmapImage.getHeight());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmapImage);
                            }
                        });
                    }
                }
                Log.d("log-onUpdateSingleContactProfileImage", "ProfileImageVersion : " + ProfileImageVersion + " and for cid : " + id + " bytearray : " + Arrays.toString(profileImageByteArray));
            } catch (Exception ex) {
                Log.d("log-onUpdateSingleContactProfileImage-Exception", ex.toString());
            }

        }
    };
}
