package ru.oiteb.xmlparser.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.oiteb.xmlparser.exception.ParserParentException;
import ru.oiteb.xmlparser.exception.dto.ErrorResponse;
import ru.oiteb.xmlparser.exception.dto.ErrorResponseMapper;

import static ru.oiteb.xmlparser.constants.ExceptionsDescriptions.UNEXPECTED_ERROR_MESSAGE;
import static ru.oiteb.xmlparser.constants.ExceptionsDescriptions.UNEXPECTED_ERROR_TYPE;

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
                UNEXPECTED_ERROR_TYPE,
                UNEXPECTED_ERROR_MESSAGE,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}