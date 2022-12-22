package com.spotify.songcollection.business.util.mapper;

import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.persistence.entity.Song;
import org.mapstruct.Mapper;

@Mapper
public interface SongMapper {

    SongDTO toDTO(Song song);
    Song toEntity(SongDTO songDTO);
}
