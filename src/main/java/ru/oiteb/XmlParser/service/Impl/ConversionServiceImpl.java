package ru.oiteb.XmlParser.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.EmptyXmlFileException;
import ru.oiteb.XmlParser.exception.InvalidXmlFileException;
import ru.oiteb.XmlParser.service.ConversionService;
import ru.oiteb.XmlParser.service.exporter.ExcelExporter;
import ru.oiteb.XmlParser.service.parser.XmlProductParser;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ru.oiteb.XmlParser.constants.ExceptionsDescriptions.*;

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
     * @throws EmptyXmlFileException если файл пуст
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