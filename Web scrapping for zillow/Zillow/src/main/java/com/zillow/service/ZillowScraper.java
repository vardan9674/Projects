package com.zillow.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;


public class ZillowScraper {
	
	String filePath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;
	Scanner sc = new Scanner(System.in);
	WebDriver driver;
	BufferedWriter bufferedWriter;

	public ZillowScraper() {
		init();
	}

	public void init() {
		WebDriverManager.firefoxdriver().setup();
		driver = new FirefoxDriver();
	}

	
	public static void main(String[] args) throws IOException {
		ZillowScraper obj = new ZillowScraper();
		System.out.println("Please enter city name:");
		// User Input
		String input = obj.sc.nextLine().trim();
		obj.startprocess(input);
		obj.bufferedWriter.close();
	}

	
	private void startprocess(String input) {

		try {

			String header = "ZPID;Type;ZipCode;Locality;Address;Latitude;Longitude;Status;Description;CardBadge;RoomType;Price;AreaSpace;SourceLink";

			for (int i = 1; i <= 2; i++) {
				if (i == 1) {
					bufferedWriter = new BufferedWriter(new FileWriter(filePath + "Rent_" + input + ".csv"));
					bufferedWriter.write(header + "\n");
					startProcessForRent(input);
					bufferedWriter.close();
				} else if (i == 2) {
					bufferedWriter = new BufferedWriter(new FileWriter(filePath + "Sell_" + input + ".csv"));
					bufferedWriter.write(header + "\n");
					startProcessForSell(input);
					bufferedWriter.close();
				}
			}
		} catch (IOException e) {
			System.out.println("Start Process: " + e.getMessage());
		}

	}

	
	private void startProcessForSell(String input) {

		Utility.openPage("https://www.zillow.com/sell/", driver);
		putAreaOnsearch(input);
		searchArea(input);

	}

	
	private void startProcessForRent(String input) {
		Utility.openPage("https://www.zillow.com/rent/", driver);
		putAreaOnsearch(input);
		searchArea(input);

	}

	
	private void putAreaOnsearch(String input) {
		driver.findElement(By.cssSelector("input.react-autosuggest__input")).click();
		driver.findElement(By.cssSelector("input.react-autosuggest__input")).sendKeys(input.trim());
		driver.findElement(By.cssSelector("span.searchBtnText")).click();
		Utility.waitForSimpleJavaScript(driver);

	}

	
	private void searchArea(String input) {
		boolean isTrue = true;
		int i = 1;
		Map<String, String> listOfJSONObject = new HashMap<String, String>();
		try {
			String url = driver.getCurrentUrl();
			while (isTrue) {
				List<WebElement> listWeb = driver.findElements(By.cssSelector("div#search-results>ul>li>article"));
				for (WebElement webElement : listWeb) {
					Utility.waitForSimpleJavaScript(driver);
					listWeb = driver.findElements(By.cssSelector("div#search-results>ul>li>article"));
					if (webElement.findElements(By.tagName("a")).size() > 0) {
						String zpId = webElement.getAttribute("data-zpid");
						String href = webElement.findElement(By.tagName("a")).getAttribute("href");
						System.out.println(href);
						listOfJSONObject.put(zpId, href);
					}
				}
				i++;
				Utility.openPage(url + i + "_p", driver);
				Utility.waitForSimpleJavaScript(driver);
				Utility.waitForPageLoaded();
				Utility.waitForAjax(driver);
				if (i == 20) {
					isTrue = false;
				}
			}
			startScraperForUrls(listOfJSONObject, input);
		} catch (Exception e) {
			System.out.println("Error in searchArea : " + e.getMessage());
		}

	}

	
	private void startScraperForUrls(Map<String, String> listOfJSONObject, String input) {
		try {
			for (String zpId : listOfJSONObject.keySet()) {
				String url = listOfJSONObject.get(zpId);
				Utility.openPage(url, driver);
				Utility.waitForSimpleJavaScript(driver);
				Utility.waitForPageLoaded();
				Utility.waitForAjax(driver);
				if (driver.findElements(By.id("hdpApolloPreloadedData")).size() > 0) {
					WebElement popupBody = driver.findElement(By.id("hdpApolloPreloadedData"));
					String json = popupBody.getAttribute("innerHTML");
					System.out.println(json);
					dataWriteForJson(json, input, url, zpId);
				} else {
					dataWriteForHtml(input, url, zpId);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	
	private void dataWriteForHtml(String input, String url, String zpId) {
		try {
			String locality = input;
			String status = "NA";
			String photoCount = "NA";
			String address = "NA";
			String zipCode = "NA";
			String type = "NA";
			String description = "NA";
			String latitude = "NA";
			String longitude = "NA";
			String latLog = "NA";
			String currentUrl = driver.getCurrentUrl();
			latLog = latAndlog(currentUrl);
			if (!latLog.equals("NA")) {
				latitude = latLog.split(",")[0];
				longitude = latLog.split(",")[1];
			}

			if (driver.findElements(By.cssSelector("div.bdp-title>h1")).size() > 0) {
				status = driver.findElement(By.cssSelector("div.bdp-title>h1")).getAttribute("innerHTML");
			}
			if (driver.findElements(By.cssSelector("div.bdp-title>h2")).size() > 0) {
				address = driver.findElement(By.cssSelector("div.bdp-title>h2")).getAttribute("innerHTML")
						.split("<br>")[0].replaceAll("\\<[^>]*>", "").trim();

				zipCode = address.split(" ")[address.split(" ").length - 1];

			}
			if (driver.findElements(By.cssSelector("div.bdp-photo-carousel>div")).size() > 0) {
				List<WebElement> elements = driver.findElements(By.cssSelector("div.bdp-photo-carousel>div"));
				photoCount = String.valueOf(elements.size());

			}

			if (driver.findElements(By.cssSelector("div#bdp-description>p")).size() > 0) {
				description = driver.findElement(By.cssSelector("div#bdp-description>p")).getAttribute("innerHTML")
						.replaceAll("\\<[^>]*>", "").trim();
			}

			if (driver.findElements(By.cssSelector("span.bdp-unit-table-tab-panel-text")).size() > 0) {
				type = driver.findElements(By.cssSelector("span.bdp-unit-table-tab-panel-text")).get(0)
						.getAttribute("innerHTML");
			}

			if (driver.findElements(By.cssSelector(
					"div.bdp-unit-table-accordion>div>section.bdp-unit-table-accordion-section.bdp-unit-table-accordion-section_closed"))
					.size() > 0) {
				List<WebElement> element = driver.findElements(By.cssSelector(
						"div.bdp-unit-table-accordion>div>section.bdp-unit-table-accordion-section.bdp-unit-table-accordion-section_closed"));
				for (WebElement webElement : element) {
					String roomType = webElement.findElement(By.cssSelector("button>strong")).getAttribute("innerHTML");
					if (webElement.findElements(By.cssSelector(
							"div.bdp-unit-table-accordion-group-content>div.bdp-unit-table-photo-card.bdp-unit-table-floorplan-card"))
							.size() > 0) {
						List<WebElement> elementlist = webElement.findElements(By.cssSelector(
								"div.bdp-unit-table-accordion-group-content>div.bdp-unit-table-photo-card.bdp-unit-table-floorplan-card"));
						for (WebElement priceElement : elementlist) {
							String price = "NA";
							String sqft = "NA";
							if (priceElement.findElements(By.cssSelector(
									"div.floorplan-units-section>ul.floorplan-units>li.floorplan-unit-info.text-overflow>span.floorplan-unit-price"))
									.size() > 0) {
								price = priceElement.findElement(By.cssSelector(
										"div.floorplan-units-section>ul.floorplan-units>li.floorplan-unit-info.text-overflow>span.floorplan-unit-price"))
										.getAttribute("innerHTML");
							} else if (priceElement
									.findElements(By.cssSelector("div.floorplan-header-section>div>p.floorplan-price"))
									.size() > 0) {
								price = priceElement
										.findElement(
												By.cssSelector("div.floorplan-header-section>div>p.floorplan-price"))
										.getAttribute("innerHTML");

							}
							if (priceElement.findElements(By.cssSelector("div.floorplan-header-section>div>p.hide-sm"))
									.size() > 0) {
								String sqftstring = priceElement
										.findElement(By.cssSelector("div.floorplan-header-section>div>p.hide-sm"))
										.getAttribute("innerHTML");
								sqft = sqftstring.split(" ")[sqftstring.split(" ").length - 2];
							}

							String finalString = zpId + ";" + type + ";" + zipCode + ";" + locality + ";" + address
									+ ";" + latitude + ";" + longitude + ";" + status + ";" + description + ";"
									+ photoCount + ";" + roomType + ";" + price.split(" ")[0] + ";" + sqft + ";" + url;

							System.out.println(finalString + "\n");

							bufferedWriter.write(finalString + "\n");

						}
					} else if (driver.findElement(By.cssSelector("div.zsg-tab-panel.zsg-tab_active"))
							.findElements(By.className("bdp-unit-table-simple-card")).size() > 0) {
						List<WebElement> listOfData = driver
								.findElement(By.cssSelector("div.zsg-tab-panel.zsg-tab_active"))
								.findElements(By.className("bdp-unit-table-simple-card"));
						dataWriteForDivHtmlList(listOfData, locality, status, address, zipCode, photoCount, description,
								type, url, zpId);
					}

				}

			}

		} catch (Exception e) {
			System.out.println("Error in rentDataWriteForHtml : " + e.getMessage());
		}

	}


	private void dataWriteForDivHtmlList(List<WebElement> listOfData, String locality, String status, String address,
			String zipCode, String photoCount, String description, String type, String url, String zpId) {
		try {
			String currentUrl = driver.getCurrentUrl();
			String latitude = "NA";
			String longitude = "NA";
			String latLog = "NA";
			latLog = latAndlog(currentUrl);
			if (!latLog.equals("NA")) {
				latitude = latLog.split(",")[0];
				longitude = latLog.split(",")[1];

			}
			for (WebElement webElement : listOfData) {
				String price = "NA";
				String sqft = "NA";
				String roomType = "NA";

				if (webElement.findElements(By.cssSelector("a.text-overflow.listing-price")).size() > 0) {
					price = webElement.findElement(By.cssSelector("a.text-overflow.listing-price"))
							.getAttribute("innerHTML");
				}
				if (webElement.findElements(By.className("sqft-info")).size() > 0) {
					sqft = webElement.findElement(By.className("sqft-info")).getAttribute("innerHTML");
				}
				if (webElement.findElements(By.className("listing-type")).size() > 0) {
					status = webElement.findElement(By.className("listing-type")).getAttribute("innerHTML");
				}
				if (webElement.findElements(By.className("baths-info")).size() > 0) {
					roomType = webElement.findElement(By.className("baths-info")).getAttribute("innerHTML");
				}
				String finalString = zpId + ";" + type + ";" + zipCode + ";" + locality + ";" + address + ";" + latitude
						+ ";" + longitude + ";" + status + ";" + description + ";" + photoCount + ";" + roomType + ";"
						+ price.split(" ")[0] + ";" + sqft + ";" + url;

				System.out.println(finalString + "\n");

				bufferedWriter.write(finalString + "\n");
			}
		} catch (Exception e) {

		}

	}

	
	private String latAndlog(String currentUrl) {
		if (currentUrl.contains("_ll/")) {
			String splitstr = currentUrl.split("_ll/")[0];
			String latLongString = splitstr.substring(splitstr.lastIndexOf("/"), splitstr.length()).replace("/", "");
			return latLongString;
		}
		return "NA";
	}

	
	private void dataWriteForJson(String json, String input, String url, String zpId) {
		try {
			String locality = input;
			ObjectMapper map = new ObjectMapper();
			JsonNode jsonNode = map.readTree(json);
			for (JsonNode jsonNodeTree : jsonNode) {
				String status = "NA";
				String photoCount = "NA";
				String currency = "NA";
				String address = "NA";
				String zipCode = "NA";
				String type = "NA";
				String description = "NA";
				String latitude = "NA";
				String longitude = "NA";
				String price = "NA";
				String sqft = "NA";
				String roomType = "NA";
				JsonNode node = jsonNodeTree.path("property");
				address = node.path("streetAddress").asText() + " " + node.path("city") + " " + node.path("state");
				zipCode = node.path("zipcode").asText();
				type = node.path("homeStatus").asText();
				status = node.path("homeType").asText();
				currency = node.path("currency").asText();
				price = node.path("price").asText();
				latitude = node.path("latitude").asText();
				longitude = node.path("longitude").asText();
				photoCount = node.path("photoCount").asText();
				description = node.path("description").asText().replace("\n", "");
				sqft = node.path("livingArea").asText().trim();
				roomType = node.path("bedrooms").asText().trim();
				String finalString = zpId + ";" + type + ";" + zipCode + ";" + locality + ";" + address + ";" + latitude
						+ ";" + longitude + ";" + status + ";" + description + ";" + photoCount + ";" + roomType + ";"
						+ currency + ":" + price + ";" + sqft + ";" + url;

				System.out.println(finalString + "\n");

				bufferedWriter.write(finalString + "\n");
				break;

			}
		} catch (Exception e) {
			System.out.println("Error In dataWriteForJson :" + e.getMessage());
		}
	}
}
