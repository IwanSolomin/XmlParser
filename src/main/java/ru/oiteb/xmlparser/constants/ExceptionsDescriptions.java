package ru.oiteb.xmlparser.constants;

import static ru.oiteb.xmlparser.constants.XmlTagConstants.NATIONAL_CODE;
import static ru.oiteb.xmlparser.constants.XmlTagConstants.OKEI_INFO;

public final class ExceptionsDescriptions {

    public static final String INVALID_XML_FILE = "Файл не предоставлен";
    public static final String EMPTY_XML_FILE = "Xml файл пустой";
    public static final String UNREADABLE_XML_FILE = "Не удалось прочитать xml файл";
    public static final String EMPTY_NATIONAL_CODE = "Код страны не должен быть  пустым";
    public static final String INPUT_STREAM_EXCEPTION = "Исключение при попытке чтения";
    public static final String EMPTY_OKEI_INFO = "Блок OKEI несодержит информации";
    public static final String INVALID_OKEI_INFO = "Блок <" + OKEI_INFO + "> не содержит обязательный элемент <" + NATIONAL_CODE + ">";
    public static final String UNEXPECTED_ERROR_TYPE = "Unexpected error";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Произошла непредвиденная ошибка";
    public static final String XML_CREATION_EXCEPTION = "Invalid XLSX generated";

}
