package com.sadoon.mockexchange;

import io.fabric8.mockwebserver.DefaultMockServer;

public class MockExchange {
    private DefaultMockServer mockExchange;
    private JsonFileUtil fileUtil;

    public MockExchange(JsonFileUtil fileUtil) {
        this.fileUtil = fileUtil;
        mockExchange = new DefaultMockServer();
        mockExchange.start(Integer.parseInt(fileUtil.getValue("port")));
        mockExchange.url(fileUtil.getValue("url"));
        setEndpoints();
    }

    public void shutdown() {
        fileUtil.close();
        mockExchange.shutdown();
    }

    private void setEndpoints() {
        mockExchange.expect()
                .withPath(getEndpoint("assetPair"))
                .andReturn(200, fileUtil.getNode("balance").toString())
                .withHeader("Content-Type: application/json")
                .always();

        mockExchange.expect()
                .withPath(getEndpoint("balance"))
                .andReturn(200, fileUtil.getNode("balance").toString())
                .withHeader("Content-Type: application/json")
                .always();
    }

    private String getEndpoint(String endpoint) {
        return fileUtil.getNode("endpoint").get(endpoint).asText();
    }
}
