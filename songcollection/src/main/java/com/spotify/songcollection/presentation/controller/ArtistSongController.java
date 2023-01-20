package com.spotify.songcollection.presentation.controller;

import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.service.ArtistSongService;
import com.spotify.songcollection.business.service.IdmClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/songcollection/artists/{uuid}/songs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistSongController {

    private final ArtistSongService artistSongService;
    private final IdmClient idmClient;

    public ArtistSongController(ArtistSongService artistSongService, IdmClient idmClient) {
        this.artistSongService = artistSongService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllArtistSongs(@PathVariable @Min(0) Integer uuid) {
        return new ResponseEntity<>(artistSongService.findAllByArtistId(uuid), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> assignSongToArtist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer uuid,
            @PathVariable @Min(0) Integer id
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isContentManagerOrOwner(token, uuid)) {
            artistSongService.create(uuid, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSongFromArtist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Size(max = 64) Integer uuid,
            @PathVariable @Size(max = 32) Integer id
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isContentManagerOrOwner(token, uuid)) {
            artistSongService.delete(uuid, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
