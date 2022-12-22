package com.spotify.songcollection.business.service;

import com.spotify.songcollection.business.model.ArtistDTO;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import com.spotify.songcollection.business.util.mapper.ArtistMapper;
import com.spotify.songcollection.persistence.entity.Artist;
import com.spotify.songcollection.persistence.repository.ArtistRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.spotify.songcollection.business.util.Constants.ARTIST_NOT_FOUND_MESSAGE;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    private final ArtistMapper artistMapper = Mappers.getMapper(ArtistMapper.class);

    // todo: create ArtistQuery
    public List<ArtistDTO> findAll(String name, Boolean exactMatch) {
        List<ArtistDTO> artists = artistRepository.findAll()
                .stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());

        if(name != null) {
            if(exactMatch) {
                return artists.stream()
                        .filter(artist -> artist.getName().equals(name))
                        .collect(Collectors.toList());
            } else {
                return artists.stream()
                        .filter(artist -> artist.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else {
            return artists;
        }
    }

    public ArtistDTO get(Integer id) {
        Artist artist = getArtistOrElseThrowException(id);

        return artistMapper.toDTO(artist);
    }

    public ArtistDTO save(Integer id, ArtistDTO artistDTO) {
        Artist artist = artistMapper.toEntity(artistDTO);

        artist.setId(id);
        Artist savedArtist = artistRepository.save(artist);

        return artistMapper.toDTO(savedArtist);
    }

    public void delete(Integer id) {
        Artist artist = getArtistOrElseThrowException(id);

        artistRepository.delete(artist);
    }

    public Artist getArtistOrElseThrowException(Integer id) {

        return artistRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(ARTIST_NOT_FOUND_MESSAGE, id))
        );
    }
}
