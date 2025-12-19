package ru.oiteb.xmlparser.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import ru.oiteb.xmlparser.entity.ProductData;
import ru.oiteb.xmlparser.service.exporter.ExcelExporter;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static ru.oiteb.xmlparser.TestHelper.getDefaultProductData;
import static ru.oiteb.xmlparser.constants.ExceptionsDescriptions.XML_CREATION_EXCEPTION;

class ExcelExporterTest {

    private final ExcelExporter exporter = new ExcelExporter();

    @Test
    void shouldGenerateValidXlsx() {
        ProductData p = getDefaultProductData();

        byte[] excelBytes = exporter.generateExcelToBytes(List.of(p));

        assertThat(excelBytes).isNotEmpty();

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            var sheet = workbook.getSheetAt(0);
            assertThat(sheet.getLastRowNum()).isEqualTo(1);

            var row = sheet.getRow(1);
            assertThat(row.getCell(0).getNumericCellValue()).isEqualTo((double) p.getIndexNum());
            assertThat(row.getCell(1).getStringCellValue()).isEqualTo(p.getUnit());
            assertThat(row.getCell(7).getStringCellValue()).isEqualTo(p.getFullName());
        } catch (Exception e) {
            fail(XML_CREATION_EXCEPTION, e);
        }
    }

    @Test
    void shouldHandleEmptyList() {
        byte[] bytes = exporter.generateExcelToBytes(List.of());
        assertThat(bytes).isNotEmpty();
    }

}