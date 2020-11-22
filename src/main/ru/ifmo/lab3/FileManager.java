package ru.ifmo.lab3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FileManager {
    public static void uploadFile(WebDriver driver, String filePath) {
        driver.findElement(By.xpath("//input[@class='file-field' and @type='file']"))
                .sendKeys(filePath);

        driver.findElement(By.xpath("//button[text()='Загрузить файл']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[@class='new']")
        ));
    }
}
