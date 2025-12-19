package ru.oiteb.xmlparser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.oiteb.xmlparser.entity.ProductData;
import ru.oiteb.xmlparser.exception.InvalidXmlFileException;
import ru.oiteb.xmlparser.service.parser.XmlProductParser;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XmlProductParserTest {

    private XmlProductParser parser;

    @BeforeEach
    void setUp() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        parser = new XmlProductParser(factory);
    }

    @Test
    void shouldParseValidXml() throws IOException {
        byte[] xml = Files.readAllBytes(Path.of("src/test/resources/valid-sample.xml"));
        List<ProductData> products = parser.parseXml(xml);

        assertThat(products).hasSize(1);
        ProductData p = products.get(0);
        assertThat(p.getIndexNum()).isEqualTo(100);
        assertThat(p.getFullName()).isEqualTo("Аспирин");
        assertThat(p.getTradeMark()).isEqualTo("Bayer");
        assertThat(p.getUnit()).isEqualTo("796");
        assertThat(p.getOkpd2()).isEqualTo("21.20.11.110");
        assertThat(p.getNkmi()).isEqualTo("RU-123");
        assertThat(p.getCountry()).isEqualTo("Германия");
    }

    @Test
    void shouldThrowOnInvalidXml() {
        byte[] invalid = "<a></b>".getBytes();
        assertThatThrownBy(() -> parser.parseXml(invalid))
                .isInstanceOf(InvalidXmlFileException.class);
    }

    @Test
    void shouldThrowOnMissingNationalCode() throws IOException {
        byte[] xml = Files.readAllBytes(Path.of("src/test/resources/invalid-sample.xml"));
        assertThatThrownBy(() -> parser.parseXml(xml))
                .isInstanceOf(InvalidXmlFileException.class);
    }

}
