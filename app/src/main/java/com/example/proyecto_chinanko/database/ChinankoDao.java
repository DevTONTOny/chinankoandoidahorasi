package com.example.proyecto_chinanko.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyecto_chinanko.dto.NotificationResponse;
import com.example.proyecto_chinanko.dto.SuggestedPointResponse;
import com.example.proyecto_chinanko.dto.TravelResponse;

import java.util.List;

@Dao
public interface ChinankoDao {


    @Query("SELECT * FROM travels")
    List<TravelResponse> getLocalTravels();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTravels(List<TravelResponse> travels);

    @Query("DELETE FROM travels")
    void clearTravels();


    @Query("SELECT * FROM notifications")
    List<NotificationResponse> getLocalNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotifications(List<NotificationResponse> notifications);

    @Query("DELETE FROM notifications")
    void clearNotifications();


    @Query("SELECT * FROM suggestions")
    List<SuggestedPointResponse> getLocalSuggestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSuggestions(List<SuggestedPointResponse> suggestions);

    @Query("DELETE FROM suggestions")
    void clearSuggestions();

    @androidx.room.Query("DELETE FROM notifications WHERE id = :notifId")
    void deleteNotificationLocal(Long notifId);

    @androidx.room.Query("SELECT * FROM notifications WHERE isRead = 0")
    java.util.List<com.example.proyecto_chinanko.dto.NotificationResponse> getLocalUnreadNotifications();

}