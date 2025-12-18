package ru.oiteb.XmlParser.constants;

import org.apache.commons.math3.optim.linear.UnboundedSolutionException;

public final class CommonConstants {

    private CommonConstants(){
        throw new UnboundedSolutionException();
    }

    public static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String EXCEL_CONTENT_HEADER = "attachment; filename=\"converted.xlsx\"";
}
