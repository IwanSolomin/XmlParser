package ru.oiteb.XmlParser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParserIOException extends ParserParentException {

    public ParserIOException(String message) {
        super(message);
    }

}
