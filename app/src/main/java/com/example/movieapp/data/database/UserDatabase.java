package com.example.movieapp.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@SuppressWarnings("unused")
@Database(entities = { UserEntity.class }, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao userDao();
}

