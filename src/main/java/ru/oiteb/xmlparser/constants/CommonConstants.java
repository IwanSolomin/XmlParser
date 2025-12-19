package ru.oiteb.xmlparser.constants;

import org.apache.commons.math3.optim.linear.UnboundedSolutionException;

public final class CommonConstants {

    private CommonConstants(){
        throw new UnboundedSolutionException();
    }

    public static final String EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String EXCEL_CONTENT_DISPOSITION = "attachment; filename=\"converted.xlsx\"";

}
