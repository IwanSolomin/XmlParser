package ru.oiteb.xmlparser.exception.dto;

import org.springframework.http.HttpStatusCode;

public class ErrorResponseMapper {

    public static ErrorResponse toErrorResponse(Exception e, HttpStatusCode status) {
        return ErrorResponse.builder()
                .type(e.getClass().getSimpleName())
                .message(e.getMessage())
                .status(status.value())
                .build();
    }

}
