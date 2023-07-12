package com.example.mank.configuration;

import android.Manifest;

public class permission_code {
    public static final int CAMERA_PERMISSION_CODE = 100;
    public static final int INTERNET_PERMISSION_CODE = 102;
    public static final int NETWORK_PERMISSION_CODE = 103;
    public static final int STORAGE_PERMISSION_CODE = 104;
    public static final int CONTACTS_PERMISSION_CODE = 105;


    public static final int PERMISSION_ALL = 1, PERMISSION_CONTACT_SYNC = 3, PERMISSION_initContentResolver = 2;
    public static final String[] PERMISSIONS = {android.Manifest.permission.INTERNET, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS,};
    public static final String[] STORAGE_PERMISSION = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};
    public static final String[] CONTACT_STORAGE_PERMISSION = {Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] NETWORK_PERMISSION = {Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.ACCESS_WIFI_STATE};
    public static final String[] CONTACT_PERMISSION = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};


}
