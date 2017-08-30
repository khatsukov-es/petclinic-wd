package ru.pflb.wd;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicTest {

    /**
     * При клике по меню "Pet Types" не должно появляться 'Not Found - 404 error'
     */
    @Test(expected = NoSuchElementException.class)
    public void shouldDisplayPetTypes() {
        System.setProperty("webdriver.chrome.driver", new File("src/main/resources/chromedriver.exe").getAbsolutePath());
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);

        driver.get("http://localhost:4200/petclinic/");

        driver.findElement(By.xpath("//span[text()='Pet Types']")).click();

        driver.findElement(By.xpath("//h2[text()='Not Found - 404 error']"));
    }

    private WebDriver driver;

    @Before
    public void initDriver() throws URISyntaxException, IOException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        File firebug = new File(FirefoxDriver.class.getResource("/firebug-1.12.7-fx.xpi").toURI());
        File firepath = new File(FirefoxDriver.class.getResource("/firepath-0.9.7-fx.xpi").toURI());
        profile.addExtension(firebug);
        profile.addExtension(firepath);
        profile.setPreference("extensions.firebug.showFirstRunPage", false);
        capabilities.setCapability(PROFILE, profile);
        driver = new FirefoxDriver(capabilities);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
    }

    @After
    public void closeDriver() {
        driver.quit();
    }

    @Test
    public void shouldFindOwnerAndChangeHisName() {

        // открываем PetClinic по ссылке
        driver.get("http://localhost:8080/");

        // клик по меню Find Owners
        driver.findElement(By.xpath("//span[text()='Find owners']")).click();

        // ввод фамилии Franklin в поле Last name
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("Franklin");

        // клик по Find Owner
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // считываение Telephone
        Long telephone = Long.valueOf(driver.findElement(By.xpath("//th[text()='Telephone']/following-sibling::td")).getText());
        // прибавление единицы для нового телефона
        Long newTelephone = telephone += 1;

        // нажатие кнопки редактировать пользователя Edit Owner
        driver.findElement(By.xpath("//a[contains(@href, '/edit') and contains(text(), 'Owner')]")).click();

        // в поле Telephone ввести новый телефон
        WebElement telephoneEditBox = driver.findElement(By.xpath("//input[@id='telephone']"));
        telephoneEditBox.clear();
        telephoneEditBox.sendKeys("" + newTelephone);

        // нажать Update Owner
        driver.findElement(By.xpath("//button[text()='Update Owner']")).click();

        // считать измененный телефон
        Long updatedPhone = Long.valueOf(driver.findElement(By.xpath("//th[text()='Telephone']/following-sibling::td")).getText());

        // проверить что телефон изменен на нужный
        assertThat(updatedPhone).isEqualTo(newTelephone);
    }


    @Test
    public void shouldAddNewPet() {
        // открываем PetClinic по ссылке
        driver.get("http://localhost:8080/");

        // клик по меню Find Owners
        driver.findElement(By.xpath("//span[text()='Find owners']")).click();

        // ввод фамилии Franklin в поле Last name
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys("Franklin");

        // клик по Find Owner
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        String rndVal = RandomStringUtils.randomAlphabetic(4);

        for (String animal : Arrays.asList("bird", "cat", "dog", "hamster", "lizard", "snake")) {
            // клик по Add new Pet
            driver.findElement(By.xpath("//a[starts-with(text(), 'Add') and contains(text(), 'New Pet')]")).click();

            // в выпадающем списке вид тип животного
            Select select = new Select(driver.findElement(By.xpath("//select[@id='type']")));
            select.selectByValue(animal);
            // в поле имя ввести уникальное имя по виду животного в формате типрандом (слитно)
            driver.findElement(By.xpath("//input[@id='name']")).sendKeys(animal + rndVal);

            // в поле даты рождения ввести 2017/01/01
            driver.findElement(By.xpath("//input[@id='birthDate']")).sendKeys("2017/01/01");

            // кликнуть Update Pet
            driver.findElement(By.xpath("//button[text()='Update Pet']")).click();

            // должен быть найден только один блок с новым комбинированным именем и относящимся к нему виду животного
            assertThat(driver.findElements(By.xpath(String.format(
                            "//dl[self::*/descendant::dd/text()[1] ='%1$s%2$s' and self::*/descendant::dt[.='Type']/following-sibling::dd/text()[1] = '%1$s']",
                    animal, rndVal
            ))).size())
                    .isEqualTo(1);
        }


    }

    /**
     * Домашнее задание.
     * <p>
     * Сценарий:<ol>
     * <li>Открыть http://localhost:4200/</li>
     * <li>Перейти в меню Owners -> Add new</li>
     * <li>Ввести валидные случайные данные (новые для каждого запуска теста)</li>
     * <li>Нажать Add Owner, открылась страница Owners</li>
     * <li>Проверить, что добавилась новая запись, и все ее поля соответствуют введенным значениям</li>
     * </ul>
     */
    public void shouldValidateAddedUser() {
        // TODO
    }
}
