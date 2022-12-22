package com.spotify.playlistcollection.persistence.document;

import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Playlist {

    private Integer id;

    private String name;

    @Valid
    private List<Song> songs;
}
