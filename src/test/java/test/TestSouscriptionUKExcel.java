/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import staging.rcibsp.Country;
import staging.rcibsp.ExcelReader;


/**
 * @author galinabikoro
 *
 */
public class TestSouscriptionUKExcel {
	private WebDriver driver;
	private WebDriverWait wait;
	private final String BASE_URL = "https://staging-store-rcibsp.demandware.net";
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private java.text.SimpleDateFormat sf;
	private Calendar c;
	private JavascriptExecutor jse2;
	/**
	 * @throws java.lang.Exception
	 */
	enum DriverType{
		FIREFOX, CHROME
	};

	@Before
	public void setUp() throws Exception {
		
		 driver = this.getDriver(DriverType.FIREFOX);
		 driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		 wait = new WebDriverWait(driver, 1);
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
	}
	
	private WebDriver getDriver( DriverType driverType){
		WebDriver _Driver = null;
		switch(driverType){
			case FIREFOX:
				 System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\src\\SELENIUM_DRIVERS\\geckodriver-v0.19.1-win64\\geckodriver.exe");
				 DesiredCapabilities capabilities=DesiredCapabilities.firefox();
				 capabilities.setCapability("marionette", false);
			     _Driver = new FirefoxDriver(capabilities);
			     _Driver.manage().window().maximize();
			     break;
			case CHROME:
				 System.setProperty("webdriver.chrome.driver",  System.getProperty("user.dir")+"\\src\\SELENIUM_DRIVERS\\chromedriver_win32\\chromedriver.exe");
				 ChromeOptions options = new ChromeOptions();
				 options.addArguments("--start-maximized");
				 _Driver = new ChromeDriver(options);
				 break;	
		}
		return _Driver;
	}
	


	  @Test
	  public void testCaseSouscriptionUserExist() throws Exception {
		  
		  ExcelReader objExcelFile = new ExcelReader();
		  String filePath = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeudetestFormated.xlsx";
		  List<Map<String, String>> result = objExcelFile.read(filePath, Country.UK);
		  for (Map m : result){
			  if (m.isEmpty()){
				  continue;
			  }
		    	System.out.println(m);
		    	runSelenium(m);
		    	//break;
			 
		  }
	   
	  }

	public void runSelenium(Map<String, String> resultSet) throws ParseException, InterruptedException {
		driver.get(BASE_URL + "/s/RCI_UK/");
	    driver.findElement(By.linkText("KEY COVER")).click();
	    driver.findElement(By.cssSelector("button.add-all-to-cart.product-0")).click();
	    driver.findElement(By.cssSelector("a.action.dialog-cart-show")).click();
	    driver.findElement(By.xpath("(//form[@id='checkout-form']/fieldset/button)[2]")).click();
	    
	    login(driver, resultSet);
	    
	    String URL = driver.getCurrentUrl();
	    if (URL.equalsIgnoreCase("https://staging-store-rcibsp.demandware.net/s/RCI_UK/shipping")){
	    	//user who never made a suscription
	    	enterUserInfo(driver, resultSet);
	    }
	   
	    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[name=\"dwfrm_billing_save\"]"))).click();
	    getSouscription(driver, resultSet);

	    makePayment(driver, resultSet);
		
	    WebElement el = driver.findElement(By.xpath("//div[@class='header-banner-right']/ul/li/a/i"));
	    
	    wait.until(ExpectedConditions.urlToBe("https://staging-store-rcibsp.demandware.net/s/RCI_UK/orderconfirmed"));
	    logout(driver, resultSet);
	   
	}
	private void logout(WebDriver driver, Map<String, String> resultSet){
		 driver.findElement(By.xpath("//div[@class='header-banner-right']/ul/li/a/i")).click();
		 driver.findElement(By.xpath("//span[@class='account-logout']/a")).click();
	
	}
	private void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException{
	    driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vin")).clear();
	    driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vin")).sendKeys(resultSet.get("VIN"));
	    driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate")).clear();
	    driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate")).sendKeys(resultSet.get("Registration"));
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoBrand"))).selectByVisibleText(resultSet.get("Brand").toUpperCase());
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoModel"))).selectByVisibleText(resultSet.get("Model"));
	    
	  
	  
	    
	    String strCarDate = resultSet.get("Vehicle Insurance Date");
		Date dateVehicle =  sf.parse(strCarDate);
	   
	 	c.setTime(dateVehicle);
	    WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
	    datepicker.click();
	   
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
	    
	   
	    
	    WebElement agreeTermsElment = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_agreeTerms"));
		jse2.executeScript("arguments[0].scrollIntoView()", agreeTermsElment); 
		
		
		agreeTermsElment.click();
	}
	private void makePayment(WebDriver driver, Map<String, String> resultSet) throws ParseException{
		WebElement billingSaveElment = driver.findElement(By.name("dwfrm_billing_save"));
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		
		driver.findElement(By.cssSelector("button[value=\"Continue to Place Order >\"]")).click();
	    //driver.findElement(By.name("dwfrm_billing_save")).click();
	    driver.findElement(By.name("dwfrm_billing_paymentMethods_selectedPaymentMethodID")).click();
		
		WebElement paymentMethodElment = driver.findElement(By.name("dwfrm_billing_paymentMethods_selectedPaymentMethodID"));
		jse2.executeScript("arguments[0].scrollIntoView()", paymentMethodElment); 
		paymentMethodElment.click();
	    
	    //driver.findElement(By.id("is-WorldPay")).click();
	    driver.findElement(By.id("placeOrder")).click();
	    driver.findElement(By.id("cardNumber")).clear();
	    //System.out.println(""+resultSet.get("Card number"));
	    driver.findElement(By.id("cardNumber")).sendKeys(resultSet.get("Card number"));
	 
	    String strDate = resultSet.get("Expiry date");
		
		Date date =  sf.parse(strDate);
		
		c.setTime(date);
	    new Select(driver.findElement(By.id("expiryMonth"))).selectByValue(String.format("%02d", c.get(Calendar.MONTH)));
	    new Select(driver.findElement(By.id("expiryYear"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		
	    driver.findElement(By.id("securityCode")).clear();
	    driver.findElement(By.id("securityCode")).sendKeys(resultSet.get("Security number"));
	    driver.findElement(By.id("submitButton")).click();
	    
	    
	}
	private void login(WebDriver driver, Map<String, String> resultSet) {
		driver.findElement(By.xpath("//form[@id='dwfrm_login']/fieldset/div/div/input")).clear();
	    driver.findElement(By.xpath("//form[@id='dwfrm_login']/fieldset/div/div/input")).sendKeys(resultSet.get("email"));
	    driver.findElement(By.xpath("//form[@id='dwfrm_login']/fieldset/div[2]/div/input")).clear();

	    driver.findElement(By.xpath("//form[@id='dwfrm_login']/fieldset/div[2]/div/input")).sendKeys(resultSet.get("password").trim());
	    driver.findElement(By.name("dwfrm_login_login")).click();
	}

	 /* @After
	  public void tearDown() throws Exception {
	    driver.quit();
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) {
	      fail(verificationErrorString);
	    }
	  }*/
	private void enterUserInfo(WebDriver driver, Map<String, String> resultSet) throws ParseException{
			//driver.findElement(By.id("dwfrm_login_username_d0pqphmpixou")).clear();
		    //driver.findElement(By.id("dwfrm_login_username_d0pqphmpixou")).sendKeys(resultSet.get("email"));
		    //driver.findElement(By.id("dwfrm_login_password_d0dopecnhtzp")).clear();
		    //driver.findElement(By.id("dwfrm_login_password_d0dopecnhtzp")).sendKeys(resultSet.get("password"));
		   // driver.findElement(By.name("dwfrm_login_login")).click();
		    new Select(driver.findElement(By.id("dwfrm_billing_title"))).selectByVisibleText("Miss");
		    
		    
		    //driver.findElement(By.cssSelector("img.ui-datepicker-trigger")).click();
		    //driver.findElement(By.linkText("2")).click();
		    java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		    Calendar c = Calendar.getInstance();
		    
		    String strCarDate = resultSet.get("date of birth");
		    //System.out.println("date of birth"+strCarDate);
			Date dateVehicle =  sf.parse(strCarDate);
			// System.out.println("date of birth"+dateVehicle +"   type "+dateVehicle.getClass().getTypeName());
		 	c.setTime(dateVehicle);
		 	
		 	//driver.findElement(By.name("dwfrm_billing_billingAddress_addressFields_birthday")).click();
		 	// System.out.println("date of birth"+strCarDate);
		    WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		    //JavascriptExecutor jse2 = (JavascriptExecutor)driver;
			//jse2.executeScript("arguments[0].scrollIntoView()", datepicker);
		    datepicker.click();
		    
		    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
		    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		    
		    
		    
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).sendKeys(resultSet.get("phone number"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address1")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address1")).sendKeys(resultSet.get("Property number / Name"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address2")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address2")).sendKeys(resultSet.get("Street"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal")).sendKeys(resultSet.get("Postal Code"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_city")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_city")).sendKeys(resultSet.get("City"));
		    
		    //driver.findElement(By.cssSelector("button[name=\"dwfrm_billing_save\"]")).click();
	}
	public static boolean isClickable(WebElement el, WebDriver driver) 
	{
		try{
			WebDriverWait wait = new WebDriverWait(driver, 6);
			wait.until(ExpectedConditions.elementToBeClickable(el));
			return true;
		}
		catch (Exception e){
			return false;
		}
	}
	  private boolean isElementPresent(By by) {
	    try {
	      driver.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	  private boolean isAlertPresent() {
	    try {
	      driver.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }

	  private String closeAlertAndGetItsText() {
	    try {
	      Alert alert = driver.switchTo().alert();
	      String alertText = alert.getText();
	      if (acceptNextAlert) {
	        alert.accept();
	      } else {
	        alert.dismiss();
	      }
	      return alertText;
	    } finally {
	      acceptNextAlert = true;
	    }
	  }
}