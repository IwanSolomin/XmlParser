package ru.oiteb.XmlParser.constants;

import static ru.oiteb.XmlParser.constants.XmlTagConstants.NATIONAL_CODE;
import static ru.oiteb.XmlParser.constants.XmlTagConstants.OKEI_INFO;

public final class ExceptionsDescriptions {

    public static final String INVALID_XML_FILE = "Файл не предоставлен";
    public static final String EMPTY_XML_FILE = "Xml файл пустой";
    public static final String UNREADABLE_XML_FILE = "Не удалось прочитать xml файл";
    public static final String EMPTY_NATIONAL_CODE = "Код страны не должен быть  пустым";
    public static final String INPUT_STREAM_EXCEPTION = "Исключение при попытке чтения";
    public static final String EMPTY_OKEI_INFO = "Блок OKEI несодержит информации";
    public static final String INVALID_OKEI_INFO = "Блок <" + OKEI_INFO + "> не содержит обязательный элемент <" + NATIONAL_CODE + ">";


}
