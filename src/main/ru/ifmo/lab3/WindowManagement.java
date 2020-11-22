package ru.ifmo.lab3;

import org.openqa.selenium.WebDriver;

public class WindowManagement {
    public static void switchToOtherWindow(WebDriver driver) {
        String oldHandle = driver.getWindowHandle();
        Object[] handles = driver.getWindowHandles().toArray();
        String curHandle = oldHandle.equals(handles[0]) ? (String)handles[1] : (String)handles[0];
        driver.switchTo().window(curHandle);
    }

    public static void closeCurrentWindow(WebDriver driver) {
        String oldHandle = driver.getWindowHandle();
        Object[] handles = driver.getWindowHandles().toArray();
        String curHandle = oldHandle.equals(handles[0]) ? (String)handles[1] : (String)handles[0];
        driver.close();
        driver.switchTo().window(curHandle);
    }
}
