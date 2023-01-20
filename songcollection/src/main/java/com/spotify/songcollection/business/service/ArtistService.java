package com.spotify.songcollection.business.service;

import com.spotify.songcollection.business.model.ArtistDTO;
import com.spotify.songcollection.business.model.GetArtistsDTO;
import com.spotify.songcollection.business.model.GetSongsDTO;
import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import com.spotify.songcollection.business.util.mapper.ArtistMapper;
import com.spotify.songcollection.persistence.entity.Artist;
import com.spotify.songcollection.persistence.repository.ArtistRepository;
import com.spotify.songcollection.presentation.controller.ArtistController;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.spotify.songcollection.business.util.Constants.ARTIST_NOT_FOUND_MESSAGE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    private final ArtistMapper artistMapper = Mappers.getMapper(ArtistMapper.class);

    public GetArtistsDTO findAll(String name,
                                   Boolean exactMatch,
                                   Integer page,
                                   Integer maxItems) {
        Pageable pageable;
        Artist exampleArtist = new Artist();

        exampleArtist.setName(name);

        if(page != null) {
            pageable = PageRequest.of(page, maxItems);
        } else {
            pageable = PageRequest.of(0, maxItems);
        }

        if (exactMatch) {
            Page<Artist> artistPage = artistRepository.findAll(Example.of(exampleArtist), pageable);
            List<ArtistDTO> artists = artistPage
                    .stream()
                    .map(artistMapper::toDTO)
                    .map(this::addLinks)
                    .collect(Collectors.toList());

            return GetArtistsDTO.builder()
                    .artists(artists)
                    .totalElements(artistPage.getTotalElements())
                    .build();
        } else {
            ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll()
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

            Page<Artist> artistPage = artistRepository.findAll(Example.of(exampleArtist, customExampleMatcher), pageable);
            List<ArtistDTO> artists = artistPage
                    .stream()
                    .map(artistMapper::toDTO)
                    .map(this::addLinks)
                    .collect(Collectors.toList());

            return GetArtistsDTO.builder()
                    .artists(artists)
                    .totalElements(artistPage.getTotalElements())
                    .build();
        }
    }

    public ArtistDTO get(Integer id) {
        Artist artist = getArtistOrElseThrowException(id);

        return addLinks(artistMapper.toDTO(artist));
    }

    public ArtistDTO save(Integer id, ArtistDTO artistDTO) {
        Artist artist = artistMapper.toEntity(artistDTO);

        artist.setId(id);
        Artist savedArtist = artistRepository.save(artist);

        return addLinks(artistMapper.toDTO(savedArtist));
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

    private ArtistDTO addLinks(ArtistDTO artistDTO) {
        Link selfLink = linkTo(methodOn(ArtistController.class)
                .getArtist(artistDTO.getId()))
                .withSelfRel();
        Link parentLink = linkTo(methodOn(ArtistController.class)
                .getAllArtists(null, null, null, null))
                .withRel("parent");

        artistDTO.add(selfLink);
        artistDTO.add(parentLink);

        return artistDTO;
    }
}
