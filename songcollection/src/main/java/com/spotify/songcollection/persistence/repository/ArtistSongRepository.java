package com.spotify.songcollection.persistence.repository;

import com.spotify.songcollection.persistence.entity.ArtistSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ArtistSongRepository extends JpaRepository<ArtistSong, Integer> {
    List<ArtistSong> findArtistSongsByArtist_Id(Integer uuid);
    Optional<ArtistSong> findArtistSongByArtist_IdAndSong_Id(Integer uuid, Integer id);
}
