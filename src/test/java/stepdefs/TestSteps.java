package stepdefs;

import com.microsoft.playwright.Page;
import hooks.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.S3Uploader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestSteps {

    private static final Logger logger = LoggerFactory.getLogger(TestSteps.class);

    private final Page page = Hooks.getPage();
    private Path screenshotPath;

    //opening google page
    @When("navigate to google page")
    public void navigateToGoogleHomepage() {
        logger.info("Navigating to Google Home Page");
        page.navigate("https://www.google.com");
        logger.info("Page loaded successfully");
    }

    //capturing screenshot
    @Then("screenshot of page is captured")
    public void captureScreenshot() {
        screenshotPath = Paths.get("target/screenshots/google-homepage.png");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
        logger.info("Screenshot captured successfully");
    }

    //screenshot capturing and uploading
    @Then("screenshot uploaded to S3 bucket")
    public void uploadScreenshot() {
        logger.info("Uploading screenshot to S3 bucket");
        S3Uploader.uploadFile(screenshotPath.toString());
    }
}