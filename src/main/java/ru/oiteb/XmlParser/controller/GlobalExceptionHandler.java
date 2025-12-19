package ru.oiteb.XmlParser.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.oiteb.XmlParser.exception.ParserParentException;
import ru.oiteb.XmlParser.exception.dto.ErrorResponse;
import ru.oiteb.XmlParser.exception.dto.ErrorResponseMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParserParentException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ParserParentException e) {
        ResponseStatus responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
        HttpStatusCode status = responseStatus != null ? responseStatus.code() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse error = ErrorResponseMapper.toErrorResponse(e, status);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleApiException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                "Unexpected error",
                "Произошла непредвиденная ошибка",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}