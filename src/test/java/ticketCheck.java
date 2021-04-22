import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Properties;

public class ticketCheck {

    WebDriver driver;
    String url = "https://tickets.formula1.com/en/f1-42837-netherlands";

    @Test
    public void f1Ticket() throws MessagingException {

        // Driver configuration

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        // Open the page

        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        // Accept the cookie

        By cookie = By.xpath("//button[@id='onetrust-accept-btn-handler']");
        wait.until(ExpectedConditions.elementToBeClickable(cookie)).click();

        // Price check

        By ticketPrice = By.xpath("//div[@class='day-caption' and text()='Sunday']//..//div[@class='price-caption']");
        By circuit = By.xpath("//h2[text()='Circuit Zandvoort']");
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(circuit));
        actions.perform();

        // Send e-mail according to result

        if (driver.findElement(ticketPrice).getText().contains("Not available")) {
            System.out.println("Bilet yok, köyüne dön.");
        } else {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS
            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(mail.username, mail.password);
                        }
                    });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail.username));
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(mail.username)
            );
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mail.to)
            );
            message.setSubject("F1 Dutch GP Ticket");
            message.setText("Bilet var, siteye kos : " + url);
            Transport.send(message);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
    }
}
