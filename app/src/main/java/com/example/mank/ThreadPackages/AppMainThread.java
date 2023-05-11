package com.example.mank.ThreadPackages;

import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AppMainThread extends  Thread{

    TimerTask timerTask;
    Timer online_status_checker_timer;
    Boolean toStopAppMainThread;
    JSONObject appConnectedObj;
    public AppMainThread(Boolean toStopAppMainThread){
        this.toStopAppMainThread =toStopAppMainThread;
        appConnectedObj = new JSONObject();
        try {
            appConnectedObj.put("user_id", user_login_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        if(toStopAppMainThread){
            online_status_checker_timer.cancel();
            online_status_checker_timer.purge();
        }else {
            online_status_checker_timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    socket.emit("user_app_connected_status", appConnectedObj);
                }
            };
            online_status_checker_timer.scheduleAtFixedRate(timerTask, 1, 1000);
        }
    }

}
