package io.simakkoi9.ratingservice.e2e;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.simakkoi9.ratingservice.config.TestContainersConfig;
import io.simakkoi9.ratingservice.config.WireMockConfig;

@QuarkusTestResource(TestContainersConfig.class)
@QuarkusTestResource(WireMockConfig.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"io.simakkoi9.ratingservice.e2e"},
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class QuarkusCucumberTest extends CucumberQuarkusTest {

}
