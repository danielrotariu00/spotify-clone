package com.spotify.playlistcollection.business.model;

import javax.validation.Valid;

import com.spotify.playlistcollection.persistence.document.Song;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@Builder
public class PlaylistDTO extends RepresentationModel<PlaylistDTO> {

    private Integer id;

    private String name;

    @Valid
    private List<Song> songs;
}
