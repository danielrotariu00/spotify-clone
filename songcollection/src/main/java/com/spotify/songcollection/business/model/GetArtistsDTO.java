package com.spotify.songcollection.business.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetArtistsDTO {

    private List<ArtistDTO> artists;
    private Long totalElements;
}
