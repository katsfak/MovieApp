package com.example.movieapp.ui.auth;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.auth.AuthManager;

public class AuthViewModel extends AndroidViewModel {

    private final AuthManager authManager;
    private final MutableLiveData<LoginSignupUiState> _loginSignupUiState = new MutableLiveData<>(
            LoginSignupUiState.idle());
    public final LiveData<LoginSignupUiState> loginSignupUiState = _loginSignupUiState;

    private final MutableLiveData<ProfileUiState> _profileUiState = new MutableLiveData<>(ProfileUiState.idle());
    public final LiveData<ProfileUiState> profileUiState = _profileUiState;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authManager = new AuthManager(application.getApplicationContext());
    }

    public void checkSession() {
        if (authManager.isLoggedIn()) {
            _loginSignupUiState.setValue(LoginSignupUiState.authenticated());
        } else {
            _loginSignupUiState.setValue(LoginSignupUiState.idle());
        }
    }

    public void login(String email, String password) {
        _loginSignupUiState.setValue(LoginSignupUiState.loading());

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            _loginSignupUiState.setValue(LoginSignupUiState.error("Please fill in email and password"));
            return;
        }

        if (authManager.login(email, password)) {
            _loginSignupUiState.setValue(LoginSignupUiState.authenticated());
        } else {
            _loginSignupUiState.setValue(LoginSignupUiState.error("Invalid credentials"));
        }
    }

    public void signUp(String email, String password) {
        _loginSignupUiState.setValue(LoginSignupUiState.loading());

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            _loginSignupUiState.setValue(LoginSignupUiState.error("Please fill in email and password"));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginSignupUiState.setValue(LoginSignupUiState.error("Please enter a valid email"));
            return;
        }

        if (password.length() < 6) {
            _loginSignupUiState.setValue(LoginSignupUiState.error("Password must be at least 6 characters"));
            return;
        }

        authManager.signUp(email, password);
        _loginSignupUiState.setValue(LoginSignupUiState.authenticated());
    }

    public void loadProfile() {
        _profileUiState.setValue(ProfileUiState.loading());
        String email = authManager.getRegisteredEmail();
        String password = authManager.getRegisteredPassword();
        _profileUiState.setValue(ProfileUiState.profileLoaded(
                email,
                maskPassword(password),
                authManager.isDarkModeEnabled()));
    }

    public void updateCredentials(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            _profileUiState.setValue(ProfileUiState.error("Email and password are required."));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _profileUiState.setValue(ProfileUiState.error("Please enter a valid email address."));
            return;
        }

        if (password.length() < 6) {
            _profileUiState.setValue(ProfileUiState.error("Password must be at least 6 characters."));
            return;
        }

        authManager.updateCredentials(email, password);
        _profileUiState.setValue(ProfileUiState.credentialsUpdated(
                email,
                maskPassword(password),
                "Credentials updated successfully.",
                authManager.isDarkModeEnabled()));
    }

    public void updateDarkMode(boolean enabled) {
        authManager.setDarkModeEnabled(enabled);
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        _profileUiState.setValue(ProfileUiState.profileLoaded(
                authManager.getRegisteredEmail(),
                maskPassword(authManager.getRegisteredPassword()),
                enabled));
    }

    public void logout() {
        authManager.logout();
        _profileUiState.setValue(ProfileUiState.loggedOut());
        _loginSignupUiState.setValue(LoginSignupUiState.idle());
    }

    public String getRawEmail() {
        return authManager.getRegisteredEmail();
    }

    public String getRawPassword() {
        return authManager.getRegisteredPassword();
    }

    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            masked.append('•');
        }
        return masked.toString();
    }
}
