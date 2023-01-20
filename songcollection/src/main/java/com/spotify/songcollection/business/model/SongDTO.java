package com.spotify.songcollection.business.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.spotify.songcollection.persistence.entity.Genre;
import com.spotify.songcollection.persistence.entity.Type;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;


@Getter
@Setter
public class SongDTO extends RepresentationModel<SongDTO> {

    private Integer id;

    @NotNull
    @Size(max = 32)
    private String name;

    @NotNull
    private Genre genre;

    @NotNull
    @Min(0)
    private Integer year;

    @NotNull
    private Type type;

    @Min(0)
    private Integer parent;
}
