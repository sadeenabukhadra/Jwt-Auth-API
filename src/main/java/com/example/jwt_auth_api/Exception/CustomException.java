package com.example.jwt_auth_api.Exception;

public class CustomException extends RuntimeException {
    private final int status;

    public CustomException(String message) {
        super(message);
        this.status = 400;
    }

    public CustomException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
