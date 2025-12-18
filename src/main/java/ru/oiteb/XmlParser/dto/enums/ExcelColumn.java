//package ru.oiteb.XmlParser.dto.enums;
//
//import lombok.Getter;
//import ru.oiteb.XmlParser.entity.ProductData;
//
//import java.util.function.Function;
//
//public enum ExcelColumn {
//    INDEX_NUM("Индекс", ProductData::getIndexNum),
//    UNIT("Ед. изм.", ProductData::getUnit),
//    OKPD2("ОКПД2", ProductData::getOkpd2),
//    NKMI("НКМИ", ProductData::getNkmi),
//    MANUFACTURER("Производитель", ProductData::getManufacturer),
//    COUNTRY("Страна", ProductData::getCountry),
//    CERT_NUMBER("Номер сертификата", ProductData::getCertNumber),
//    FULL_NAME("Наименование", ProductData::getFullName),
//    TRADE_MARK("Торговая марка", ProductData::getTradeMark);
//
//    @Getter
//    private final String header;
//    private final Function<ProductData, ?> extractor;
//
//    ExcelColumn(String header, Function<ProductData, ?> extractor) {
//        this.header = header;
//        this.extractor = extractor;
//    }
////
////    public String getHeader() {
////        return header;
////    }
//
//    public Object getValue(ProductData product) {
//        Object value = extractor.apply(product);
//        return value == null ? "" : value;
//    }
//}
