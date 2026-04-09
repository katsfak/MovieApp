package com.example.movieapp.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@SuppressWarnings("unused")
@Dao
public interface UserDao {

    @Query("SELECT * FROM user_db WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM user_db WHERE is_logged_in = 1 LIMIT 1")
    UserEntity getLoggedInUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("UPDATE user_db SET is_logged_in = 0")
    void clearLoggedInUsers();
}

