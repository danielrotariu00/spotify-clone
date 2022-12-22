package com.spotify.songcollection.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Getter
@Setter
public class Artist {

    @Id
    @Column(nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;
}
