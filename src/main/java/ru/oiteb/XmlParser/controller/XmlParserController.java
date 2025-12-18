package ru.oiteb.XmlParser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.oiteb.XmlParser.exception.ConversationInternalError;
import ru.oiteb.XmlParser.service.ConversationService;

import static ru.oiteb.XmlParser.constants.CommonConstants.EXCEL_CONTENT_HEADER;
import static ru.oiteb.XmlParser.constants.CommonConstants.EXCEL_MEDIA_TYPE;

@RestController
@RequestMapping("v1/api")
@Tag(name = "Конвертер api", description = "API ля парсинга и конвертации")
public class XmlParserController {

    private final ConversationService parserService;

    public XmlParserController(ConversationService parserService) {
        this.parserService = parserService;
    }

    @PostMapping(value = "/xml-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Парсинг и конвертация из .xml в .xlsx",
            operationId = "convertXmlToExcel"
    )
    public ResponseEntity<byte[]> convertXmlToExcel(
            @Parameter(description = "XML-файл для конвертации", required = true)
            @RequestParam("file") MultipartFile xml) {
        try {
            byte[] excelBytes = parserService.convert(xml);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, EXCEL_CONTENT_HEADER)
                    .body(excelBytes);
        } catch (Exception e) {
            throw new ConversationInternalError(e.getMessage());
        }
    }

}
