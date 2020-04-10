package com.example.imageupload;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Photo.class}, version = 1, exportSchema = false)
public abstract class PhotoDb extends RoomDatabase {

    public abstract PhotoDao photoDao();

    private static volatile PhotoDb INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static PhotoDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PhotoDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PhotoDb.class, "photo_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
