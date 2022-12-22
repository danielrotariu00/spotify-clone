package com.spotify.playlistcollection.business.util.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ExceptionWithStatus {

    public AlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.CONFLICT);
    }
}
