package com.netcracker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("version")
public class MessageController {

    @Value("${build.version}")
    private String projectVersion;

    String javaVersion = Runtime.class.getPackage().getImplementationVersion();

    @GetMapping
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\nApplication version: " + projectVersion);
        sb.append(",\nJava version: " + javaVersion);
        sb.append("\n}");
        return sb.toString();
    }
}
