package com.spotify.songcollection.presentation.util;

import com.spotify.songcollection.business.util.exception.ExceptionWithStatus;
import com.spotify.songcollection.presentation.controller.ArtistController;
import com.spotify.songcollection.presentation.controller.SongController;
import com.spotify.songcollection.presentation.util.mappers.ValidationErrorMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.spotify.songcollection.presentation.util.Constants.INTERNAL_SERVER_ERROR_MESSAGE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
public class ExceptionTranslator {

    private final ValidationErrorMapper validationErrorMapper = Mappers.getMapper(ValidationErrorMapper.class);

    @ResponseBody
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ValidationError> translate(MethodArgumentNotValidException ex) {

        return  ex.getBindingResult().getFieldErrors().stream()
                .map(validationErrorMapper::toValidationError)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorWithLink> translate(MethodArgumentTypeMismatchException ex) {
        String message = ex.getMessage();
        ErrorWithLink error = ErrorWithLink.builder()
                .message(message)
                .build();
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorWithLink> translate(ConstraintViolationException ex) {
        String message = ex.getMessage();
        ErrorWithLink error = ErrorWithLink.builder()
                .message(message)
                .build();
        return new ResponseEntity<>(error, UNPROCESSABLE_ENTITY);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorWithLink> translate(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        ErrorWithLink error = ErrorWithLink.builder()
                .message(message)
                .build();
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(ExceptionWithStatus.class)
    public ResponseEntity<ErrorWithLink> translate(ExceptionWithStatus ex) {
        Link parentLink;
        String message = ex.getMessage();
        ErrorWithLink error = ErrorWithLink.builder()
                .message(message)
                .build();

        if(message.startsWith("Song")) {
             parentLink = linkTo(methodOn(SongController.class)
                    .getAllSongs(null, null, null, null, null, null))
                    .withRel("parent");

        } else {
            parentLink = linkTo(methodOn(ArtistController.class)
                    .getAllArtists(null, null, null, null))
                    .withRel("parent");
        }

        error.add(parentLink);

        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<String> translate(SoapFaultClientException ex) {
        String status = Objects.requireNonNull(ex.getMessage()).split(" ")[1];

        return new ResponseEntity<>(HttpStatus.valueOf(status));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> translate() {

        return new ResponseEntity<>(INTERNAL_SERVER_ERROR_MESSAGE, INTERNAL_SERVER_ERROR);
    }
}