package com.example.mank.services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;

import android.os.Handler;

public class MyContentObserver extends ContentObserver {

    public MyContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // handle the change in contacts



    }
}
