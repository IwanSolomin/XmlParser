package ru.oiteb.xmlparser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExcelGenerationException extends ParserParentException {

    public ExcelGenerationException(String message) {
        super("Exception when generating .xlsx" + message);
    }

}
