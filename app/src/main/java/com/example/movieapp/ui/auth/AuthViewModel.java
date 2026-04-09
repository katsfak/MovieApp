package com.example.movieapp.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.auth.AuthManager;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthManager authManager;
    private final MutableLiveData<LoginSignupUiState> _loginSignupUiState = new MutableLiveData<>(
            LoginSignupUiState.idle());
    public final LiveData<LoginSignupUiState> loginSignupUiState = _loginSignupUiState;

    private final MutableLiveData<ProfileUiState> _profileUiState = new MutableLiveData<>(ProfileUiState.idle());
    public final LiveData<ProfileUiState> profileUiState = _profileUiState;

    @Inject
    public AuthViewModel(AuthManager authManager) {
        this.authManager = authManager;
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

        if (authManager.isEmailInUse(email)) {
            _loginSignupUiState.setValue(LoginSignupUiState.error("This email is already registered."));
            return;
        }

        if (authManager.signUp(email, password)) {
            _loginSignupUiState.setValue(LoginSignupUiState.authenticated());
        } else {
            _loginSignupUiState.setValue(LoginSignupUiState.error("This email is already registered."));
        }
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

        String currentEmail = authManager.getRegisteredEmail();
        if (!email.equals(currentEmail) && authManager.isEmailInUse(email)) {
            _profileUiState.setValue(ProfileUiState.error("This email is already in use by another account."));
            return;
        }

        if (authManager.updateCredentials(email, password)) {
            _profileUiState.setValue(ProfileUiState.credentialsUpdated(
                    email,
                    maskPassword(password),
                    "Credentials updated successfully.",
                    authManager.isDarkModeEnabled()));
        } else {
            _profileUiState.setValue(ProfileUiState.error("Could not update your account details."));
        }
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
