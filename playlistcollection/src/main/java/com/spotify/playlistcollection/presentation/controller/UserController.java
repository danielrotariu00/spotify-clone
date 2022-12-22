package com.spotify.playlistcollection.presentation.controller;

import com.spotify.playlistcollection.business.model.PlaylistRequest;
import com.spotify.playlistcollection.business.service.IdmClient;
import com.spotify.playlistcollection.persistence.document.Playlist;
import com.spotify.playlistcollection.persistence.document.Song;
import com.spotify.playlistcollection.business.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users/{userId}/playlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final IdmClient idmClient;

    public UserController(UserService userService, IdmClient idmClient) {
        this.userService = userService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getPlaylistsByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                               @PathVariable Integer userId) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(userService.getPlaylistsByUserId(userId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<Playlist> getPlaylistByUserIdAndPlaylistId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                     @PathVariable Integer userId,
                                                                     @PathVariable Integer playlistId) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(userService.getPlaylistByUserIdAndPlaylistId(userId, playlistId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @PathVariable Integer userId,
                                                   @RequestBody PlaylistRequest playlistRequest) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(userService.createPlaylist(userId, playlistRequest), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                               @PathVariable Integer userId,
                                               @PathVariable Integer playlistId) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            userService.deletePlaylist(userId, playlistId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Song> addSongToPlaylist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                  @PathVariable Integer userId,
                                                  @PathVariable Integer playlistId,
                                                  @PathVariable Integer songId) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            return new ResponseEntity<>(userService.addSongToPlaylist(userId, playlistId, songId), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> deleteSongFromPlaylist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @PathVariable Integer userId,
                                                       @PathVariable Integer playlistId,
                                                       @PathVariable Integer songId) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isOwner(token, userId)) {
            userService.deleteSongFromPlaylist(userId, playlistId, songId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
