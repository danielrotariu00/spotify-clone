package com.spotify.songcollection.business.service;

import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.util.exception.AlreadyExistsException;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import com.spotify.songcollection.persistence.entity.Artist;
import com.spotify.songcollection.persistence.entity.ArtistSong;
import com.spotify.songcollection.persistence.entity.Song;
import com.spotify.songcollection.persistence.repository.ArtistSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spotify.songcollection.business.util.Constants.SONG_ALREADY_EXISTS_FOR_ARTIST_MESSAGE;
import static com.spotify.songcollection.business.util.Constants.SONG_NOT_FOUND_MESSAGE;

@Service
public class ArtistSongService {

    @Autowired
    private ArtistService artistService;
    @Autowired
    private SongService songService;
    @Autowired
    private ArtistSongRepository artistSongRepository;

    public List<SongDTO> findAllByArtistId(Integer uuid) {
        artistService.getArtistOrElseThrowException(uuid);

        return artistSongRepository.findArtistSongsByArtist_Id(uuid)
                .stream()
                .map(artistSong -> songService.get(artistSong.getSong().getId()))
                .collect(Collectors.toList());
    }

    public boolean existsByArtistIdAndSongId(Integer artistId, Integer songId) {
        return artistSongRepository.existsByArtistIdAndSongId(artistId, songId);
    }

    public void create(Integer artistId, Integer songId) {
        Artist artist = artistService.getArtistOrElseThrowException(artistId);
        Song song = songService.getSongOrElseThrowException(songId);

        Optional<ArtistSong> artistSongOptional = artistSongRepository.findArtistSongByArtist_IdAndSong_Id(artistId, songId);

        if(artistSongOptional.isPresent()) {
            throw new AlreadyExistsException(String.format(SONG_ALREADY_EXISTS_FOR_ARTIST_MESSAGE, songId, artistId));
        }

        ArtistSong artistSong = new ArtistSong();
        artistSong.setArtist(artist);
        artistSong.setSong(song);

        artistSongRepository.save(artistSong);
    }

    public void delete(Integer artistId, Integer songId) {
        artistService.getArtistOrElseThrowException(artistId);
        songService.getSongOrElseThrowException(songId);

        ArtistSong artistSong = artistSongRepository.findArtistSongByArtist_IdAndSong_Id(artistId, songId)
                .orElseThrow(() -> new NotFoundException(String.format(SONG_NOT_FOUND_MESSAGE, songId)));

        artistSongRepository.delete(artistSong);
    }

    public void deleteAllByArtistId(Integer artistId) {
        artistSongRepository.deleteAllByArtistId(artistId);
    }

    public void deleteAllBySongId(Integer songId) {
        artistSongRepository.deleteAllBySongId(songId);
    }
}
