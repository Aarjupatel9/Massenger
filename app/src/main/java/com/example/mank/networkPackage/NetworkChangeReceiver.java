package com.example.mank.networkPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("log-NetworkChangeReceiver", "onReceive run start");
        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.d("log-NetworkChangeReceiver", "status:"+status);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
//                new ForceExitPause(context).execute();
                Log.d("log-NetworkChangeReceiver", "onReceive: network is not connected");
            } else {
//                new ResumeForceExitPause(context).execute();
                Log.d("log-NetworkChangeReceiver", "onReceive: network is  connected");
            }
        }
    }
}