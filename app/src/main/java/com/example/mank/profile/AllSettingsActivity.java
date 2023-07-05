package com.example.mank.profile;

import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.user_login_id;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;
import com.example.mank.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AllSettingsActivity extends Activity {

    private TextView username, aboutInfo;
    private ImageView userProfileImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_settings);

        username = (TextView) findViewById(R.id.ASUsername);
        aboutInfo = (TextView) findViewById(R.id.ASAboutInfo);
        userProfileImage = (ImageView) findViewById(R.id.ASUserProfileImage);

        setUserDetails();
    }

    private void setUserDetails() {
        Thread ts = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    MassegeDao massegeDao = db.massegeDao();
                    loginDetailsEntity loginDetailsEntity = massegeDao.getLoginDetailsFromDatabase();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            username.setText(loginDetailsEntity.getDisplayUserName());
                            aboutInfo.setText(loginDetailsEntity.getAbout());
                        }
                    });
                }


                //setup image
                String imagePath = "/storage/emulated/0/Android/media/com.massenger.mank.main/Pictures/profiles/" + user_login_id + user_login_id + ".png";
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
                    Bitmap selfImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    Log.d("log-AllSettingsActivity", "setUserImage : after fetch image form file system : " + byteArray.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userProfileImage.setImageBitmap(selfImage);
                        }
                    });
                }
            }
        });
        ts.start();
    }

    public void SetBbForContactPageLabelOnClick(View view) {
        Intent intent = new Intent(this, BgImageSetForContactPage.class);
        startActivity(intent);
    }


    public void LaunchUserProfileActivity(View view) {
        Intent intent = new Intent(AllSettingsActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void FinishSettingActivity(View view) {
        this.finish();
    }

    public void ProfilePageMainLabelOnClick(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void ChatsPageLabelOnClick(View view) {
//        Intent intent = new Intent(this, UserProfileActivity.class);
//        startActivity(intent);
        Toast.makeText(AllSettingsActivity.this, "this option coming soon...", Toast.LENGTH_SHORT).show();
    }

    public void AccountPageLabelOnClick(View view) {
        Intent intent = new Intent(this, AccountSettingPage.class);
        startActivity(intent);
//        Toast.makeText(AllSettingsActivity.this, "this option coming soon...", Toast.LENGTH_SHORT).show();
    }

    public void PrivacyPageLabelOnClick(View view) {
//        Intent intent = new Intent(this, UserProfileActivity.class);
//        startActivity(intent);
        Toast.makeText(AllSettingsActivity.this, "this option coming soon...", Toast.LENGTH_SHORT).show();
    }


}
