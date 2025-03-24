package io.simakkoi9.passengerservice.e2e.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = {
                "io.simakkoi9.passengerservice.e2e"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-report.html"
        }
)
public class CucumberRunnerTest {
}
