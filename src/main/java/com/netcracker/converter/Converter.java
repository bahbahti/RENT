package com.netcracker.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.entity.DTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.IOException;


@Repository
public class Converter {

   /* @Value("${build.version}")
    private String buildVersion;*/



    public String convertPojoToJSON(String buildVersion) {
        String javaVersion = System.getProperty("java.version");
        DTO pojo = new DTO(javaVersion, buildVersion);
        String jsonString = "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pojo);
            System.out.println(jsonString);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return jsonString;
    }
}
