package com.spotify.songcollection.business.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetSongsDTO {

    private List<SongDTO> songs;
    private Long totalElements;
}
