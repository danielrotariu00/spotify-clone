package com.spotify.playlistcollection.business.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class SongDTO implements Serializable {

    private Integer id;

    private String name;

    private LinkList _links;
}
