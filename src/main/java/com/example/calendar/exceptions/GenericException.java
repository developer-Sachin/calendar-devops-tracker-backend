package com.example.calendar.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class GenericException extends RuntimeException{

    public GenericException(String message) {
        super(message);
    }
}
