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
//        validateProducts(products);
        return exporter.generateExcelToBytes(products);
    }

}