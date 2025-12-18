package ru.oiteb.XmlParser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyXmlFileException extends ParserParentException {

    public EmptyXmlFileException(String message) {
        super("XML файл пустой" + message);
    }
}
