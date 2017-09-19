package com.toggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.toggle.exceptions.ToggleAlreadyExistsException.MESSAGE;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason=MESSAGE)
public class ToggleAlreadyExistsException extends Exception{

    public static final String MESSAGE = "Toggler already exists exception";

    public ToggleAlreadyExistsException() {
        super (MESSAGE);
    }

    public ToggleAlreadyExistsException(String message) {
        super (message);
    }
}