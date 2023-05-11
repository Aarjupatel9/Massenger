package com.example.mank.services;

//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.AddNewContactActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.HomePageWithContactActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.IncomingCallActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.LoginActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.MakeCallActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.RegisterActivityRunStatus;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.AppStatusVariables.StaticSocket;
//import static com.example.server_cl_com_emtyactivity.FunctionalityClasses.FunctionsStorage.showNotification;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("log-MyFirebaseMessagingService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("log-MyFirebaseMessagingService", "Message data payload: " + remoteMessage.getData());
            if (Objects.equals(remoteMessage.getData().get("massege_type"), "1")) {

                String massege_from = remoteMessage.getData().get("massege_from");
                String massegeOBJ = remoteMessage.getData().get("massegeOBJ");
                Log.d("log-MyFirebaseMessagingService", "massegeOBJ: " + massegeOBJ);

//                showPushNotification(this, "Massenger", "massege form "+massege_from, new Intent(this, ContactMassegeDetailsView.class), 201);

            }else {
                Log.d("log-MyFirebaseMessagingService", "call_type is other than 1");
            }
        }
    }

    public void  checkAppIsRunningStatus(String message){
//        boolean IsAppForGround = false;
//        if(AddNewContactActivityRunStatus){
//            IsAppForGround=true;
//        }if(HomePageWithContactActivityRunStatus){
//            IsAppForGround=true;
//        }if(IncomingCallActivityRunStatus){
//            IsAppForGround=true;
//        }if(LoginActivityRunStatus){
//            IsAppForGround=true;
//        }if(MakeCallActivityRunStatus){
//            IsAppForGround=true;
//        }if(RegisterActivityRunStatus){
//            IsAppForGround=true;
//        }
//        Intent IncomingCallIntent = new Intent(this, IncomingCallActivity.class);
//        if(IsAppForGround){
//            startActivity(IncomingCallIntent);
//        }else {
//            showNotification(this, "callOverInternet", message, IncomingCallIntent, 201);
//        }
    }
        

}
