package com.example.mank.LoginMenagement;

import android.util.Log;

import com.example.mank.LocalDatabaseFiles.DataContainerClasses.holdLoginData;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;

public class Login {

    public Login() {
    }

    public int isLogIn(MainDatabaseClass db) {
        Log.d("log-login-check method", "myfilereadmethod: enter in logIned check  method");

        holdLoginData hold_LoginData = new holdLoginData();
        loginDetailsEntity dataFromDatabase = hold_LoginData.getData();
        if (dataFromDatabase != null) {
            Log.d("log-in login check method  database data is ", " : " + dataFromDatabase.getPassword() + " qnd " + dataFromDatabase.getMobileNumber());
            int l = 0;
            long num = dataFromDatabase.getMobileNumber();
            while (num != 0) {
                num = num / 10;
                l++;
            }
            Log.d("log-in login check method  database data is ", " : " + l);

            if (l == 10) {
                return 1;
            } else {
                return 0;
            }
        }
        Log.d("log-login-check method", " not logIned cond. ");
        return 0;
    }

    public int getUserLoginId(MainDatabaseClass db) {
        Log.d("log-login-check method", "myfilereadmethod: enter in logIned check  method");

        holdLoginData hold_LoginData = new holdLoginData();
        loginDetailsEntity dataFromDatabase = hold_LoginData.getData();
        if (dataFromDatabase != null) {
            Log.d("log-in login check method  database data is ", " : " + dataFromDatabase.getPassword() + " qnd " + dataFromDatabase.getMobileNumber());
            String user_login_id = dataFromDatabase.getUID();
        }
        Log.d("log-login-check method", " not logIned cond. ");
        return 0;
    }

}
