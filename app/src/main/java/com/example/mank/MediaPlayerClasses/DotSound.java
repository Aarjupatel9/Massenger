package com.example.mank.MediaPlayerClasses;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.mank.R;

public class DotSound {
    public MediaPlayer mp;
    private Context context;

    public DotSound(Context context, int id) {
        this.context = context;
        if (id == 0) {
            mp = MediaPlayer.create(context, R.raw.massege_pop_alert);
        } else if (id == 1) {
            mp = MediaPlayer.create(context, R.raw.massege_pop_pup_notification_alert);
        }else if(id ==10){
            //long music will play
            mp = MediaPlayer.create(context, R.raw.dil_meri_na_sune);
        }
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void massegePopPlay() {
        mp.start();
    }

    public void massegePopStart() {
        mp.stop();
    }
}
