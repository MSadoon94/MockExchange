package com.sadoon.mockexchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.dsl.EventDoneable;
import io.fabric8.mockwebserver.dsl.TimesOnceableOrHttpHeaderable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MockExchange {
    private DefaultMockServer mockExchange;
    private JsonFileUtil fileUtil;
    private Random random = new Random();

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

        for (int i = 10000; i <= 50000; i += 1000) {
            websocket.waitFor(i).andEmit(createRandomTicker().toString());
        }

        websocket
                .done()
                .always();
    }

    private JsonNode createRandomTicker() {
        JsonNode node = fileUtil.getNode("wsTickerNode").get("template");
        ((ObjectNode) node.get(1)).replace("b",
                fileUtil.createArrayNode().add(
                        BigDecimal.valueOf(random.nextDouble(70, 100))
                                .setScale(2, RoundingMode.HALF_UP)));
        return node;
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
