package me.khw7385.graduate_project.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class QueryPlanFileReader {
    private static final String FILE_PATH = "\\\\wsl$\\Ubuntu\\tmp\\test.json";

    public Map<String, Object> readFile(String userId){
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = null;
        try{
            data = objectMapper.readValue(new File(FILE_PATH), new TypeReference<Map<String, Object>>() {
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        return data;
    }
}
