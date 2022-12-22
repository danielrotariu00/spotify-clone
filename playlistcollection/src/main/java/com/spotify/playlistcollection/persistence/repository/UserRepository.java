package com.spotify.playlistcollection.persistence.repository;

import com.spotify.playlistcollection.persistence.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Integer> {
}
