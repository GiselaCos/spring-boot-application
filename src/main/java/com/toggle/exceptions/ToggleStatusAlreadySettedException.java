package com.toggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.toggle.exceptions.ToggleStatusAlreadySettedException.MESSAGE;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason= MESSAGE)
public class ToggleStatusAlreadySettedException extends Exception{

    public static final String MESSAGE = "Toggle status altready has the received value, no need to update exception";

    public ToggleStatusAlreadySettedException() {
        super (MESSAGE);
    }

    public ToggleStatusAlreadySettedException(String message) {
        super (message);
    }
}