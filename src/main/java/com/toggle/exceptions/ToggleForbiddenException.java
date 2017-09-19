package com.toggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.toggle.exceptions.ToggleForbiddenException.MESSAGE;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason=MESSAGE)
public class ToggleForbiddenException extends Exception{

    public static final String MESSAGE = "Toggle forbidden exception";

    public ToggleForbiddenException() {
        super (MESSAGE);
    }

    public ToggleForbiddenException(String message) {
        super (message);
    }
}
