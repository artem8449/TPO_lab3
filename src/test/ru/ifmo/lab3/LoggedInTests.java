package ru.ifmo.lab3;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoggedInTests {
    private static WebDriver driver;
    private static String panelUrl;

    @BeforeAll
    static void setUp() throws AWTException {
        System.setProperty("webdriver.gecko.driver", "C:/SeleniumDrivers/geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().window().maximize();

        AccountController.login(driver);
        String url = AccountController.getAnyCreatedWebsite(driver);
        if (url == null)
            AccountController.createWebsite(driver);
        else
            AccountController.websiteAdminLogin(driver, url);

        panelUrl = driver.getCurrentUrl();
        closePopupIfAny();
    }

    static void closePopupIfAny() {
        try {
            new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='fancybox-outer']")
            ));
        }
        catch (Exception ignored) {
            return;
        }

        driver.findElement(By.xpath("//div[contains(@class, 'fancybox-close')]"))
                .click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[@id='fancybox-overlay']")
        ));
    }

    static void ensureAtPage(String url) {
        driver.get(url);
        new WebDriverWait(driver, 4).until(ExpectedConditions.urlMatches(url));
        closePopupIfAny();
    }

    @Test
    void bytesIncreaseTest() throws InterruptedException {
        ensureAtPage(panelUrl);
        WebElement elDiskSpace = driver.findElement(By.xpath("//span[@id='diskUpdater']"));
        String value1 = elDiskSpace.getText();
        Thread.sleep(1200);
        String value2 = elDiskSpace.getText();

        long bytes1 = Long.parseLong(value1);
        long bytes2 = Long.parseLong(value2);
        assertTrue(bytes1 < bytes2);
    }

    @Test
    void activateDeactivateBlogTest() {
        ensureAtPage(panelUrl);
        WebElement inactiveTab = driver.findElement(By.xpath("//div[@id='uninstMods']"));
        if (inactiveTab.getAttribute("class").equals("MnotActive"))
            inactiveTab.click();

        new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Блог']")
        ));

        driver.findElement(By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Блог']"))
                .click();

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Активировать модуль']")
        ));
        driver.findElement(By.xpath("//button[text()='Активировать модуль']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//b[text()='Управление модулем']")
        ));
        driver.findElement(By.xpath("//table//td/a[contains(text(),'Удалить модуль')]"))
                .click();

        driver.findElement(By.xpath("//td[text()='Ответ на вопрос:']/../td/input"))
                .sendKeys(Constants.TEST_ACCOUNT_SECRET_ANSW);
        driver.findElement(By.xpath("//button[text()='Удалить модуль']"))
                .click();

        Alert alert = new WebDriverWait(driver, 3).until(ExpectedConditions.alertIsPresent());
        alert.accept();

        new WebDriverWait(driver, 4).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Блог']")
        ));
    }

    @Test
    void addUserTest() throws AWTException {
        ensureAtPage(panelUrl);

        String username = RandomString.get();
        String fullName = "Vasia " + username;
        String email = username + "@emailnotfound.com";

        driver.findElement(By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Пользователи']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//td/a[text()='Добавить пользователя']")
        ));
        driver.findElement(By.xpath("//table//td/a[text()='Добавить пользователя']"))
                .click();

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//td[contains(text(), 'Имя пользователя')]")
        ));

        driver.findElement(By.xpath("//table//td[contains(text(), 'Имя пользователя')]/../td/input"))
                .sendKeys(username);
        driver.findElement(By.xpath("//table//td[contains(text(), 'Полное имя')]/../td/input"))
                .sendKeys(fullName);
        driver.findElement(By.xpath("//table//td[contains(text(), 'E-mail')]/../td/input"))
                .sendKeys(email);

        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOWN);

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Сохранить']")
        ));

        robot.keyRelease(KeyEvent.VK_DOWN);

        driver.findElement(By.xpath("//button[text()='Сохранить']"))
                .click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[text()='Поиск пользователя']")
        ));

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(String.format("//table//tr/td[2]/a/b[text()='%s']", username))
        ));
    }

    @Test
    void uploadFileTest() throws URISyntaxException {
        goToFileManager();

        String filePath = getTestFilePath();
        FileManager.uploadFile(driver, filePath);

        assertEquals("Cat.bmp",
                driver.findElement(By.xpath("//table//tr[@class='new']/td[1]/a")).getText());
    }

    @Test
    void deleteFileTest() throws URISyntaxException {
        goToFileManager();
        if (driver.findElements(By.xpath("//table//tr/td[1]/a[text()='Cat.bmp']")).size() == 0)
            FileManager.uploadFile(driver, getTestFilePath());

        driver.findElement(By.xpath(
                "//table//tr/td[1]/a[text()='Cat.bmp']/../../td[4]/i[contains(@class,'fa-close')]"))
                .click();

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Удалить...']")
        ));

        driver.findElement(By.xpath("//button[text()='Удалить...']"))
                .click();

        Alert alert = new WebDriverWait(driver, 4).until(ExpectedConditions.alertIsPresent());
        alert.accept();

        new WebDriverWait(driver, 4).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//table//tr[@class='new']/td[1]/a[text()='Cat.bmp']")
        ));
    }

    @Test
    void fileRenameTest() throws URISyntaxException {
        goToFileManager();
        if (driver.findElements(By.xpath("//table//tr/td[1]/a[text()='Cat.bmp']")).size() == 0)
            FileManager.uploadFile(driver, getTestFilePath());

        WebElement renameBtn = driver.findElement(By.xpath(
                "//table//tr[@class='new']/td[1]/a[text()='Cat.bmp']/../../td[4]/i[contains(@class,'ufm-rename')]"));

        renameBtn.click();
        WebElement input = renameBtn.findElement(By.xpath("./../../td[1]/input"));
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOf(input));

        String newName = RandomString.get();
        input.sendKeys(newName);
        input.sendKeys(Keys.RETURN);

        new WebDriverWait(driver, 3).until(ExpectedConditions.invisibilityOf(input));

        assertEquals(1,
                driver.findElements(By.xpath(
                        String.format("//table//tr/td[1]/a[text()='%s']", newName))).size()
        );
    }

    @Test
    void createFolderTest() {
        goToFileManager();
        WebElement createButton = driver.findElement(By.xpath("//button[text()='Создать папку']"));
        createButton.click();

        WebElement input = createButton.findElement(By.xpath("./../div[@class='ufm']/input"));
        String newName = RandomString.get();
        input.sendKeys(newName);
        input.sendKeys(Keys.RETURN);

        new WebDriverWait(driver, 4).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath(String.format("//table//tr[@class='new']/td[1]/a[text()='%s']", newName))
        ));
    }

    @Test
    void cancelFolderCreateTest() {
        goToFileManager();
        WebElement createButton = driver.findElement(By.xpath("//button[text()='Создать папку']"));
        createButton.click();

        WebElement input = createButton.findElement(By.xpath("./../div[@class='ufm']/input"));
        input.sendKeys(Keys.ESCAPE);
    }

    @Test
    void helperPopupsTest() {
        ensureAtPage(panelUrl);

        driver.findElement(By.xpath("//div[@class='panelTop1']/span/a[text()='FTP детали']"))
                .click();
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id,'_uwndTop')]")
        ));

        driver.findElement(By.xpath("//div[@class='panelTop1']/span/a[text()='Админ панель']"))
                .click();
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id,'_uwndTop')]")
        ));

        driver.findElement(By.xpath("//div[@class='panelTop1']/span/a[text()='Браузер']"))
                .click();
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id,'_uwndTop')]")
        ));
    }

    @Test
    void advertExistsTest() {
        ensureAtPage(panelUrl);
        WebElement banner = driver.findElement(
                By.xpath("//table[@class='panel-wrapper']//td//div[@class='cp-banner']/a/img"));
        assertTrue(banner.isDisplayed());

        banner.click();
        WindowManagement.switchToOtherWindow(driver);

        new WebDriverWait(driver, 5).until(ExpectedConditions.urlMatches(
                String.format("^((?!%s).)*$", panelUrl)));

        WindowManagement.closeCurrentWindow(driver);
    }

    @Test
    void advertGoToProTest() {
        ensureAtPage(panelUrl);
        driver.findElement(
                By.xpath("//table[@class='panel-wrapper']//td//div[@class='cp-banner']/..//a//b[text()='Убрать рекламный баннер']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[text()='Пакеты платных услуг']")
        ));
    }

    @Test
    void regRulesTest() {
        ensureAtPage(panelUrl);

        driver.findElement(By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Пользователи']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//td[@class='servMenuList']/a[text()='Правила регистрации пользователей']")
        ));
        driver.findElement(By.xpath("//table//td[@class='servMenuList']/a[text()='Правила регистрации пользователей']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//textarea[@id='rules']")
        ));

        String rules1 = "Не взламывайте сайт и не обижайте никого.";
        String rules2 = "Примите лабу плз :(";

        WebElement textarea = driver.findElement(By.xpath("//textarea[@id='rules']"));
        if (textarea.getText().equals(rules1))
            textarea.sendKeys(rules2);
        else
            textarea.sendKeys(rules1);

        driver.findElement(By.xpath("//button[text()='Сохранить']")).click();

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='myWinSuccess' and contains(text(), 'Шаблон успешно')]")
        ));
    }

    @Test
    void languageDropdownTest() {
        ensureAtPage(panelUrl);
        driver.findElement(By.xpath("//div[contains(@class,'u-menuhitem')]//div[text()='Язык']")).click();

        new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='u-menu']//div[text()='Русский']/..")
        ));
        WebElement languageDropdown = driver.findElement(By.xpath("//div[@class='u-menu']//div[text()='Русский']/.."));
        assertTrue(languageDropdown.findElement(By.xpath("./div[text()='Polski']")).isDisplayed());
        assertTrue(languageDropdown.findElement(By.xpath("./div[text()='English']")).isDisplayed());
        assertTrue(languageDropdown.findElement(By.xpath("./div[text()='Deutsch']")).isDisplayed());
    }

    String getTestFilePath() throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("Cat.bmp");
        File file = Paths.get(res.toURI()).toFile();
        return file.getAbsolutePath();
    }

    void goToFileManager() {
        ensureAtPage(panelUrl);
        driver.findElement(By.xpath("//div[contains(@class, 'MmenuOut')]//a[text()='Редактор страниц']"))
                .click();

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@class='servmenu' and text()='Файловый менеджер']")
        ));
        driver.findElement(By.xpath("//a[@class='servmenu' and text()='Файловый менеджер']")).click();

        new WebDriverWait(driver, 4).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table[contains(@class, 'ufm-table')]")
        ));
    }


    @AfterAll
    static void tearDown() {
        driver.quit();
    }
}
