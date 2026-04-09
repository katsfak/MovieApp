package com.example.movieapp.ui.auth;

public class ProfileUiState {

    public enum Status {
        IDLE,
        LOADING,
        PROFILE_LOADED,
        CREDENTIALS_UPDATED,
        LOGGED_OUT,
        ERROR
    }

    private final Status status;
    private final String email;
    private final String maskedPassword;
    private final String message;
    private final boolean darkModeEnabled;

    private ProfileUiState(Status status, String email, String maskedPassword, String message,
            boolean darkModeEnabled) {
        this.status = status;
        this.email = email;
        this.maskedPassword = maskedPassword;
        this.message = message;
        this.darkModeEnabled = darkModeEnabled;
    }

    public static ProfileUiState idle() {
        return new ProfileUiState(Status.IDLE, "", "", null, false);
    }

    public static ProfileUiState loading() {
        return new ProfileUiState(Status.LOADING, "", "", null, false);
    }

    public static ProfileUiState profileLoaded(String email, String maskedPassword, boolean darkModeEnabled) {
        return new ProfileUiState(Status.PROFILE_LOADED, email, maskedPassword, null, darkModeEnabled);
    }

    public static ProfileUiState credentialsUpdated(String email,
            String maskedPassword,
            String message,
            boolean darkModeEnabled) {
        return new ProfileUiState(Status.CREDENTIALS_UPDATED, email, maskedPassword, message, darkModeEnabled);
    }

    public static ProfileUiState loggedOut() {
        return new ProfileUiState(Status.LOGGED_OUT, "", "", null, false);
    }

    public static ProfileUiState error(String message) {
        return new ProfileUiState(Status.ERROR, "", "", message, false);
    }

    public Status getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getMaskedPassword() {
        return maskedPassword;
    }

    public String getMessage() {
        return message;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }
}
