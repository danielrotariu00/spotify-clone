package com.spotify.playlistcollection.business.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistRequest {

    @NotNull
    @Size(max = 255)
    private String name;
}
