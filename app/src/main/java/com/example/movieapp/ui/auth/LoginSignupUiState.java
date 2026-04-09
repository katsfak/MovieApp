package com.example.movieapp.ui.auth;

public class LoginSignupUiState {

    public enum Status {
        IDLE,
        LOADING,
        AUTHENTICATED,
        ERROR
    }

    private final Status status;
    private final String message;

    private LoginSignupUiState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static LoginSignupUiState idle() {
        return new LoginSignupUiState(Status.IDLE, null);
    }

    public static LoginSignupUiState loading() {
        return new LoginSignupUiState(Status.LOADING, null);
    }

    public static LoginSignupUiState authenticated() {
        return new LoginSignupUiState(Status.AUTHENTICATED, null);
    }

    public static LoginSignupUiState error(String message) {
        return new LoginSignupUiState(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
