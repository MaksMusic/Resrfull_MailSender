package org.example.mailsender.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CodeExpiredException.class)
    public String handleCodeExpiredException(CodeExpiredException e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalCodeValueException.class)
    public String handleIllegalCodeValueException(IllegalCodeValueException e) {
        return e.getMessage();
    }

    @ExceptionHandler(TimeExpiredException.class)
    public String handleTimeExpiredException(TimeExpiredException e) {
        return e.getMessage();
    }
}
