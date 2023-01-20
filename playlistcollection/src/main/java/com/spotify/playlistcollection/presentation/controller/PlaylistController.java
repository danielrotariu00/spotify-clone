package com.spotify.playlistcollection.presentation.controller;

import com.spotify.playlistcollection.business.model.PlaylistDTO;
import com.spotify.playlistcollection.business.model.PlaylistRequest;
import com.spotify.playlistcollection.business.service.IdmClient;
import com.spotify.playlistcollection.business.service.PlaylistService;
import com.spotify.playlistcollection.persistence.document.Playlist;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/users/{userId}/playlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlaylistController {

    private final PlaylistService playlistService;
    private final IdmClient idmClient;

    public PlaylistController(PlaylistService playlistService, IdmClient idmClient) {
        this.playlistService = playlistService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getPlaylistsByUserId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @RequestParam(required = false) @Size(max = 32) String name
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            List<PlaylistDTO> playlists = playlistService.getPlaylistsByUserId(userId, name);
            return new ResponseEntity<>(playlists, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistByUserIdAndPlaylistId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @PathVariable @Min(0) Integer playlistId
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(playlistService.getPlaylistByUserIdAndPlaylistId(userId, playlistId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @RequestBody @Valid PlaylistRequest playlistRequest
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(playlistService.createPlaylist(userId, playlistRequest), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @PathVariable @Min(0) Integer playlistId
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            playlistService.deletePlaylist(userId, playlistId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> addSongToPlaylist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @PathVariable @Min(0) Integer playlistId,
            @PathVariable @Min(0) Integer songId
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            playlistService.addSongToPlaylist(userId, playlistId, songId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> deleteSongFromPlaylist(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer userId,
            @PathVariable @Min(0) Integer playlistId,
            @PathVariable @Min(0) Integer songId
    ) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            playlistService.deleteSongFromPlaylist(userId, playlistId, songId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
