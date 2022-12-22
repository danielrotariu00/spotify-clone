package com.spotify.playlistcollection.persistence.document;

import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@Builder
public class User {

    @Id
    private Integer id;

    @Valid
    private List<Playlist> playlists;
}
