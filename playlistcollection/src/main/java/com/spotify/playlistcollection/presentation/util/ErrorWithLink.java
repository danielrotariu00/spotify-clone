package com.spotify.playlistcollection.presentation.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
public class ErrorWithLink extends RepresentationModel<ErrorWithLink> {

    private String message;
}
