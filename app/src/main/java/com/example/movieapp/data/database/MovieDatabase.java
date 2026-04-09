package com.example.movieapp.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { MovieEntity.class }, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
}
