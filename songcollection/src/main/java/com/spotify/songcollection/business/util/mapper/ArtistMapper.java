package com.spotify.songcollection.business.util.mapper;

import com.spotify.songcollection.business.model.ArtistDTO;
import com.spotify.songcollection.persistence.entity.Artist;
import org.mapstruct.Mapper;

@Mapper
public interface ArtistMapper {

    ArtistDTO toDTO(Artist artist);
    Artist toEntity(ArtistDTO artistDTO);
}
