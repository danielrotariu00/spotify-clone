package com.spotify.songcollection.business.util.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionWithStatus extends RuntimeException{

    private final HttpStatus status;

    @Builder
    public ExceptionWithStatus(String errorMessage, HttpStatus status) {
        super(errorMessage);

        this.status = status;
    }
}
