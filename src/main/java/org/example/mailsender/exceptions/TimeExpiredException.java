package org.example.mailsender.exceptions;

public class TimeExpiredException extends RuntimeException {
    public TimeExpiredException(String message) {
        super(message);
    }
}
