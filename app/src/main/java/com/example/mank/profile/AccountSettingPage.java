package com.example.mank.profile;

import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.user_login_id;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.MainActivity;
import com.example.mank.R;

public class AccountSettingPage extends Activity {

    private MassegeDao massegeDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings_page);
        massegeDao = db.massegeDao();
    }

    public void finishAccountSetting(View view){
        this.finish();
    }

    public void ACSPLogout(View view) {
        massegeDao.LogOutFromAppForThisUser(user_login_id);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Log.d("log-AccountSettingPage", "After intent creation");
    }
}
