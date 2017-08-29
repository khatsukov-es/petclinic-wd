package ru.pflb.wd;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicTest {

    @Test
    public void shouldDisplayPetTypes() {
        System.setProperty("webdriver.chrome.driver", new File("src/main/resources/chromedriver.exe").getAbsolutePath());
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("http://localhost:4200/petclinic/");

        driver.findElement(By.linkText("Pet Types")).click();

        List<WebElement> h2 = driver.findElements(By.tagName("h2"));
        for (WebElement we : h2) {
            if ("Not Found - 404 error".equals(we.getText())) {
                System.out.println("");
            }
        }

        System.out.println("");
    }
}
