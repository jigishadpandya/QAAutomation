import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;


public class BaseDriver {

    protected WebDriver driver;
    protected String driverpath;
    protected int defaultTimeout = 20;

    public BaseDriver() {
        beforeClass();
        getWebDriver();
    }

    public void beforeClass() {
        killBrowser();
        driverpath = getCurrentDirectory() + File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "drivers";
        startChrome();//similar code can be written for firefox and IE
    }


    public void navigate(String url) {
        //System.out.println("Navigating to" + url);
        driver.get(url);
    }

    public boolean waitForPageFullyLoaded(int maxtimeout) {
        try {
            Wait<WebDriver> wait = new WebDriverWait(driver, maxtimeout);
            wait.until(driver1 -> String
                    .valueOf(((JavascriptExecutor) driver1).executeScript("return document.readyState"))
                    .equals("complete"));
            return true;
        } catch (Exception e) {
            System.out.println("Not able to load page" + e);
            return false;
        }
    }


    protected void startChrome() {

        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> chromePrefs = new HashMap<>();
        String chromedriverPath = "";
        try {
            chromedriverPath = driverpath + File.separator + "chromedriver.exe";
            chromePrefs.put("profile.default_content_settings.popups", false);
            chromePrefs.put("useAutomationExtension", false);
            chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.default_directory", System.getProperty("java.io.tmpdir"));
            chromeOptions.setExperimentalOption("prefs", chromePrefs);
            chromeOptions.addArguments("disable-popup-blocking");
            chromeOptions.addArguments("disable-extensions");
            chromeOptions.addArguments("start-maximized");

            chromeOptions.addArguments("window-size=1920,1080");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("proxy-server='direct://'");
            chromeOptions.addArguments("proxy-bypass-list=*");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--remote-debugging-port=9222");
            //chromeOptions.setHeadless(true);//headless can be set to true here for headless browser testing
            if (null == System.getProperty("webdriver.chrome.driver")) {
                System.setProperty("webdriver.chrome.driver", chromedriverPath);
            }
            driver = new ChromeDriver(chromeOptions);
            driver.manage().deleteAllCookies();
            driver.manage().window().maximize();

        } catch (Exception e) {
            System.out.println("ERROR occurred in StartChrome:" + e);
        }
    }

    public boolean typeKeys(String locator, String value) {
        try {
            waitForElement(locator, defaultTimeout);
            typeKeys(getElement(locator), value);
            } catch (Exception e) {
            System.out.println("Exception in typeKeys " + e);
            return false;
        }
        return true;
    }

    protected WebElement typeKeys(final WebElement we, final String value) {
        return typeKeys(we, value, true, false);
    }


    protected WebElement typeKeys(final WebElement we, final String value, final boolean clear,
                                  final boolean keyClear) {
        if (clear)
            we.clear();
        if (keyClear) {
            we.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            we.sendKeys(Keys.chord(Keys.DELETE));
        }
        we.sendKeys(value);
        return we;
    }

    public WebDriver getWebDriver() {
         return driver;
    }

    protected static By byLocator(final String locator) {
        String prefix = locator.substring(0, locator.indexOf('='));
        String suffix = locator.substring(locator.indexOf('=') + 1);

        switch (prefix) {
            case "xpath":
                return By.xpath(suffix);
            case "css":
                return By.cssSelector(suffix);
            case "link":
                return By.linkText(suffix);
            case "partLink":
                return By.partialLinkText(suffix);
            case "id":
                return By.id(suffix);
            case "name":
                return By.name(suffix);
            case "tag":
                return By.tagName(suffix);
            case "class":
                return By.className(suffix);
            default:
                System.out.println("Error locating element " + locator);
                Assert.fail("Invalid locator identifier: " + locator);
                return null;
        }
    }

    public WebElement getElement(final String locator) throws Exception {
        return getElement(locator, true);
    }

    protected WebElement getElement(final String locator, boolean screenShotOnFail) throws Exception {
        try {
            return getWebDriver().findElement(byLocator(locator));
        } catch (Exception e) {
            throw e;
        }
    }


    protected WebElement waitForElement(final String locator, int timeOut, boolean notVisible) throws Exception {

        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOut);
        if (notVisible)
            wait.until(ExpectedConditions.presenceOfElementLocated(byLocator(locator)));
        else
            wait.until(ExpectedConditions.visibilityOfElementLocated(byLocator(locator)));

        return getElement(locator);
    }


    public void setPermission(File file) throws IOException {
        Set<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_EXECUTE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE);

        Files.setPosixFilePermissions(file.toPath(), perms);
    }


    public boolean click(String locator) {
        try {
            waitForElement(locator, defaultTimeout);
            click(getElement(locator));
        } catch (Exception e) {
            System.out.println("Exception " + e);
            return false;
        }
        return true;
    }

    protected WebElement waitForElement(final String locator, int timeOut) throws Exception {
        return waitForElement(locator, timeOut, false);
    }


    protected void click(final WebElement we) {
        we.click();
    }

    public static String getCurrentDirectory() {
        String dirStr = null;
        try {
            File curDir = new File(".");
            dirStr = curDir.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Error trying to get current directory." + e);
        }
        return dirStr;
    }

    protected void killBrowser() {
        try {
            ProcessBuilder procBuilder = new ProcessBuilder("cmd.exe",
                    "/C taskkill /F /IM chrome.exe /T & taskkill /F /IM chromedriver.exe /T & taskkill /F /IM firefox.exe /T & taskkill /F /IM geckdriver.exe /T & taskkill /F /IM WerFault.exe /T");
            Process proc = procBuilder.start();
            proc.waitFor();
            proc.destroyForcibly();

        } catch (Exception e) {
            System.out.println("Error killing windows processes " + e);
        }
    }


}
