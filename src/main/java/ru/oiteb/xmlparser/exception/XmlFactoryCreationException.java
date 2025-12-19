package ru.oiteb.xmlparser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class XmlFactoryCreationException extends ParserParentException {

    public XmlFactoryCreationException(String message) {
        super("Exception when creating xml factory. Probably the file is not .xml" + message);
    }

}
