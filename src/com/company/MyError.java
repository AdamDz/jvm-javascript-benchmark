package com.company;

public class MyError {
    private final String message;
    private final String code;

    public MyError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyError{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
