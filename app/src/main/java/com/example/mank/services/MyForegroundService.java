package com.example.mank.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mank.MainActivity;
import com.example.mank.R;

public class MyForegroundService extends Service {
    // Define variables and objects needed for your foreground service

    public int NOTIFICATION_ID = 111;

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform initialization tasks for your foreground service
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start your foreground service and perform tasks here

        // Create and configure the notification for your foreground service

        Thread tx = new Thread(new Runnable() {
            @Override
            public void run() {
                long timer = 0;
                while (true) {
                    Log.d("log-foreground-service", "service running from " + timer + " seconds");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.d("log-foreground-service-exception", e.toString());
                    }
                    timer += 5000;
                }
            }
        });
        tx.start();
        // Start the foreground service with a notification
//        Notification notification = createNotification();
//        startForeground(NOTIFICATION_ID, notification);

        // Return the appropriate flag based on your requirements
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources and perform any necessary cleanup tasks
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // If your service does not support binding, return null
        return null;
    }

    // Create a notification for your foreground service
    private Notification createNotification() {
        // Create a notification using the NotificationCompat.Builder
        String CHANNEL_ID = "channel_name";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Foreground Service")
                .setContentText("Running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create a PendingIntent for the notification (optional)
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Build and return the notification
        return builder.build();
    }
}
