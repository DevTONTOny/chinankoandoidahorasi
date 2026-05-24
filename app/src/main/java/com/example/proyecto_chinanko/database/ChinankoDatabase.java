package com.example.proyecto_chinanko.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.proyecto_chinanko.dto.NotificationResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.dto.TravelResponse;

@Database(entities = {TravelResponse.class, NotificationResponse.class, SuggestedPointResponse.class}, version = 1, exportSchema = false)
public abstract class ChinankoDatabase extends RoomDatabase {

    private static ChinankoDatabase instance;

    public abstract ChinankoDao chinankoDao();

    public static synchronized ChinankoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ChinankoDatabase.class, "chinanko_offline_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}