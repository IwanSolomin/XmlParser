package ru.oiteb.XmlParser.service;

import org.springframework.web.multipart.MultipartFile;

public interface ConversationService {

    byte[] convert(MultipartFile xml);
}
