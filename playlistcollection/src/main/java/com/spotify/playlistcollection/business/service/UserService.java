package com.spotify.playlistcollection.business.service;

import com.spotify.playlistcollection.business.model.PlaylistRequest;
import com.spotify.playlistcollection.business.util.exception.AlreadyExistsException;
import com.spotify.playlistcollection.business.util.exception.NotFoundException;
import com.spotify.playlistcollection.persistence.document.Playlist;
import com.spotify.playlistcollection.persistence.document.Song;
import com.spotify.playlistcollection.persistence.document.User;
import com.spotify.playlistcollection.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SongCollectionClient songCollectionClient;

    private final String USER_NOT_FOUND_MESSAGE = "User does not exist.";
    private final String PLAYLIST_NOT_FOUND_MESSAGE = "Playlist does not exist.";
    private final String PLAYLIST_ALREADY_EXISTS_MESSAGE = "Playlist already exists.";

    public UserService(final UserRepository userRepository, final SongCollectionClient songCollectionClient) {
        this.userRepository = userRepository;
        this.songCollectionClient = songCollectionClient;
    }

    public List<Playlist> getPlaylistsByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        return user.getPlaylists();
    }

    public Playlist getPlaylistByUserIdAndPlaylistId(Integer userId, Integer playlistId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                return playlist;
            }
        }
        throw new NotFoundException(PLAYLIST_NOT_FOUND_MESSAGE);
    }

    public Playlist createPlaylist(Integer userId, PlaylistRequest playlistRequest) {
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

        return playlist;
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

    public Song addSongToPlaylist(Integer userId, Integer playlistId, Integer songId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE)
        );

        for (Playlist playlist : user.getPlaylists()) {
            if (Objects.equals(playlist.getId(), playlistId)) {
                Song song = songCollectionClient.getSongById(songId);
                playlist.getSongs().add(song);

                userRepository.save(user);
                return song;
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
}
