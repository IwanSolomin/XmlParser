package ru.oiteb.XmlParser.service.parser;

import org.springframework.stereotype.Component;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.BadXmlFileException;
import ru.oiteb.XmlParser.exception.XmlFactoryCreationException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.oiteb.XmlParser.constants.XmlTagConstants.*;
import static ru.oiteb.XmlParser.mapper.ProductDataMapper.toProductData;

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
 *
 * <h3>Особенности парсинга:</h3>
 * <ul>
 *   <li>Поддерживает вложенные структуры (например, {@code trademark} внутри {@code trademarkInfo})</li>
 *   <li>Извлекает национальный код из {@code OKEIInfo → nationalCode}</li>
 *   <li>Игнорирует неизвестные теги (пропускает их содержимое)</li>
 *   <li>Останавливает парсинг текущего продукта при встрече закрывающего {@code </productInfo>}</li>
 * </ul>
 *
 * <h3>Обработка ошибок:</h3>
 * <ul>
 *   <li>{@link BadXmlFileException} — при ошибках ввода-вывода (например, повреждённый файл)</li>
 *   <li>{@link XmlFactoryCreationException} — при ошибках парсинга XML (некорректная структура, неожиданный конец потока)</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * byte[] xmlBytes = Files.readAllBytes(Paths.get("products.xml"));
 * List<ProductData> products = parser.parseXml(xmlBytes);
 * }</pre>
 *
 * @implNote Класс помечен как Spring-компонент ({@code @Component}) и предназначен для внедрения через DI.
 * @author solominis
 * @since 1.0
 */
@Component
public class XmlProductParser {

    private final XMLInputFactory xmlFactory;

    public XmlProductParser(XMLInputFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    /**
     * Парсит XML-данные из байтового массива и возвращает список продуктов.
     * <p>
     * Метод ищет все элементы {@code <productInfo>} в корне XML и вызывает {@link #parseProduct(XMLStreamReader)}
     * для каждого из них.
     *
     * @param xmlBytes содержимое XML-файла в виде массива байтов; не должно быть {@code null}
     * @return список объектов {@link ProductData}, по одному на каждый {@code <productInfo>}
     * @throws BadXmlFileException если возникла ошибка ввода-вывода при чтении потока
     * @throws XmlFactoryCreationException если XML повреждён или не соответствует ожидаемой структуре
     * @see #parseProduct(XMLStreamReader)
     */
    public List<ProductData> parseXml(byte[] xmlBytes) {
        List<ProductData> products = new ArrayList<>();
        XMLStreamReader reader = null;
        try (InputStream is = new ByteArrayInputStream(xmlBytes);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            reader = xmlFactory.createXMLStreamReader(bis);
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && PRODUCT_INFO.equals(reader.getLocalName())) {
                    ProductData product = parseProduct(reader);
                    products.add(product);
                }
            }
        } catch (IOException e) {
            throw new BadXmlFileException(e.getMessage());
        } catch (XMLStreamException e) {
            throw new XmlFactoryCreationException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException ignored) {
                }
            }
        }
        return products;
    }

    /**
     * Извлекает данные одного продукта из XML-потока, начиная с тега {@code <productInfo>}.
     * <p>
     * Метод парсит дочерние элементы до тех пор, пока не встретит закрывающий {@code </productInfo>}.
     * Поддерживает вложенные структуры: {@code trademarkInfo → trademark}, {@code OKPD2Info → OKPDCode} и др.
     *
     * @param reader поток, позиционированный на открывающем теге {@code <productInfo>}
     * @return заполненный объект {@link ProductData}
     * @throws XMLStreamException если возникла ошибка при чтении XML
     * @see #parseOKEIUnit(XMLStreamReader)
     * @see #readElementText(XMLStreamReader)
     */
    private ProductData parseProduct(XMLStreamReader reader) throws XMLStreamException {
        String unit = "";
        String okpd2 = "";
        String nkmi = "";
        String manufacturer = "";
        String country = "";
        String certNumber = "";
        String fullName = "";
        String trademark = "";
        int indexNum = 0;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.START_ELEMENT) {
                String name = reader.getLocalName();
                switch (name) {
                    case INDEX_NUM:
                        String numText = readElementText(reader);
                        if (!numText.isEmpty()) {
                            indexNum = Integer.parseInt(numText);
                        }
                        break;
                    case NAME:
                        if (fullName.isEmpty()) {
                            fullName = readElementText(reader);
                        } else {
                            readElementText(reader);
                        }
                        break;
                    case TRADEMARK_INFO:
                        while (reader.hasNext()) {
                            int e = reader.next();
                            if (e == XMLEvent.START_ELEMENT && TRADEMARK.equals(reader.getLocalName())) {
                                trademark = readElementText(reader);
                                break;
                            }
                            if (e == XMLEvent.END_ELEMENT && TRADEMARK_INFO.equals(reader.getLocalName())) {
                                break;
                            }
                        }
                        break;
                    case OKEI_INFO:
                        unit = parseOKEIUnit(reader);
                        break;
                    case OKPD2_INFO:
                        while (reader.hasNext()) {
                            int e = reader.next();
                            if (e == XMLEvent.START_ELEMENT && OKPD_CODE.equals(reader.getLocalName())) {
                                okpd2 = readElementText(reader);
                                break;
                            }
                            if (e == XMLEvent.END_ELEMENT && OKPD2_INFO.equals(reader.getLocalName())) {
                                break;
                            }
                            if (e == XMLEvent.START_ELEMENT) {
                                readElementText(reader);
                            }
                        }
                        break;
                    case MEDICAL_PRODUCT_CODE:
                        nkmi = readElementText(reader);
                        break;
                    case COUNTRY_FULL_NAME:
                        country = readElementText(reader);
                        break;
                    default:
                        readElementText(reader);
                        break;
                }
            }
            if (event == XMLEvent.END_ELEMENT && PRODUCT_INFO.equals(reader.getLocalName())) {
                break;
            }
        }
        return toProductData(indexNum, unit, okpd2, nkmi, manufacturer, country, certNumber, fullName, trademark);
    }

    /**
     * Безопасно считывает текстовое содержимое текущего XML-элемента.
     * <p>
     * Метод обрабатывает возможное разделение текста на несколько {@code CHARACTERS}-событий
     * (что допустимо в StAX) и объединяет их в одну строку.
     *
     * @param reader поток, позиционированный на открывающем теге элемента
     * @return текстовое содержимое элемента, обрезанное по краям; пустая строка, если содержимого нет
     * @throws XMLStreamException если поток завершился некорректно
     */
    private String readElementText(XMLStreamReader reader) throws XMLStreamException {
        StringBuilder text = new StringBuilder();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.CHARACTERS) {
                text.append(reader.getText());
            } else if (event == XMLEvent.END_ELEMENT) {
                break;
            }
        }
        return text.toString().trim();
    }

    /**
     * Извлекает национальный код единицы измерения из блока {@code <OKEIInfo>}.
     * <p>
     * Ищет дочерний элемент {@code <nationalCode>} внутри {@code OKEIInfo} и возвращает его значение.
     * Если элемент не найден — возвращает пустую строку.
     *
     * @param reader поток, позиционированный на открывающем теге {@code <OKEIInfo>}
     * @return значение {@code <nationalCode>} или пустая строка
     * @throws XMLStreamException если возникла ошибка при чтении XML
     */
    private String parseOKEIUnit(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.START_ELEMENT) {
                if (NATIONAL_CODE.equals(reader.getLocalName())) {
                    return readElementText(reader);
                } else {
                    readElementText(reader);
                }
            } else if (event == XMLEvent.END_ELEMENT) {
                if (OKEI_INFO.equals(reader.getLocalName())) {
                    break;
                }
            }
        }
        return "";
    }

}
