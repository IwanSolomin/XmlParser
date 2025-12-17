package ru.oiteb.XmlParser.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("parser")
public class XmlParserController {

    private final XmlParserService parserService;

    public XmlParserController(XmlParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("parse")
    public Integer parseFile() {
        return parserService.parse();
    }
}
