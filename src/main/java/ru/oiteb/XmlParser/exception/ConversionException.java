package ru.oiteb.XmlParser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConversionException extends ParserParentException {

    public ConversionException(String message) {
        super("Conversation Main exception" + message);
    }

}
