package io.simakkoi9.ridesservice.util;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;

public class WireMockStubs {
    public static void mockPassengerService() {
        stubFor(
                WireMock.get(WireMock.urlPathMatching("/api/v1/passengers/1"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                    """
                                        {
                                            "id": 1,
                                            "name": "passenger",
                                            "email": "passenger@mail.com",
                                            "phone": "+375290987654"
                                        }
                                    """
                                )
                        )
        );
    }

    public static void mockInvalidPassengerService() {
        stubFor(
                WireMock.get(WireMock.urlPathMatching("/api/v1/passengers/999"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                            {
                                                "status": 404
                                            }
                                        """
                                )
                        )
        );
    }

    public static void mockFareService() {
        stubFor(
                WireMock.get(WireMock.urlPathMatching("/route/v1/driving/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                            {
                                                "routes": [
                                                     {
                                                         "distance": 1500.0
                                                     }
                                                ]
                                            }
                                        """
                                )
                        )
        );
    }
}
