package io.simakkoi9.ratingservice.e2e;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.simakkoi9.ratingservice.config.TestContainersConfig;
import io.simakkoi9.ratingservice.config.WireMockConfig;
import io.simakkoi9.ratingservice.controller.RatingController;
import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;

@QuarkusTestResource(TestContainersConfig.class)
@QuarkusTestResource(WireMockConfig.class)
@TestHTTPEndpoint(RatingController.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"io.simakkoi9.ratingservice.e2e"},
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberRatingTest extends CucumberQuarkusTest {

}
