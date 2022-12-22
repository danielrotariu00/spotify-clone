package com.spotify.playlistcollection.presentation.util.mappers;

import com.spotify.playlistcollection.presentation.util.ValidationError;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.validation.FieldError;

@Mapper
public interface ValidationErrorMapper {

    @Mapping(target="message", source="defaultMessage")
    ValidationError toValidationError(FieldError fieldError);
}
