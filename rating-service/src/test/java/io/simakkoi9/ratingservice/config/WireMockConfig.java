package io.simakkoi9.ratingservice.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpStatus;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import java.util.Collections;
import java.util.Map;

public class WireMockConfig implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8085);
        wireMockServer.start();

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/rides/" + TestDataUtil.RIDE_ID))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(HttpStatus.SC_OK)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(TestDataUtil.RIDE_REQUEST_JSON)
                        )
        );

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/rides/" + TestDataUtil.WRONG_RIDE_ID))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(HttpStatus.SC_NOT_FOUND)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(TestDataUtil.RIDE_NOT_FOUND_JSON)
                        )
        );

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/rides/" + TestDataUtil.UNCOMPLETED_RIDE_ID))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(HttpStatus.SC_OK)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(TestDataUtil.UNCOMPLETED_RIDE_REQUEST_JSON)
                        )
        );

        return Collections.singletonMap("quarkus.rest-client.ridesClient.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
} 