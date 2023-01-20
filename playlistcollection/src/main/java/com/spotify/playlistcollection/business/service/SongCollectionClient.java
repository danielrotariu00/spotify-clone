package com.spotify.playlistcollection.business.service;

import com.spotify.playlistcollection.business.model.SongDTO;
import com.spotify.playlistcollection.business.util.exception.ExceptionWithStatus;
import com.spotify.playlistcollection.business.util.exception.NotFoundException;
import com.spotify.playlistcollection.persistence.document.Song;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SongCollectionClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final static String URL = "http://localhost:8081/api/songcollection/songs";
    private final String SONG_NOT_FOUND_MESSAGE = "Song does not exist.";

    public Song getSongById(Integer songId) {
        SongDTO songDTO;

        try {
            songDTO = restTemplate.getForObject(URL + String.format("/%d", songId), SongDTO.class);
        } catch(HttpClientErrorException e) {
            throw new ExceptionWithStatus(e.getMessage(), e.getStatusCode());
        }

        if (songDTO != null) {
            String selfLink = songDTO.get_links().getSelf().getHref();

            return Song.builder()
                    .id(songDTO.getId())
                    .name(songDTO.getName())
                    .selfLink(selfLink)
                    .build();
        } else {
            throw new NotFoundException(SONG_NOT_FOUND_MESSAGE);
        }
    }
}
