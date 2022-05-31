package com.sadoon.mockexchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;

public class JsonFileUtil {
    private ObjectMapper mapper;
    private InputStream fileStream;
    private JsonNode node;

    public JsonFileUtil(ObjectMapper mapper, String fileName){
        this.mapper = mapper;
        try {
            fileStream = JsonFileUtil.class.getResourceAsStream(fileName);
            node = toNode(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key){
        return node.get(key).asText();
    }

    public JsonNode getNode(String key){
        return node.get(key);
    }

    public ArrayNode createArrayNode(){
        return mapper.createArrayNode();
    }

    public void close(){
        try {
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonNode toNode(InputStream inputStream) throws IOException {
        return mapper.readTree(inputStream);
    }

}
