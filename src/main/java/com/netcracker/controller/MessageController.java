package com.netcracker.controller;

import com.netcracker.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("version")
public class MessageController {

    @Value("${build.version}")
    private String buildVersion;


    @Autowired
    Converter converter;

    @GetMapping
    public String getInfo() {
        return converter.convertPojoToJSON(buildVersion);
    }
}
