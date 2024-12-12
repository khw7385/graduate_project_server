package me.khw7385.graduate_project.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class QueryPlanFileReader {
    private static final QueryPlanFileReader INSTANCE = new QueryPlanFileReader();

    public static QueryPlanFileReader getInstance(){
        return INSTANCE;
    }

    private static final String DIL_PATH = "\\\\wsl$\\Ubuntu\\tmp\\";

    public Map<String, Object> readFile(String userId){
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = null;

        String filePath = String.format("%s\\%s.json", DIL_PATH, userId);
        try{
            data = objectMapper.readValue(new File(filePath), new TypeReference<Map<String, Object>>() {
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        return data;
    }
}
