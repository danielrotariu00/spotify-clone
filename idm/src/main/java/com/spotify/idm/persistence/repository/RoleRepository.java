package com.spotify.idm.persistence.repository;

import com.spotify.idm.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
