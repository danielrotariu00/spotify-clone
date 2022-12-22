package com.spotify.playlistcollection.business.service;

import com.spotify.playlistcollection.business.model.SongDTO;
import com.spotify.playlistcollection.business.util.exception.NotFoundException;
import com.spotify.playlistcollection.persistence.document.Song;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SongCollectionClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final static String URL = "http://localhost:8080//api/songcollection/songs";

    public Song getSongById(Integer songId) {
        SongDTO songDTO = restTemplate.getForObject(URL + String.format("/%d", songId), SongDTO.class);

        if (songDTO != null) {
            String selfLink = songDTO.get_links().getSelf().getHref();

            return Song.builder()
                    .id(songDTO.getId())
                    .name(songDTO.getName())
                    .selfLink(selfLink)
                    .build();
        } else {
            throw new NotFoundException("Song does not exist.");
        }
    }
}
