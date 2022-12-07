package org.cgi;

import org.cgi.models.Address;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class RoastMarketCommerceModule {
    static String SCRAPING_BEE_API_KEY = "J7LHLTQGWBRU6BL0VEMC0PTX92LCKYXUL850AP1NSJF0ERYG4ZCR2MXWSAQWBDDUJNCE4PP4EFI3OCUP";
    static String RM_BASE = "https://www.roastmarket.de";
    static String CART_ID = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjYXJ0SWQiOiI3Mzc0NzE0In0.hAypVfNkD-OxcNKpt1mB_GrzxhUpveYNTIQasrub93A";
    static String SKU = "117-1-012-10";

    static void checkStock() {
        String url = String.format("%s/api/stock/check?sku=%s", RM_BASE, SKU);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    static void seleniumDriverTest() {
        WebDriver driver = new EdgeDriver();
        driver.get("https://selenium.dev");
        driver.quit();
    }

    static void mainPageDriver(String productName, HashMap<String, String> billingInfos,
            HashMap<String, String> creditInfos)
            throws InterruptedException, ParseException {
        String cartId = "";
        String url = String.format("https://www.roastmarket.de/%s.html", productName);
        WebDriver driver = new ChromeDriver();

        // Open product page
        driver.get(url);
        driver.manage().window().maximize();
        Thread.sleep(1000);

        // Add to cart
        WebElement addCartBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"viewport\"]/div[4]/div/section[1]/div/section/div[2]/div[3]/button[1]")));
        addCartBtn.click();
        Thread.sleep(1000);

        // Save cart ID
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        cartId = (String) jse.executeScript("localStorage.getItem", "'shop/cart/current-cart-token'");
        System.out.println(cartId);

        // Cookie consent
        driver.get("https://www.roastmarket.de/checkout/login");
        Cookie consentBox = new Cookie("OptanonAlertBoxClosed", "2022-09-09T14:10:17.318Z", ".roastmarket.de", "/",
                Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        driver.manage().addCookie(consentBox);

        // Checkout
        Thread.sleep(1000);
        WebElement checkoutBtn = driver
                .findElement(By.xpath("//*[@id=\"viewport\"]/div[4]/div/section/div/div[1]/form/button"));
        checkoutBtn.click();

        // Fill billing form
        driver.findElement(By.xpath("//*[@id=\"billing-new-address-form\"]/div/ul/li[2]/div/div[2]/div/label[2]"))
                .click();
        driver.findElement(By.id("billing:firstname")).sendKeys(billingInfos.get("firstname"));
        driver.findElement(By.id("billing:lastname")).sendKeys(billingInfos.get("lastname"));
        driver.findElement(By.id("billing:street1")).sendKeys(billingInfos.get("street1"));
        driver.findElement(By.id("billing:postcode")).sendKeys(billingInfos.get("postcode"));
        driver.findElement(By.id("billing:city")).sendKeys(billingInfos.get("city"));
        driver.findElement(By.id("billing:email")).sendKeys(billingInfos.get("email"));

        // Confirm billing infos
        WebElement billingBtn = driver.findElement(By.xpath("//*[@id=\"billing-buttons-container\"]/button"));
        billingBtn.click();

        // Select credit field
        Thread.sleep(4000);
        WebElement creditBtn = driver
                .findElement(By.xpath("//*[@id=\"dt_method_gene_braintree_creditcard\"]/label/div/div"));
        creditBtn.click();
        Thread.sleep(4000);

        // Enter credit card infos
        enterCreditInfoField(driver, "number", creditInfos, "credit-card-number");
        enterCreditInfoField(driver, "expirationMonth", creditInfos, "expiration-month");
        enterCreditInfoField(driver, "expirationYear", creditInfos, "expiration-year");
        enterCreditInfoField(driver, "cvv", creditInfos, "cvv");

        // Confirm payment
        WebElement paymentBtn = driver.findElement(By.xpath("//*[@id=\"payment-buttons-container\"]/button"));
        paymentBtn.click();

        // Final confirm
        Thread.sleep(4000);
        WebElement finaliseBtn = driver.findElement(By.xpath("//*[@id=\"review-buttons-container\"]/button"));
        finaliseBtn.click();

        // driver.close();
    }

    private static void enterCreditInfoField(WebDriver d, String frameName, HashMap<String, String> c, String field) {
        d.switchTo().frame("braintree-hosted-field-" + frameName);
        d.findElement(By.id(field)).sendKeys(c.get(field));
        d.switchTo().defaultContent();
    }

    public static void mockPurchaseFromShop() throws InterruptedException, ParseException {
        String productName = "arabica-espresso-probierpaket-1-5kg";

        Address a = IoPResource.address3;

        HashMap<String, String> billingInfos = new HashMap<>();
        billingInfos.put("firstname", a.getFirstName());
        billingInfos.put("lastname", a.getLastName());
        billingInfos.put("street1", a.getStreetName() + " " + a.getStreetNo());
        billingInfos.put("postcode", Integer.toString(a.getPostCode()));
        billingInfos.put("city", a.getCity());
        billingInfos.put("email", "aretha@franklin.de");

        HashMap<String, String> creditInfos = new HashMap<>();
        creditInfos.put("credit-card-number", "4532707811670255");
        creditInfos.put("expiration-month", "08");
        creditInfos.put("expiration-year", "28");
        creditInfos.put("cvv", "313");

        mainPageDriver(productName, billingInfos, creditInfos);
    }

    public static void main(String[] args) throws InterruptedException, ParseException {

    }
}
