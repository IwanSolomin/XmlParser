package ru.oiteb.xmlparser.service;

import org.springframework.web.multipart.MultipartFile;

public interface ConversionService {

    byte[] convert(MultipartFile xml);

}
