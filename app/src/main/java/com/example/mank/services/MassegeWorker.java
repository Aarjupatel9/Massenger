package com.example.mank.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class MassegeWorker extends Worker {

        public MassegeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }


        @NonNull
        @Override
        public Result doWork() {
            // Perform your background task here
            // ...

            long timer = 0;
            while (true) {
                Log.d("log-MainWorker", "worker thread is running from " + timer + " seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.d("log-MainWorker-exception", e.toString());
                }
                timer += 5000;
                if (timer < -1) {
                    break;
                }
            }

            // Return the result of the task
            return Result.success();
        }
        public static void startWorker(Context context) {
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(MassegeWorker.class)
                            .build();

            WorkManager.getInstance(context)
                    .beginUniqueWork("main_worker", ExistingWorkPolicy.KEEP, workRequest)
                    .enqueue();
        }
    }
