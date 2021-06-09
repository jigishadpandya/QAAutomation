import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/********* Scenarios ********************
 * 1. User should book ticket by providing all values - automated with Paris Dept and all other as destination
 * 2.Departure and destination cannot be same
 * Second page Validation
 * 3. Name,Address,CardNumber,Nameoncard etc. all field should be mandatory
 * 4. User should be able to click on any of the Choose This Flight button
 * 5. If remember me is checked(selected) - next time User details should get auto-populated.
 * Third page Validation
 * 6. Your flight from TLV to SFO has been reserved.-- this on third page should match with Departure and destination cities.
 * 7. On third page - flight number, price etc. should match with user selection on second page.
 * 8. Total cost should match with - price + Arbitrary Fees and Taxes
 * 9. Card number/prefix validation with selected card type
 * 10. Standard credit/debit card validation with checksum
 * Last page validation
 * 11. Confirmation ID provided by system and is not blank - AUTOMATED
 * 12. Amount should match with total price
 * 13. Card number should be hidden with only last four digits displayed.
 * 14. Date provided should be Booking date
 */

public class BookFlight extends BaseDriver {
    private int testingtimeout = 200;
    private List<WebElement> depCity = new ArrayList<>();
    private List<WebElement> destCity = new ArrayList<>();
    int depCityLength = 0;
    int destCityLength = 0;
    private BaseDriver browser;
    private int btnCount =1;



    @BeforeClass(alwaysRun = true)
    @Override
    public void beforeClass() {
        browser = new BaseDriver();
        browser.navigate("http://blazedemo.com/");
        browser.waitForPageFullyLoaded(testingtimeout);
        //finding total departure and destination city
        depCity=browser.getWebDriver().findElements(By.xpath("//*[@name='fromPort']//option"));
        destCity=browser.getWebDriver().findElements(By.xpath("//*[@name='toPort']//option"));
        depCityLength = depCity.size();
        destCityLength = destCity.size();
    }

    /*
        This tc takes String vlues departure and destination city from data provider
        As of now Departure city selected to  Paris with all Destination city
     */

    @Test(description = "Author : Jigisha Pandya" + "Book a flight from source to destination", dataProvider = "city")
    public void bookFlight(String depCity, String destCity) {
        browser.navigate("http://blazedemo.com/");
        browser.waitForPageFullyLoaded(testingtimeout);
        browser.click("xpath=//*[@name='fromPort']");
        browser.click("xpath=//*[text()='"+depCity+"']");
        browser.click("xpath=//*[@name='toPort']");
        browser.click("xpath=//*[text()='"+destCity+"']");
        browser.click("xpath=//*[@value='Find Flights']");
        browser.waitForPageFullyLoaded(testingtimeout);
        List<WebElement>chooseFlightbtn=browser.getWebDriver().findElements(By.xpath("//*[@value='Choose This Flight']"));
        WebElement chooseFlight=browser.getWebDriver().findElement(By.xpath("(//*[@value='Choose This Flight'])["+btnCount+"]"));
        if (btnCount < chooseFlightbtn.size()) {
            btnCount++;
        } else {
            btnCount = 1;
        }
        chooseFlight.click();
        browser.waitForPageFullyLoaded(testingtimeout);
        browser.typeKeys("xpath=//*[@id='inputName']","FirstName");
        browser.typeKeys("xpath=//*[@id='address']","Sample Address");
        browser.typeKeys("xpath=//*[@id='city']","Sample City");
        browser.typeKeys("xpath=//*[@id='state']","Sample State");
        browser.typeKeys("xpath=//*[@id='zipCode']","312346");
        browser.typeKeys("xpath=//*[@id='state']","Sample State");
        browser.typeKeys("xpath=//*[@id='creditCardNumber']","1234-2456-6789-2344-1234");
        browser.typeKeys("xpath=//*[@id='nameOnCard']","TestName");
        browser.click("xpath=//*[@id='rememberMe']");
        browser.click("xpath=//*[@value='Purchase Flight']");
        WebElement confirmId=browser.getWebDriver().findElement(By.xpath("//*[text()='Id']/following-sibling::td"));
        Assert.assertTrue(!confirmId.getText().equals(""),"Confirmation ID is not generated");
    }

    private String[][] getDepCity(List<WebElement> citiesDep, List<WebElement>citiesDest) {
        String[][] cityName = new String[citiesDep.size()][2];
            int i=0;
            for(int j=0;j<citiesDest.size();j++) {
                cityName[j][0] = citiesDep.get(i).getAttribute("value");
                cityName[j][1] = citiesDest.get(j).getAttribute("value");
            }
            return cityName;
    }

    @DataProvider(name = "city")
    public Object[][] attributeType() {
        return getDepCity(depCity,destCity);
    }


}
