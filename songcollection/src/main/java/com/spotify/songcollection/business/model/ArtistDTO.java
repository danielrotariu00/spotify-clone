package com.spotify.songcollection.business.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class ArtistDTO extends RepresentationModel<ArtistDTO> {

    private Integer id;

    @NotNull
    @Size(max = 32)
    private String name;

    @NotNull
    private Boolean isActive;
}
