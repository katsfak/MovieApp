package com.example.movieapp.data.auth;

import com.example.movieapp.data.database.UserDao;
import com.example.movieapp.data.database.UserEntity;

import javax.inject.Inject;

public class AuthManager {

    private final UserDao userDao;

    @Inject
    public AuthManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isEmailInUse(String email) {
        return userDao.getUserByEmail(email) != null;
    }

    public boolean signUp(String email, String password) {
        if (isEmailInUse(email)) {
            return false;
        }

        userDao.clearLoggedInUsers();
        userDao.insert(new UserEntity(0, email, password, true, false));
        return true;
    }

    public boolean login(String email, String password) {
        UserEntity user = userDao.getUserByEmail(email);
        boolean isValid = user != null && password.equals(user.getPassword());

        if (isValid) {
            userDao.clearLoggedInUsers();
            user.setLoggedIn(true);
            userDao.update(user);
        }
        return isValid;
    }

    public void logout() {
        userDao.clearLoggedInUsers();
    }

    public boolean isLoggedIn() {
        return userDao.getLoggedInUser() != null;
    }

    public String getRegisteredEmail() {
        UserEntity user = getCurrentUser();
        return user != null ? user.getEmail() : "";
    }

    public String getRegisteredPassword() {
        UserEntity user = getCurrentUser();
        return user != null ? user.getPassword() : "";
    }

    public boolean updateCredentials(String email, String password) {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return false;
        }

        if (!email.equals(user.getEmail()) && isEmailInUse(email)) {
            return false;
        }

        user.setEmail(email);
        user.setPassword(password);
        userDao.update(user);
        return true;
    }

    public void setDarkModeEnabled(boolean enabled) {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return;
        }

        user.setDarkModeEnabled(enabled);
        userDao.update(user);
    }

    public boolean isDarkModeEnabled() {
        UserEntity user = getCurrentUser();
        return user != null && user.isDarkModeEnabled();
    }

    private UserEntity getCurrentUser() {
        return userDao.getLoggedInUser();
    }
}
