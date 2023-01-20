package com.spotify.songcollection.presentation.controller;

import com.spotify.idm.AuthorizeRequest;
import com.spotify.idm.AuthorizeResponse;
import com.spotify.songcollection.business.model.GetSongsDTO;
import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.service.ArtistSongService;
import com.spotify.songcollection.business.service.IdmClient;
import com.spotify.songcollection.business.service.SongService;
import com.spotify.songcollection.persistence.entity.Genre;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

import static com.spotify.songcollection.business.util.Constants.ARTIST_ROLE_ID;
import static com.spotify.songcollection.business.util.Constants.CONTENT_MANAGER_ROLE_ID;

@Validated
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/songcollection/songs", produces = MediaType.APPLICATION_JSON_VALUE)
public class SongController {

    private final SongService songService;
    private final ArtistSongService artistSongService;
    private final IdmClient idmClient;


    public SongController(
            SongService songService,
            ArtistSongService artistSongService,
            IdmClient idmClient
    ) {
        this.songService = songService;
        this.artistSongService = artistSongService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<GetSongsDTO> getAllSongs(
            @RequestParam(required = false) @Size(max = 32) String name,
            @RequestParam(defaultValue = "false") Boolean exactMatch,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) @Min(0) Integer year,
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(0) Integer maxItems
    ) {
        return new ResponseEntity<>(songService.findAll(name, exactMatch, genre, year, page, maxItems), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSong(@PathVariable @Min(0) Integer id) {
        return new ResponseEntity<>(songService.get(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SongDTO> createSong(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody @Valid SongDTO songDTO
    ) {
        String token = authorizationHeader.split(" ")[1];
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = idmClient.authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        if (roleIds.contains(CONTENT_MANAGER_ROLE_ID)) {
            return new ResponseEntity<>(songService.create(songDTO), HttpStatus.CREATED);
        } else if (roleIds.contains(ARTIST_ROLE_ID)) {
            SongDTO createdSong = songService.create(songDTO);
            artistSongService.create(response.getUserId(), createdSong.getId());
            return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> updateSong(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer id,
            @RequestBody @Valid SongDTO songDTO
    ) {
        String token = authorizationHeader.split(" ")[1];
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = idmClient.authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        if (songService.existsById(id)) {
            if (roleIds.contains(CONTENT_MANAGER_ROLE_ID)) {
                return new ResponseEntity<>(songService.update(id, songDTO), HttpStatus.OK);
            } else if (roleIds.contains(ARTIST_ROLE_ID)) {
                if (artistSongService.existsByArtistIdAndSongId(response.getUserId(), id)) {
                    return new ResponseEntity<>(songService.update(id, songDTO), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
//            if (roleIds.contains(CONTENT_MANAGER_ROLE_ID)) {
//                return new ResponseEntity<>(songService.create(songDTO), HttpStatus.CREATED);
//            } else if (roleIds.contains(ARTIST_ROLE_ID)) {
//                SongDTO createdSong = songService.create(songDTO);
//                artistSongService.create(response.getUserId(), createdSong.getId());
//                return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
//            }
//      am eliminat partea de create de la PUT deoarece id-ul unui cantec e generat cu autoincrement si pot aparea conflicte

            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable @Min(0) Integer id
    ) {
        String token = authorizationHeader.split(" ")[1];
        AuthorizeRequest request = new AuthorizeRequest();
        request.setToken(token);

        AuthorizeResponse response = idmClient.authorize(request);
        List<Integer> roleIds = response.getRoleIds();

        if (roleIds.contains(CONTENT_MANAGER_ROLE_ID)) {
            artistSongService.deleteAllBySongId(id);
            songService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (roleIds.contains(ARTIST_ROLE_ID)) {
            if (artistSongService.existsByArtistIdAndSongId(response.getUserId(), id)) {
                artistSongService.deleteAllBySongId(id);
                songService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
