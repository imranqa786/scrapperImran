package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WrapperMethods {
    public static void clickOperation(WebDriver driver,By locator){
        try {
            driver.findElement(locator).click();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}