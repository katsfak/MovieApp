package com.example.movieapp.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@SuppressWarnings("unused")
@Entity(tableName = "user_db", indices = {@Index(value = "email", unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "is_logged_in")
    private boolean loggedIn;

    @ColumnInfo(name = "dark_mode_enabled")
    private boolean darkModeEnabled;

    public UserEntity(int id, String email, String password, boolean loggedIn, boolean darkModeEnabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.loggedIn = loggedIn;
        this.darkModeEnabled = darkModeEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }
}

