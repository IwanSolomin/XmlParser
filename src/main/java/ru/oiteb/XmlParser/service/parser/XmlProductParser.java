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

import static ru.oiteb.XmlParser.dto.mapper.ProductDataMapper.toProductData;

@Component
public class XmlProductParser {

    private final XMLInputFactory xmlFactory;

    public XmlProductParser(XMLInputFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    public List<ProductData> parseXml(byte[] xmlBytes) {
        List<ProductData> products = new ArrayList<>();
        XMLStreamReader reader = null;
        try (InputStream is = new ByteArrayInputStream(xmlBytes);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            reader = xmlFactory.createXMLStreamReader(bis);
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "productInfo".equals(reader.getLocalName())) {
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
                    case "indexNum":
                        String numText = readElementText(reader);
                        if (!numText.isEmpty()) {
                            indexNum = Integer.parseInt(numText);
                        }
                        break;
                    case "name":
                        if (fullName.isEmpty()) {
                            fullName = readElementText(reader);
                        } else {
                            readElementText(reader);
                        }
                        break;
                    case "trademarkInfo":
                        while (reader.hasNext()) {
                            int e = reader.next();
                            if (e == XMLEvent.START_ELEMENT && "trademark".equals(reader.getLocalName())) {
                                trademark = readElementText(reader);
                                break;
                            }
                            if (e == XMLEvent.END_ELEMENT && "trademarkInfo".equals(reader.getLocalName())) {
                                break;
                            }
                        }
                        break;
                    case "OKEIInfo":
                        unit = parseOKEIUnit(reader);
                        break;
                    case "OKPD2Info":
                        while (reader.hasNext()) {
                            int e = reader.next();
                            if (e == XMLEvent.START_ELEMENT && "OKPDCode".equals(reader.getLocalName())) {
                                okpd2 = readElementText(reader);
                                break;
                            }
                            if (e == XMLEvent.END_ELEMENT && "OKPD2Info".equals(reader.getLocalName())) {
                                break;
                            }
                            if (e == XMLEvent.START_ELEMENT) {
                                readElementText(reader);
                            }
                        }
                        break;
                    case "medicalProductCode":
                        nkmi = readElementText(reader);
                        break;
                    case "countryFullName":
                        country = readElementText(reader);
                        break;
                    default:
                        readElementText(reader);
                        break;
                }
            }
            if (event == XMLEvent.END_ELEMENT && "productInfo".equals(reader.getLocalName())) {
                break;
            }
        }
        return toProductData(indexNum, unit, okpd2, nkmi, manufacturer, country, certNumber, fullName, trademark);
    }

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

    private String parseOKEIUnit(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.START_ELEMENT) {
                if ("nationalCode".equals(reader.getLocalName())) {
                    return readElementText(reader);
                } else {
                    readElementText(reader);
                }
            } else if (event == XMLEvent.END_ELEMENT) {
                if ("OKEIInfo".equals(reader.getLocalName())) {
                    break;
                }
            }
        }
        return "";
    }

}
