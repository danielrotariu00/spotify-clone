package com.spotify.songcollection.presentation.controller;

import com.spotify.songcollection.business.model.ArtistDTO;
import com.spotify.songcollection.business.service.ArtistService;
import com.spotify.songcollection.business.service.IdmClient;
import com.spotify.songcollection.business.util.exception.NotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping(value = "/api/songcollection/artists", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistController {

    private final ArtistService artistService;
    private final IdmClient idmClient;

    public ArtistController(ArtistService artistService, IdmClient idmClient) {
        this.artistService = artistService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtists(@RequestParam(required = false) String name,
                                                         @RequestParam(defaultValue = "false") Boolean exactMatch) {
        List<ArtistDTO> artists = artistService.findAll(name, exactMatch);
        // todo: move links to mapper
        for (final ArtistDTO artist : artists) {
            Link selfLink = linkTo(methodOn(ArtistController.class)
                    .getArtist(artist.getId())).withSelfRel();
            Link parentLink = linkTo(methodOn(ArtistController.class)
                    .getAllArtists(name, exactMatch)).withRel("parent");

            artist.add(selfLink);
            artist.add(parentLink);
        }

        return new ResponseEntity<>(artists, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable Integer id) {
        return ResponseEntity.ok(artistService.get(id));
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
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @PathVariable Integer id) {
        String token = authorizationHeader.split(" ")[1];

        if (idmClient.isContentManager(token)) {
            artistService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
