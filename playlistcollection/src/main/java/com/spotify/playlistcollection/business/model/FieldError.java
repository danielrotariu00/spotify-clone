package com.spotify.playlistcollection.business.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {

    private String field;
    private String errorCode;
}
