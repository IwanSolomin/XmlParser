package ru.oiteb.xmlparser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.oiteb.xmlparser.exception.ConversationInternalError;
import ru.oiteb.xmlparser.service.ConversionService;

import static ru.oiteb.xmlparser.constants.CommonConstants.EXCEL_CONTENT_DISPOSITION;
import static ru.oiteb.xmlparser.constants.CommonConstants.EXCEL_XLSX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
@Tag(name = "Конвертер api", description = "API ля парсинга и конвертации")
public class ConversionController {

    private final ConversionService parserService;

    @PostMapping(value = "/xml-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Парсинг и конвертация из .xml в .xlsx",
            description = "Принимает XML-файл, парсит его и возвращает Excel-документ (.xlsx).",
            operationId = "convertXmlToExcel"
    )
    public ResponseEntity<byte[]> convertXmlToExcel(
            @Parameter(description = "XML-файл для конвертации", required = true)
            @RequestParam("file") MultipartFile xml) {
        try {
            byte[] excelBytes = parserService.convert(xml);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(EXCEL_XLSX))
                    .header(HttpHeaders.CONTENT_DISPOSITION, EXCEL_CONTENT_DISPOSITION)
                    .body(excelBytes);
        } catch (Exception e) {
            throw new ConversationInternalError(e.getMessage());
        }
    }

}
