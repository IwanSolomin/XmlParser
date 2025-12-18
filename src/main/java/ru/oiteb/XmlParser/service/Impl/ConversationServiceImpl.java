package ru.oiteb.XmlParser.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.EmptyXmlFileException;
import ru.oiteb.XmlParser.service.ConversationService;
import ru.oiteb.XmlParser.service.exporter.ExcelExporter;
import ru.oiteb.XmlParser.service.parser.XmlProductParser;

import java.io.IOException;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final XmlProductParser parser;
    private final ExcelExporter exporter;

    public ConversationServiceImpl(XmlProductParser parser, ExcelExporter exporter) {
        this.parser = parser;
        this.exporter = exporter;
    }

    /**
     * Конвертирует XML-данные в формат XLSX и возвращает байтовое представление Excel-файла.
     * <p>
     * Метод парсит входной XML потоково с использованием {@link XMLStreamReader},
     * извлекает данные о продуктах и записывает их в XLSX через Apache POI.
     * </p>
     *
     * @param xmlContent байтовое содержимое XML-файла; не должно быть {@code null} или пустым
     * @return массив байтов, представляющий XLSX-файл, готовый к скачиванию
     * @throws ConversionException если:
     *         <ul>
     *             <li>XML повреждён или не соответствует ожидаемой структуре</li>
     *             <li>Ошибка при генерации Excel (например, не хватает памяти)</li>
     *             <li>Отсутствуют обязательные поля (например, {@code nationalCode})</li>
     *         </ul>
     * @throws IllegalArgumentException если входной массив пуст
     * @since 1.0
     */
    @Override
    public byte[] convert(MultipartFile xml) {
        if (xml.isEmpty()) {
            throw new EmptyXmlFileException(xml.getName());
        }
        List<ProductData> products;
        try {
            products = parser.parseXml(xml.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return exporter.generateExcelToBytes(products);
    }

}