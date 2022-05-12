package com.sadoon.mockexchange;

import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.dsl.EventDoneable;
import io.fabric8.mockwebserver.dsl.TimesOnceableOrHttpHeaderable;

public class MockExchange {
    private DefaultMockServer mockExchange;
    private JsonFileUtil fileUtil;

    public MockExchange(JsonFileUtil fileUtil) {
        this.fileUtil = fileUtil;
        mockExchange = new DefaultMockServer();
        mockExchange.start(Integer.parseInt(fileUtil.getValue("port")));
        mockExchange.url(fileUtil.getValue("url"));
        setEndpoints();
        setWebSocketEndpoints();
    }

    public void shutdown() {
        fileUtil.close();
        mockExchange.shutdown();
    }

    private void setEndpoints() {
        createAlwaysRespondingEndpoint("assetPairsNode", "assetPairs");
        createAlwaysRespondingEndpoint("balanceNode", "balance");
        createAlwaysRespondingEndpoint("tradeVolumeNode", "tradeVolume");
    }

    private void setWebSocketEndpoints() {
        EventDoneable<TimesOnceableOrHttpHeaderable<Void>> websocket = mockExchange.expect()
                .withPath(getEndpoint("ws"))
                .andUpgradeToWebSocket()
                .open();

        websocket.waitFor(2000).andEmit(fileUtil.getNode("wsTickerNode").get("first").toString());
        websocket.waitFor(5000).andEmit(fileUtil.getNode("wsTickerNode").get("second").toString());

        websocket
                .done()
                .always();
    }

    private String getEndpoint(String endpoint) {
        return fileUtil.getNode("endpoint").get(endpoint).asText();
    }

    private void createAlwaysRespondingEndpoint(String nodeName, String endpointName) {
        JsonNode node = fileUtil.getNode(nodeName);
        mockExchange.expect()
                .withPath(getEndpoint(endpointName))
                .andReturn(node.get("status").asInt(), node.get(endpointName).toString())
                .withHeader("Content-Type: application/json")
                .always();
    }
}
