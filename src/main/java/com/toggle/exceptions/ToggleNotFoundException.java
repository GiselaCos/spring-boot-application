package com.toggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.toggle.exceptions.ToggleNotFoundException.MESSAGE;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason= MESSAGE)
public class ToggleNotFoundException extends Exception{

    public static final String MESSAGE = "Feature not found exception";

    public ToggleNotFoundException() {
        super (MESSAGE);
    }

    public ToggleNotFoundException(String message) {
        super (message);
    }
}
