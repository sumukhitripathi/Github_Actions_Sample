package stepdefs;

import com.microsoft.playwright.Page;
import hooks.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.S3Uploader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestSteps {

    private final Page page = Hooks.getPage();
    private Path screenshotPath;

    @When("navigate to google page")
    public void navigateToGoogleHomepage() {
        page.navigate("https://www.google.com");
    }

    @Then("screenshot of page is captured")
    public void captureScreenshot() {
        screenshotPath = Paths.get("target/screenshots/google-homepage.png");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
    }

    @Then("screenshot uploaded to S3 bucket")
    public void uploadScreenshot() {
        S3Uploader.uploadFile(screenshotPath.toString());
    }
}