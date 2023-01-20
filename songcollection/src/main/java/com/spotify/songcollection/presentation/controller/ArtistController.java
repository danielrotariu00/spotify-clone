package com.spotify.songcollection.presentation.controller;

import com.spotify.songcollection.business.model.ArtistDTO;
import com.spotify.songcollection.business.model.GetArtistsDTO;
import com.spotify.songcollection.business.service.ArtistService;
import com.spotify.songcollection.business.service.ArtistSongService;
import com.spotify.songcollection.business.service.IdmClient;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@Validated
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/songcollection/artists", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistSongService artistSongService;
    private final IdmClient idmClient;

    public ArtistController(
            ArtistService artistService,
            ArtistSongService artistSongService,
            IdmClient idmClient
    ) {
        this.artistService = artistService;
        this.artistSongService = artistSongService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<GetArtistsDTO> getAllArtists(
            @RequestParam(required = false) @Size(max = 32) String name,
            @RequestParam(defaultValue = "false") Boolean exactMatch,
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(0) Integer maxItems
    ) {
        return new ResponseEntity<>(artistService.findAll(name, exactMatch, page, maxItems), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable @Min(0) Integer id) {
        return new ResponseEntity<>(artistService.get(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ArtistDTO> saveArtist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                @PathVariable @Min(0) Integer id,
                                                @RequestBody @Valid ArtistDTO artistDTO) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isContentManager(token)) {
            try {
                artistService.get(id);
            } catch (NotFoundException ex) {
                ArtistDTO artist = artistService.save(id, artistDTO);

                return new ResponseEntity<>(artist, HttpStatus.CREATED);
            }

            ArtistDTO artist = artistService.save(id, artistDTO);

            return new ResponseEntity<>(artist, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @PathVariable @Min(0) Integer id) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isContentManager(token)) {
            artistSongService.deleteAllByArtistId(id);
            artistService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
