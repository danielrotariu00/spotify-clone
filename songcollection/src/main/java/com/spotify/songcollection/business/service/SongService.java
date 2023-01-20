package com.spotify.songcollection.business.service;

import com.spotify.songcollection.business.model.GetSongsDTO;
import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import com.spotify.songcollection.business.util.mapper.SongMapper;
import com.spotify.songcollection.persistence.entity.Genre;
import com.spotify.songcollection.persistence.entity.Song;
import com.spotify.songcollection.persistence.repository.SongRepository;
import com.spotify.songcollection.presentation.controller.SongController;
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

import static com.spotify.songcollection.business.util.Constants.SONG_NOT_FOUND_MESSAGE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    private final SongMapper songMapper = Mappers.getMapper(SongMapper.class);

    public GetSongsDTO findAll(String name,
                               Boolean exactMatch,
                               Genre genre,
                               Integer year,
                               Integer page,
                               Integer maxItems) {
        Pageable pageable;
        Song exampleSong = new Song();

        exampleSong.setName(name);
        exampleSong.setGenre(genre);
        exampleSong.setYear(year);

        if(page != null) {
            pageable = PageRequest.of(page, maxItems);
        } else {
            pageable = PageRequest.of(0, maxItems);
        }

        if (exactMatch) {
            Page<Song> songPage = songRepository.findAll(Example.of(exampleSong), pageable);
            List<SongDTO> songs = songPage
                    .stream()
                    .map(songMapper::toDTO)
                    .map(this::addLinks)
                    .collect(Collectors.toList());

            return GetSongsDTO.builder()
                    .songs(songs)
                    .totalElements(songPage.getTotalElements())
                    .build();

        } else {
            ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll()
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

            Page<Song> songPage = songRepository.findAll(Example.of(exampleSong, customExampleMatcher), pageable);
            List<SongDTO> songs = songPage
                    .stream()
                    .map(songMapper::toDTO)
                    .map(this::addLinks)
                    .collect(Collectors.toList());

            return GetSongsDTO.builder()
                    .songs(songs)
                    .totalElements(songPage.getTotalElements())
                    .build();
        }
    }

    public boolean existsById(Integer id) {
        return songRepository.existsById(id);
    }

    public SongDTO get(Integer id) {
        Song song = getSongOrElseThrowException(id);

        return addLinks(songMapper.toDTO(song));
    }

    public SongDTO create(SongDTO songDTO) {
        Song song = songMapper.toEntity(songDTO);

        return addLinks(songMapper.toDTO(songRepository.save(song)));
    }

    public SongDTO update(Integer id, SongDTO songDTO) {
        Song song = getSongOrElseThrowException(id);

        Song updatedSong = songMapper.toEntity(songDTO);
        updatedSong.setId(song.getId());

        return addLinks(songMapper.toDTO(songRepository.save(updatedSong)));
    }

    public void delete(Integer id) {
        Song song = getSongOrElseThrowException(id);

        songRepository.delete(song);
    }

    public Song getSongOrElseThrowException(Integer id) {

        return songRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(SONG_NOT_FOUND_MESSAGE, id))
        );
    }

    private SongDTO addLinks(SongDTO songDTO) {
        Link selfLink = linkTo(methodOn(SongController.class)
                .getSong(songDTO.getId()))
                .withSelfRel();
        Link parentLink = linkTo(methodOn(SongController.class)
                .getAllSongs(null, null, null, null, null, null))
                .withRel("parent");
        Link playlistLink = Link.of(
                String.format("http://localhost:8082/api/users/{userId}/playlists/{playlistId}/songs/%d", songDTO.getId()),
                "addToPlaylist");

        songDTO.add(selfLink);
        songDTO.add(parentLink);
        songDTO.add(playlistLink);

        return songDTO;
    }
}
