package com.example.mank.profile;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mank.R;

import java.io.IOException;

public class SettingsOptionPage extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activtity_settings_option_page);

    }

    public void ProfilePageMainLabelOnClick(View view) {
        Intent intent = new Intent(this, ProfileUploadActivity.class);
        startActivity(intent);
    }

    public void SetBbForContactPageLabelOnClick(View view) {
        Intent intent = new Intent(this, BgImageSetForContactPage.class);
        startActivity(intent);
    }

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO

    public void startRecordingButton(View view){

//        File MyFile = new File(ge);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
//        recorder.setOutputFile(MyFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.d("log-SettingsOptionPage", "prepare() failed e:"+e);
        }

        recorder.start();

    }

    private void stopRecordingButton(View view) {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

}
