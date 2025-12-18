package ru.oiteb.XmlParser.exception;

import lombok.Getter;

@Getter
public class ParserParentException extends RuntimeException {

    public ParserParentException(String message) {
        super(message);
    }
}
