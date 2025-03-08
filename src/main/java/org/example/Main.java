package org.example;

import com.microsoft.playwright.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.example.helper.java;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaywrightTest {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightTest.class);

    @DataProvider(name = "playwrightDataProvider")
    public Object[][] searchData() {
        return new Object[][] {
                {"validateHeading"},
                {"transactions"}
        };
    }

    @Test(dataProvider = "playwrightDataProvider")
    public void testSearch(String testCaseId) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://blockstream.info/block/000000000000000000076c036ff5119e5a5a74df77abf64203473364509f7732");

            if (testCaseId.equals("validateHeading")) {
                try {
                    Locator transaction = page.locator("//*[@id=\"explorer\"]/div/div/div[2]/div[2]/div[3]/h3");
                    Assert.assertEquals(transaction.innerText(), "25 of 2875 Transactions", "The Heading is incorrect");
                } catch (Exception e) {
                    log.error("Error validating heading: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (testCaseId.equals("transactions")) {
                try {
                    Locator transactions = page.locator("//*[@id=\"explorer\"]/div/div/div[2]/div[2]/div[3]");
                    int divCount = transactions.count();
                    for (int i = 0; i < divCount; i++) {
                        try {
                            Locator transaction = transactions.nth(i);
                            // Fetching each input transaction Div
                            Locator inputTransactionCount = transaction.locator("//*[@id=\"transaction-box\"]/div[2]/div[1]");
                            int inputCount = inputTransactionCount.count();
                            // Fetching each output transaction Div
                            Locator outputTransactionCount = transaction.locator("//*[@id=\"transaction-box\"]/div[2]/div[3]");
                            int outputCount = outputTransactionCount.count();

                            if (inputCount == 1 && outputCount == 2) {
                                Locator transactionHash = transaction.locator("//*[@id=\"transaction-box\"]/div[1]/div[1]/a");
                                String transactionHashText = transactionHash.innerText();
                                System.out.println("The Transaction hash as per condition is : " + transactionHashText);
                            }
                        } catch (Exception e) {
                            log.error("Error processing transaction div " + (i + 1) + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    log.error("Error locating transactions: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while initiating Playwright: " + e.getMessage());
            e.printStackTrace();
        }
    }
}