package demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.logging.Level;

public class TestCases {
    static WebDriver driver;
    static WebDriverWait wait;

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public static void openURL(String url) {
        driver.get(url);
    }

    public static void click_on_HTFSP() {
        WrapperMethods.clickOperation(driver, By.linkText("Hockey Teams: Forms, Searching and Pagination"));
    }

    public static void click_on_OWFAJ() {
        try {
            WrapperMethods.clickOperation(driver, By.linkText("Oscar Winning Films: AJAX and Javascript"));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody[@id='table-body']/tr")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pages_on_HTFSP(int pageNo) {
        try {
            WebElement page = driver.findElement(By.xpath("//ul[@class='pagination']/li/a[normalize-space()='" + String.valueOf(pageNo) + "']"));
            page.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='table']/tbody/tr")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<HashMap<String, String>> get_The_Movie_Details_on_OWFAJ() {
        try {
            Boolean isWinner = true;
            ArrayList<HashMap<String, String>> listOfHashMaps = new ArrayList<>();
            List<WebElement> page_of_year = driver.findElements(By.xpath("//a[contains(@class,'year-link')]"));
            for (WebElement webElement : page_of_year) {
                webElement.click();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody[@id='table-body']/tr")));
                int count = 0;
                List<WebElement> title = driver.findElements(By.xpath("//tbody[@id='table-body']/tr/td[1]"));
                List<WebElement> nominations = driver.findElements(By.xpath("//tbody[@id='table-body']/tr/td[2]"));
                List<WebElement> award = driver.findElements(By.xpath("//tbody[@id='table-body']/tr/td[3]"));
                for (WebElement ele : title) {
                    if (count == 5) {
                        break;
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Epoch Time of Scrape", String.valueOf(System.currentTimeMillis()));
                    map.put("Year", webElement.getText());
                    map.put("Title", ele.getText());
                    map.put("Nominations", nominations.get(count).getText());
                    map.put("Award", award.get(count).getText());
                    if (count == 0) {
                        isWinner = true;
                    } else {
                        isWinner = false;
                    }
                    map.put("isWinner", String.valueOf(isWinner));
                    listOfHashMaps.add(map);
                    count++;
                }
            }
            return listOfHashMaps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<HashMap<String, String>> get_TeamName_Year_Win_where_win_less_than_40_percentage(int pages) {
        try {
            ArrayList<HashMap<String, String>> listOfHashMaps = new ArrayList<>();
            for (int i = 1; i <= pages; i++) {
                pages_on_HTFSP(i);
                List<WebElement> win_percentage = driver.findElements(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Win %']/following::tr/td[6]"));
                List<WebElement> teamName = driver.findElements(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Team Name']/following::tr/td[1]"));
                List<WebElement> year = driver.findElements(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Year']/following::tr/td[2]"));
                int count = 0;
                for (WebElement webElement : win_percentage) {
                    HashMap<String, String> map1 = new HashMap<>();
                    if (Double.parseDouble(webElement.getText()) < 0.40 && Double.parseDouble(webElement.getText()) > 0) {
                        map1.put("Epoch Time of Scrape", String.valueOf(System.currentTimeMillis()));
                        map1.put("Win Percentage", webElement.getText());
                        map1.put("Team Name", teamName.get(count).getText());
                        map1.put("Year", year.get(count).getText());
                        listOfHashMaps.add(map1);
                    }
                    count++;
                }
            }
            return listOfHashMaps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeResultsToJson(List<HashMap<String, String>> results, String outputDirectory, String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            Path outputPath = Paths.get(outputDirectory, fileName);
            Files.createDirectories(outputPath.getParent());
            mapper.writeValue(outputPath.toFile(), results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCase01() {
        try {
            System.out.println("Starting testCase01");
            openURL("https://www.scrapethissite.com/pages/");
            System.out.println("Opened URL for testCase01");
            click_on_HTFSP();
            System.out.println("Clicked on Hockey Teams: Forms, Searching and Pagination");
            String outputDirectory = "output";
            String fileName = "hockey-team-data.json";
            writeResultsToJson(get_TeamName_Year_Win_where_win_less_than_40_percentage(4), outputDirectory, fileName);
            System.out.println("Data written to JSON file for testCase01");
            File outputFile = new File(outputDirectory, fileName);
            Assert.assertTrue(outputFile.exists(), "JSON file does not exist in the output folder");
            Assert.assertTrue(outputFile.length() > 0, "JSON file is empty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCase02() {
        try {
            System.out.println("Starting testCase02");
            openURL("https://www.scrapethissite.com/pages/");
            System.out.println("Opened URL for testCase02");
            click_on_OWFAJ();
            System.out.println("Clicked on Oscar Winning Films: AJAX and Javascript");
            String outputDirectory = "output";
            String fileName = "oscar-winning-films-data.json";
            writeResultsToJson(get_The_Movie_Details_on_OWFAJ(), outputDirectory, fileName);
            System.out.println("Data written to JSON file for testCase02");
            File outputFile = new File(outputDirectory, fileName);
            Assert.assertTrue(outputFile.exists(), "JSON file does not exist in the output folder");
            Assert.assertTrue(outputFile.length() > 0, "JSON file is empty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterSuite
    public void closeBrowser() {
        driver.quit();
    }
}
