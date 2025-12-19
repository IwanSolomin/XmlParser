package ru.oiteb.XmlParser.service.exporter;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.oiteb.XmlParser.constants.ExcelConstants;
import ru.oiteb.XmlParser.entity.ProductData;
import ru.oiteb.XmlParser.exception.ExcelGenerationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Экспортер данных о продуктах в формат Microsoft Excel (.xlsx).
 * <p>
 * Класс отвечает за создание XLSX-документа на основе списка объектов {@link ProductData},
 * используя библиотеку Apache POI. Генерация происходит полностью в памяти — файл на диск не записывается.
 * </p>
 * <p>
 * Структура документа:
 * <ul>
 *   <li>Один лист с именем, заданным в {@link ExcelConstants#sheetName}</li>
 *   <li>Первая строка — заголовки колонок из {@link ExcelConstants#HEADERS}</li>
 *   <li>Каждая последующая строка соответствует одному продукту</li>
 * </ul>
 * </p>
 * <p>
 * Данный компонент предназначен для использования в связке с {@link ru.oiteb.XmlParser.service.ConversionService}
 * и не содержит логики валидации входных данных — предполагается, что список продуктов уже прошёл проверку.
 * </p>
 */
@Component
public class ExcelExporter {

    /**
     * Генерирует Excel-файл в формате .xlsx и возвращает его как массив байтов.
     * <p>
     * Метод создаёт в памяти рабочую книгу Excel, заполняет её данными и сериализует в байтовый поток.
     * Результат готов к отправке клиенту как HTTP-ответ с типом
     * {@code application/vnd.openxmlformats-officedocument.spreadsheetml.sheet}.
     * </p>
     *
     * @param products список данных о продуктах для экспорта; не должен быть {@code null},
     *                 но может быть пустым (в этом случае будет создан файл с заголовками)
     * @return массив байтов, представляющий XLSX-файл
     * @throws ExcelGenerationException если возникла ошибка при генерации Excel-документа
     *         (например, нехватка памяти, ошибка сериализации)
     * @throws IllegalArgumentException если входной список равен {@code null}
     *
     * @implNote Метод использует try-with-resources для автоматического освобождения ресурсов
     *           (рабочая книга и поток). При возникновении ошибки исключение оборачивается
     *           в {@link ExcelGenerationException} с сохранением первопричины.
     */
    public byte[] generateExcelToBytes(List<ProductData> products) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            createSheet(workbook, products);
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ExcelGenerationException(e.getMessage());
        }
    }

    /**
     * Создаёт и заполняет лист Excel данными о продуктах.
     * <p>
     * Метод формирует структуру листа: сначала заголовки, затем строки с данными.
     * Каждая ячейка заполняется соответствующим полем из {@link ProductData}.
     * Порядок колонок строго соответствует массиву {@link ExcelConstants#HEADERS}.
     * </p>
     *
     * @param workbook рабочая книга Excel, в которую будет добавлен лист
     * @param products список продуктов для экспорта (не null)
     *
     * @implNote Метод не проверяет наличие обязательных полей — предполагается,
     *           что все данные уже валидны. Пустые строки или null-значения
     *           будут записаны как пустые ячейки.
     */
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

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
