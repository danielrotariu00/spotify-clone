package com.spotify.songcollection.presentation.controller;

import com.spotify.songcollection.business.model.SongDTO;
import com.spotify.songcollection.business.service.IdmClient;
import com.spotify.songcollection.business.service.SongService;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping(value = "/api/songcollection/songs", produces = MediaType.APPLICATION_JSON_VALUE)
public class SongController {

    private final SongService songService;
    private final IdmClient idmClient;


    public SongController(SongService songService, IdmClient idmClient) {
        this.songService = songService;
        this.idmClient = idmClient;
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs(@RequestParam(required = false) Integer page,
                                                     @RequestParam(defaultValue = "10") Integer maxItems) {
        return new ResponseEntity<>(songService.findAll(page, maxItems), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSong(@PathVariable Integer id) {
        SongDTO songDTO = songService.get(id);
        Link selfLink = linkTo(methodOn(SongController.class)
                .getSong(songDTO.getId())).withSelfRel();

        songDTO.add(selfLink);

        return new ResponseEntity<>(songDTO, HttpStatus.OK);
    }

    // todo: isContentManagerOrArtist
    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody @Valid SongDTO songDTO) {
        return new ResponseEntity<>(songService.create(songDTO), HttpStatus.CREATED);
    }

    // todo: isContentManagerOrOwner
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSong(@PathVariable Integer id,
                                           @RequestBody @Valid SongDTO songDTO) {
        try {
            songService.update(id, songDTO);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ResponseStatusException ex) {
            songService.create(songDTO);
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }
    }

    // todo: isContentManagerOrOwner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Integer id) {
        songService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
