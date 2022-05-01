package com.sadoon.mockexchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;

public class MockExchangeApplication {
    public static void main(String[] args){
        ArrayList<MockExchange> mockExchanges = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Arrays.stream(args)
                .forEach(arg -> {
                    JsonFileUtil fileUtil = new JsonFileUtil(mapper, arg);
                    MockExchange mockExchange = new MockExchange(fileUtil);
                    mockExchanges.add(mockExchange);
                });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> mockExchanges.forEach(MockExchange::shutdown)));
    }
}
