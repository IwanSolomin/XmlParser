package ru.oiteb.xmlparser.constants;

import org.apache.commons.math3.optim.linear.UnboundedSolutionException;

public final class ExcelConstants {
    private ExcelConstants() {
        throw new UnboundedSolutionException();
    }

    public static final String[] HEADERS = {
            "№ п/п",
            "Единицы измерения",
            "ОКПД2",
            "НКМИ",
            "Производитель",
            "Страна производства",
            "Рег. номер удостоверения",
            "Полное  наименование",
            "Товарный знак"
    };

    public static final String sheetName = "Продукты";

}
