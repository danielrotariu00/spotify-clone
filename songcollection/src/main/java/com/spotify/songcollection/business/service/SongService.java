package com.spotify.songcollection.business.service;

import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import com.spotify.songcollection.business.util.mapper.SongMapper;
import com.spotify.songcollection.persistence.entity.Song;
import com.spotify.songcollection.persistence.repository.SongRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.spotify.songcollection.business.util.Constants.SONG_NOT_FOUND_MESSAGE;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    private final SongMapper songMapper = Mappers.getMapper(SongMapper.class);

    // todo: create SongQuery
    public List<SongDTO> findAll(Integer page, Integer maxItems) {
        Pageable pageable;

        if(page != null) {
            pageable = PageRequest.of(page, maxItems);
        } else {
            pageable = PageRequest.of(0, maxItems);
        }

        return songRepository.findAll(pageable)
                .stream()
                .map(songMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SongDTO get(Integer id) {
        Song song = getSongOrElseThrowException(id);

        return songMapper.toDTO(song);
    }

    public SongDTO create(SongDTO songDTO) {
        Song song = songMapper.toEntity(songDTO);

        return songMapper.toDTO(songRepository.save(song));
    }

    public void update(Integer id, SongDTO songDTO) {
        Song song = getSongOrElseThrowException(id);

        Song updatedSong = songMapper.toEntity(songDTO);
        updatedSong.setId(song.getId());

        songRepository.save(updatedSong);
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
}
