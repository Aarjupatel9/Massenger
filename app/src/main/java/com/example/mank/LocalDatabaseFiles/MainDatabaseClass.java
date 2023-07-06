package com.example.mank.LocalDatabaseFiles;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.LocalDatabaseFiles.entities.SetupFirstTimeEntity;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MassegeEntity.class, loginDetailsEntity.class, ContactWithMassengerEntity.class , SetupFirstTimeEntity.class, AllContactOfUserEntity.class}, version = 5)
public abstract class MainDatabaseClass extends RoomDatabase {
    public abstract MassegeDao massegeDao();

    private static volatile MainDatabaseClass INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MainDatabaseClass getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MainDatabaseClass.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MainDatabaseClass.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
