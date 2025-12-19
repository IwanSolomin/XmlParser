package ru.oiteb.xmlparser.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.oiteb.xmlparser.exception.EmptyXmlFileException;
import ru.oiteb.xmlparser.service.ConversionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.oiteb.xmlparser.constants.CommonConstants.EXCEL_CONTENT_DISPOSITION;
import static ru.oiteb.xmlparser.constants.CommonConstants.EXCEL_XLSX;

@WebMvcTest(ConversionController.class)
public class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ConversionService conversionService;

    @Test
    void shouldReturnExcelFileOnValidXml() throws Exception {
        byte[] fakeExcel = new byte[]{0x50, 0x4B, 0x03, 0x04};
        when(conversionService.convert(any())).thenReturn(fakeExcel);

        MockMultipartFile xmlFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                "<products><productInfo><indexNum>1</indexNum></productInfo></products>".getBytes()
        );

        mockMvc.perform(multipart("/v1/api/xml-converter")
                        .file(xmlFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", EXCEL_XLSX))
                .andExpect(header().string("Content-Disposition", EXCEL_CONTENT_DISPOSITION))
                .andExpect(content().bytes(fakeExcel));
    }

    @Test
    void shouldReturn500OnConversionFailure() throws Exception {
        when(conversionService.convert(any()))
                .thenThrow(new RuntimeException("Internal error"));

        MockMultipartFile xmlFile = new MockMultipartFile(
                "file", "test.xml", "application/xml", "<a></a>".getBytes()
        );

        mockMvc.perform(multipart("/v1/api/xml-converter")
                        .file(xmlFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        when(conversionService.convert(any()))
                .thenThrow(new EmptyXmlFileException("File is empty"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.xml", "application/xml", new byte[0]);

        mockMvc.perform(multipart("/v1/api/xml-converter").file(emptyFile))
                .andExpect(status().is5xxServerError());
    }

}
