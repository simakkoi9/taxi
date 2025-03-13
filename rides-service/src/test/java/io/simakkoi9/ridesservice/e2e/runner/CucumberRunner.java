package io.simakkoi9.ridesservice.e2e.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "io.simakkoi9.ridesservice.e2e",
        plugin = {
            "pretty",
            "json:target/cucumber-reports/cucumber-report.json",
            "html:target/cucumber-reports/report.html"
        }
)
public class CucumberRunner {

}
