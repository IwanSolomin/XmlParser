package ru.oiteb.xmlparser.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.oiteb.xmlparser.entity.ProductData;
import ru.oiteb.xmlparser.exception.EmptyXmlFileException;
import ru.oiteb.xmlparser.exception.InvalidXmlFileException;
import ru.oiteb.xmlparser.service.ConversionService;
import ru.oiteb.xmlparser.service.exporter.ExcelExporter;
import ru.oiteb.xmlparser.service.parser.XmlProductParser;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ru.oiteb.xmlparser.constants.ExceptionsDescriptions.*;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

    private final XmlProductParser parser;
    private final ExcelExporter exporter;

    /**
     * Конвертирует XML-данные в формат XLSX и возвращает байтовое представление Excel-файла.
     *
     * @param xml загруженный XML-файл; не должен быть null или пустым
     * @return массив байтов XLSX-файла
     * @throws EmptyXmlFileException   если файл пуст
     * @throws InvalidXmlFileException если файл повреждён, не XML или не содержит данных
     */
    @Override
    public byte[] convert(MultipartFile xml) {
        if (xml == null) {
            throw new InvalidXmlFileException(INVALID_XML_FILE);
        }
        if (xml.isEmpty()) {
            throw new EmptyXmlFileException(EMPTY_XML_FILE);
        }
        List<ProductData> products;
        try {
            products = parser.parseXml(xml.getBytes());
        } catch (IOException e) {
            throw new InvalidXmlFileException(UNREADABLE_XML_FILE);
        }
        products.sort(Comparator.comparingInt(ProductData::getIndexNum));
        return exporter.generateExcelToBytes(products);
    }

}