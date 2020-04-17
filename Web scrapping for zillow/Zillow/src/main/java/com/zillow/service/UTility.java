package com.zillow.service;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utility {

	public static void openPage(String url, WebDriver driver) {

		try {

			driver.manage().timeouts().pageLoadTimeout(500, TimeUnit.SECONDS);
			driver.get(url);

			waitForPageLoaded();

		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("return window.stop");
		}

	}

	public static void waitForSimpleJavaScript(WebDriver driver) {

		try {
			new WebDriverWait(driver, 1000).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					return (Boolean) js.executeScript("return document.readyState").equals("complete");
				}
			});

		} catch (Exception e) {

		}
	}

	public static void executeJavaScript(WebDriver driver, final String query) {
		try {
			new WebDriverWait(driver, 1000).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					return (Boolean) js.executeScript(query);
				}
			});
		} catch (Exception e) {
		}
	}

	public static void waitForAjax(WebDriver driver) {

		try {
			new WebDriverWait(driver, 100).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					return (Boolean) js.executeScript("return jQuery.active == 0");
				}
			});

		} catch (Exception e) {

		}
	}

	public static void waitForPageLoaded() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
