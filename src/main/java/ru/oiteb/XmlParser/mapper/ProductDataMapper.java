package ru.oiteb.XmlParser.mapper;

import ru.oiteb.XmlParser.entity.ProductData;

public class ProductDataMapper {

    public static ProductData toProductData(int indexNum, String unit, String okpd2, String nkmi, String manufacturer,
                                            String country, String certNumber, String fullName, String tradeMark) {
        return ProductData.builder()
                .indexNum(indexNum)
                .unit(sanitize(unit))
                .okpd2(sanitize(okpd2))
                .nkmi(sanitize(nkmi))
                .manufacturer(sanitize(manufacturer))
                .country(sanitize(country))
                .certNumber(sanitize(certNumber))
                .fullName(sanitize(fullName))
                .tradeMark(sanitize(tradeMark))
                .build();
    }

    private static String sanitize(String s) {
        return s == null ? "" : s.trim();
    }
}
