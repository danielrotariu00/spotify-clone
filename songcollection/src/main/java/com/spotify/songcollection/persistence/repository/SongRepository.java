package com.spotify.songcollection.persistence.repository;

import com.spotify.songcollection.persistence.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SongRepository extends JpaRepository<Song, Integer> {
}
