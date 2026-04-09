package com.example.movieapp.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {

    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_DARK_MODE_ENABLED = "dark_mode_enabled";

    private final SharedPreferences prefs;

    public AuthManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void signUp(String email, String password) {
        prefs.edit()
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PASSWORD, password)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public boolean login(String email, String password) {
        String storedEmail = prefs.getString(KEY_USER_EMAIL, "");
        String storedPassword = prefs.getString(KEY_USER_PASSWORD, "");
        boolean isValid = email.equals(storedEmail) && password.equals(storedPassword);

        if (isValid) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply();
        }
        return isValid;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean hasRegisteredUser() {
        return !prefs.getString(KEY_USER_EMAIL, "").isEmpty();
    }

    public String getRegisteredEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getRegisteredPassword() {
        return prefs.getString(KEY_USER_PASSWORD, "");
    }

    public void updateCredentials(String email, String password) {
        prefs.edit()
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PASSWORD, password)
                .apply();
    }

    public void setDarkModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE_ENABLED, false);
    }
}
