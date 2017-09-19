package com.toggle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.toggle.exceptions.ToggleStatusNotValidException.MESSAGE;


@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason= MESSAGE)
public class ToggleStatusNotValidException extends Exception{
    public static final String MESSAGE = "Feature status not valid exception (accepted options: true|false)";

    public ToggleStatusNotValidException() {
        super (MESSAGE);
    }

    public ToggleStatusNotValidException(String message) {
        super (message);
    }
}
