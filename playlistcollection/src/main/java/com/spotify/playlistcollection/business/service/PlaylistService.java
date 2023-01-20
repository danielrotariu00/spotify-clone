package com.spotify.playlistcollection.business.service;

import com.spotify.playlistcollection.business.model.PlaylistDTO;
import com.spotify.playlistcollection.business.model.PlaylistRequest;
import com.spotify.playlistcollection.business.util.exception.AlreadyExistsException;
import com.spotify.playlistcollection.business.util.exception.NotFoundException;
import com.spotify.playlistcollection.persistence.document.Playlist;
import com.spotify.playlistcollection.persistence.document.Song;
import com.spotify.playlistcollection.persistence.document.User;
import com.spotify.playlistcollection.persistence.repository.UserRepository;
import com.spotify.playlistcollection.presentation.controller.PlaylistController;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PlaylistService {

    private final UserRepository userRepository;
    private final SongCollectionClient songCollectionClient;

    private final String USER_NOT_FOUND_MESSAGE = "User does not exist.";
    private final String PLAYLIST_NOT_FOUND_MESSAGE = "Playlist does not exist.";
    private final String PLAYLIST_ALREADY_EXISTS_MESSAGE = "Playlist already exists.";

    public PlaylistService(final UserRepository userRepository, final SongCollectionClient songCollectionClient) {
        this.userRepository = userRepository;
        this.songCollectionClient = songCollectionClient;
    }

    public List<PlaylistDTO> getPlaylistsByUserId(Integer userId, String name) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        return user.getPlaylists().stream()
                .filter(playlist -> name == null || playlist.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::toDTO)
                .map(playlist -> addLinks(userId, playlist))
                .collect(Collectors.toList());
    }

    public PlaylistDTO getPlaylistByUserIdAndPlaylistId(Integer userId, Integer playlistId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                return addLinks(userId, toDTO(playlist));
            }
        }
        throw new NotFoundException(PLAYLIST_NOT_FOUND_MESSAGE);
    }

    public PlaylistDTO createPlaylist(Integer userId, PlaylistRequest playlistRequest) {
        User user;
        Optional<User> dbUser = userRepository.findById(userId);

        if (dbUser.isEmpty()) {
            user = User.builder()
                    .id(userId)
                    .playlists(new ArrayList<>())
                    .build();
        } else {
            user = dbUser.get();
        }

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getName(), playlistRequest.getName())) {
                throw new AlreadyExistsException(PLAYLIST_ALREADY_EXISTS_MESSAGE);
            }
        }

        Integer lastId = 0;
        if (user.getPlaylists().size() > 0) {
            lastId = user.getPlaylists().get(user.getPlaylists().size() - 1).getId();
        }

        Playlist playlist = Playlist.builder()
                .id(lastId + 1)
                .name(playlistRequest.getName())
                .songs(new ArrayList<>())
                .build();

        user.getPlaylists().add(playlist);
        userRepository.save(user);

        return addLinks(userId, toDTO(playlist));
    }

    public void deletePlaylist(Integer userId, Integer playlistId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                user.getPlaylists().remove(playlist);

                userRepository.save(user);
                return;
            }
        }

        throw new NotFoundException(PLAYLIST_NOT_FOUND_MESSAGE);
    }

    public void addSongToPlaylist(Integer userId, Integer playlistId, Integer songId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                Song song = songCollectionClient.getSongById(songId);
                playlist.getSongs().add(song);

                userRepository.save(user);
                return;
            }
        }

        throw new NotFoundException(PLAYLIST_NOT_FOUND_MESSAGE);
    }

    public void deleteSongFromPlaylist(Integer userId, Integer playlistId, Integer songId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                for (Song song : playlist.getSongs()) {
                    if (Objects.equals(song.getId(), songId)) {
                        playlist.getSongs().remove(song);

                        userRepository.save(user);
                        return;
                    }
                }
            }
        }

        throw new NotFoundException(PLAYLIST_NOT_FOUND_MESSAGE);
    }

    private PlaylistDTO addLinks(Integer userId, PlaylistDTO playlist) {
        Link selfLink = linkTo(methodOn(PlaylistController.class)
                .getPlaylistByUserIdAndPlaylistId(null, userId, playlist.getId()))
                .withSelfRel();
        Link parentLink = linkTo(methodOn(PlaylistController.class)
                .getPlaylistsByUserId(null, userId, null))
                .withRel("parent");

        playlist.add(selfLink);
        playlist.add(parentLink);

        return playlist;
    }

    private PlaylistDTO toDTO(Playlist playlist) {
        return PlaylistDTO.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .songs(playlist.getSongs())
                .build();
    }
}
