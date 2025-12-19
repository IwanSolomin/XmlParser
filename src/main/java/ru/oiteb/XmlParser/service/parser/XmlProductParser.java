package ru.oiteb.XmlParser.service.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.InvalidXmlFileException;
import ru.oiteb.XmlParser.exception.ParsingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.stream.XMLStreamConstants.*;
import static ru.oiteb.XmlParser.constants.ExceptionsDescriptions.*;
import static ru.oiteb.XmlParser.constants.XmlTagConstants.*;

/**
 * Парсер XML-документов, предназначенный для извлечения данных о продуктах из структурированного XML.
 * <p>
 * Реализует потоковый (streaming) подход с использованием {@link XMLStreamReader},
 * что позволяет обрабатывать большие XML-файлы с минимальным потреблением памяти.
 * Основной метод {@link #parseXml(byte[])} принимает байтовый массив XML и возвращает список объектов {@link ProductData}.
 * </p>
 * <p>
 * Парсер ожидает XML-структуру, содержащую элементы {@code <productInfo>},
 * внутри которых расположены теги: {@code indexNum}, {@code name}, {@code OKEIInfo},
 * {@code OKPD2Info}, {@code medicalProductCode}, {@code countryFullName}, {@code trademarkInfo} и др.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class XmlProductParser {

    private final XMLInputFactory xmlInputFactory;

    /**
     * Парсит XML-данные из байтового массива и возвращает список продуктов.
     * <p>
     * Метод ищет все элементы {@code <productInfo>} на любом уровне вложенности
     * и извлекает из них данные о продуктах.
     * </p>
     *
     * @param xmlBytes содержимое XML-файла в виде массива байтов; не должно быть {@code null} или пустым
     * @return список объектов {@link ProductData}, по одному на каждый найденный {@code <productInfo>}
     * @throws ru.oiteb.XmlParser.exception.InvalidXmlFileException если:
     *         <ul>
     *             <li>входной массив {@code null} или пуст;</li>
     *             <li>XML не соответствует ожидаемой структуре;</li>
     *             <li>возникла ошибка при чтении потока.</li>
     *         </ul>
     */
    public List<ProductData> parseXml(byte[] xmlBytes) {
        if (xmlBytes == null || xmlBytes.length == 0) {
            throw new InvalidXmlFileException(EMPTY_XML_FILE);
        }
        try (InputStream inputStream = new ByteArrayInputStream(xmlBytes)) {
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
            List<ProductData> products = new ArrayList<>();

            while (reader.hasNext()) {
                if (reader.next() == START_ELEMENT && PRODUCT_INFO.equals(reader.getLocalName())) {
                    products.add(parseProduct(reader));
                }
            }
            return products;
        } catch (XMLStreamException e) {
            throw new InvalidXmlFileException(INVALID_XML_FILE);
        } catch (IOException e) {
            throw new ParsingException(INPUT_STREAM_EXCEPTION);
        }
    }

    /**
     * Извлекает данные одного продукта из XML-потока, начиная с тега {@code <productInfo>}.
     * <p>
     * Метод парсит дочерние элементы до тех пор, пока не встретит закрывающий {@code </productInfo>}.
     * Поддерживаются вложенные структуры: {@code trademarkInfo → trademark}, {@code OKPD2Info → OKPDCode} и др.
     * </p>
     *
     * @param reader поток, позиционированный на открывающем теге {@code <productInfo>}
     * @return заполненный объект {@link ProductData}
     * @throws InvalidXmlFileException если структура XML внутри {@code <productInfo>} нарушена
     * @throws XMLStreamException если возникла низкоуровневая ошибка при чтении XML
     */
    private ProductData parseProduct(XMLStreamReader reader) throws XMLStreamException {
        ProductData product = new ProductData();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == START_ELEMENT) {
                String elementName = reader.getLocalName();
                switch (elementName) {
                    case INDEX_NUM:
                        product.setIndexNum(parseIndexNumber(reader));
                        break;
                    case NAME:
                        if (isEmpty(product.getFullName())) {
                            product.setFullName(readElementText(reader));
                        } else {
                            readElementText(reader);
                        }
                        break;
                    case TRADEMARK_INFO:
                        product.setTradeMark(parseTrademark(reader));
                        break;
                    case OKEI_INFO:
                        product.setUnit(parseOKEIUnit(reader));
                        break;
                    case OKPD2_INFO:
                        product.setOkpd2(parseOkpd2(reader));
                        break;
                    case MEDICAL_PRODUCT_CODE:
                        product.setNkmi(readElementText(reader));
                        break;
                    case COUNTRY_FULL_NAME:
                        product.setCountry(readElementText(reader));
                        break;
                    default:
                        readElementText(reader);
                        break;
                }
            }
            if (event == END_ELEMENT && PRODUCT_INFO.equals(reader.getLocalName())) {
                break;
            }
        }
        return product;
    }

    /**
     * Безопасно считывает текстовое содержимое текущего XML-элемента.
     * <p>
     * Метод обрабатывает возможное разделение текста на несколько {@code CHARACTERS}-событий
     * (что допустимо в StAX) и объединяет их в одну строку.
     * Также поддерживает CDATA-секции.
     * </p>
     *
     * @param reader поток, позиционированный на открывающем теге элемента
     * @return текстовое содержимое элемента, обрезанное по краям; пустая строка, если содержимого нет
     * @throws XMLStreamException если поток завершился некорректно
     */
    private String readElementText(XMLStreamReader reader) throws XMLStreamException {
        StringBuilder text = new StringBuilder();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == CHARACTERS || event == XMLStreamConstants.CDATA) {
                text.append(reader.getText());
            } else if (event == END_ELEMENT) {
                break;
            }
        }
        return text.toString().trim();
    }

    /**
     * Парсит числовое значение индекса продукта.
     *
     * @param reader поток, позиционированный на открывающем теге {@code <indexNum>}
     * @return целое число; 0, если тег пуст
     * @throws InvalidXmlFileException если содержимое тега не является допустимым целым числом
     * @throws XMLStreamException если возникла ошибка при чтении XML
     */
    private int parseIndexNumber(XMLStreamReader reader) throws XMLStreamException {
        String text = readElementText(reader);
        if (isEmpty(text)) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidXmlFileException(UNREADABLE_XML_FILE);
        }
    }

    /**
     * Извлекает наименование торговой марки из блока {@code <trademarkInfo>}.
     *
     * @param reader поток, позиционированный на открывающем теге {@code <trademarkInfo>}
     * @return значение {@code <trademark>}, или пустая строка, если не найдено
     * @throws XMLStreamException если возникла ошибка при чтении XML
     */
    private String parseTrademark(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == START_ELEMENT && TRADEMARK.equals(reader.getLocalName())) {
                return readElementText(reader);
            }
            if (event == END_ELEMENT && TRADEMARK_INFO.equals(reader.getLocalName())) {
                break;
            }
        }
        return "";
    }

    /**
     * Извлекает национальный код единицы измерения из блока {@code <OKEIInfo>}.
     * <p>
     * Элемент {@code <nationalCode>} является обязательным — если он отсутствует,
     * выбрасывается исключение.
     * </p>
     *
     * @param reader поток, позиционированный на открывающем теге {@code <OKEIInfo>}
     * @return значение {@code <nationalCode>}
     * @throws InvalidXmlFileException если элемент {@code <nationalCode>} отсутствует или пуст
     * @throws XMLStreamException если возникла ошибка при чтении XML
     */
    private String parseOKEIUnit(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == START_ELEMENT) {
                if (NATIONAL_CODE.equals(reader.getLocalName())) {
                    String code = readElementText(reader);
                    if (isEmpty(code)) {
                        throw new InvalidXmlFileException(EMPTY_NATIONAL_CODE);
                    }
                    return code;
                } else {
                    readElementText(reader);
                }
            } else if (event == END_ELEMENT && OKEI_INFO.equals(reader.getLocalName())) {
                throw new InvalidXmlFileException(INVALID_OKEI_INFO);
            }
        }
        throw new InvalidXmlFileException(EMPTY_OKEI_INFO);
    }

    /**
     * Извлекает код ОКПД2 из блока {@code <OKPD2Info>}.
     *
     * @param reader поток, позиционированный на открывающем теге {@code <OKPD2Info>}
     * @return значение {@code <OKPDCode>}, или пустая строка, если не найдено
     * @throws XMLStreamException если возникла ошибка при чтении XML
     */
    private String parseOkpd2(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == START_ELEMENT && OKPD_CODE.equals(reader.getLocalName())) {
                return readElementText(reader);
            }
            if (event == END_ELEMENT && OKPD2_INFO.equals(reader.getLocalName())) {
                break;
            }
        }
        return "";
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
