package ru.oiteb.XmlParser.service.exporter;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.oiteb.XmlParser.coonstants.ExcelConstants;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.GenerateExcelException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    public byte[] generateExcelToBytes(List<ProductData> products) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            createSheet(workbook, products);
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new GenerateExcelException(e.getMessage());
        }
    }

    private void createSheet(XSSFWorkbook workbook, List<ProductData> products) {
        XSSFSheet sheet = workbook.createSheet(ExcelConstants.sheetName);
        String[] headers = ExcelConstants.HEADERS;
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        for (int i = 0; i < products.size(); i++) {
            ProductData p = products.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(p.getIndexNum());
            row.createCell(1).setCellValue(p.getUnit());
            row.createCell(2).setCellValue(p.getOkpd2());
            row.createCell(3).setCellValue(p.getNkmi());
            row.createCell(4).setCellValue(p.getManufacturer());
            row.createCell(5).setCellValue(p.getCountry());
            row.createCell(6).setCellValue(p.getCertNumber());
            row.createCell(7).setCellValue(p.getFullName());
            row.createCell(8).setCellValue(p.getTradeMark());
        }
    }
}
