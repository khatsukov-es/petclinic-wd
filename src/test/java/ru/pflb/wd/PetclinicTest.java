package ru.pflb.wd;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicTest {

    @Test
    public void generateKingMoves() {
        WebDriver firefoxDriver = new FirefoxDriver();

        firefoxDriver.get("http://localhost:4200/petclinic/");
        System.out.println("");
    }
}
