package ru.oiteb.xmlparser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.oiteb.xmlparser.entity.ProductData;
import ru.oiteb.xmlparser.exception.EmptyXmlFileException;
import ru.oiteb.xmlparser.exception.InvalidXmlFileException;
import ru.oiteb.xmlparser.service.Impl.ConversionServiceImpl;
import ru.oiteb.xmlparser.service.exporter.ExcelExporter;
import ru.oiteb.xmlparser.service.parser.XmlProductParser;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConversionServiceImplTest {

    @Mock
    private XmlProductParser parser;
    @Mock
    private ExcelExporter exporter;
    @InjectMocks
    private ConversionServiceImpl service;

    @Test
    void shouldConvertValidXmlToExcel() {
        byte[] xmlBytes = "<products></products>".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "application/xml", xmlBytes);

        List<ProductData> products = Collections.singletonList(new ProductData());
        when(parser.parseXml(xmlBytes)).thenReturn(products);
        when(exporter.generateExcelToBytes(products)).thenReturn(new byte[100]);

        byte[] result = service.convert(file);

        assertThat(result).hasSize(100);
        verify(parser).parseXml(xmlBytes);
        verify(exporter).generateExcelToBytes(products);
    }

    @Test
    void shouldThrowOnNullFile() {
        assertThatThrownBy(() -> service.convert(null))
                .isInstanceOf(InvalidXmlFileException.class);
    }

    @Test
    void shouldThrowOnEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.xml", "application/xml", new byte[0]);
        assertThatThrownBy(() -> service.convert(empty))
                .isInstanceOf(EmptyXmlFileException.class);
    }

}
