package ru.oiteb.xmlparser;

import ru.oiteb.xmlparser.entity.ProductData;

public class TestHelper {

    public static ProductData.ProductDataBuilder createProductDataBuilder() {
        return ProductData.builder()
                .indexNum(1)
                .unit("796")
                .okpd2("21.20")
                .nkmi("RU-123")
                .country("Россия")
                .fullName("Test")
                .tradeMark("OITEB");
    }

    public static ProductData getDefaultProductData() {
        return createProductDataBuilder().build();
    }

}
