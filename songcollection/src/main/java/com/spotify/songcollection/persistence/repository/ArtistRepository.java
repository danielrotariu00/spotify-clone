package com.spotify.songcollection.persistence.repository;

import com.spotify.songcollection.persistence.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArtistRepository extends JpaRepository<Artist, Integer> {
}
