package com.example.mank.ThreadPackages;

import android.content.Context;

import com.example.mank.MediaPlayerClasses.DotSound;

public class MassegePopSoundThread extends  Thread{

    private Context context;
    private int id;
    public MassegePopSoundThread(Context context, int id){
        this.context= context;
        this.id = id;
    }

    @Override
    public void run() {
        DotSound ma = new DotSound(context, id);
        ma.massegePopPlay();
    }

}
