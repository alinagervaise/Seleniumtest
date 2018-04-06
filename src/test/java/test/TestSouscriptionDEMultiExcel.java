package test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

import staging.rcibsp.ConstantUtils;
import staging.rcibsp.Country;
import staging.rcibsp.DriverType;
import staging.rcibsp.ExcelReader;
import staging.rcibsp.GUIException;
import staging.rcibsp.GUILogger;
import staging.rcibsp.Loader;
import staging.rcibsp.WebDriverFactory;


/**
 * @author galinabikoro
 *
 */
public class TestSouscriptionDEMultiExcel extends TestBase implements TestInterface {
	
	/**
	 * @throws java.lang.Exception
	 */

	@Before
	public void setUp() throws Exception {
		 this.setProperties();
		 this.setCaptureFile("screenshotDE");
		
		 driver = new WebDriverFactory().getDriver(DriverType.FIREFOX); 
		 driver.manage().timeouts().implicitlyWait(Long.parseLong(properties.getProperty("IMPLICIT_WAIT_TIME")), TimeUnit.MINUTES);
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
	
	     this.setHandler("errLogDE");
	}
	  @Test
	  public void testCaseSouscriptionUserExist() throws IOException,ParseException, InterruptedException {
		  try{
			  isTestPassed = true;
			  ExcelReader objExcelFile = new ExcelReader();
			  
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.readFile(ConstantUtils.INPUT_FILE_PATH_DE, Country.DE);
			  String infotest = "EXECUTE "+ this.getClass().getSimpleName()+ "\n";
			  infotest += "READING DATA FROM FILE: "+ ConstantUtils.INPUT_FILE_PATH_DE +"\n";
			  LOGGER.info(infotest);
			  int count = 0;
			  for (Map m : result){
				  errorMessage = "";
				  if (m.isEmpty()){
					  continue;
				  }
				  count += 1;
				  LOGGER.info("BEGINNING TEST: \n");
				  LOGGER.info("for data set " + count +":"+ m +"\n");
				  try{
				    	runSelenium(m);
				    	LOGGER.info("END OF TEST FOR DATA SET: " + count +" SUCCESS!\n");
				  }
				  catch(GUIException e){
					  if (e != null){
						  takeScreenshot(e);
						  logout(driver);
						  LOGGER.info("END OF TEST FOR DATE SET :" + count +" FAIL!\n");
						  isTestPassed = false;
						  continue;
					  }  
				  }
				  catch(Exception ex){
					  LOGGER.log(Level.SEVERE, ex.getClass().getName()+ "   "+ex.getMessage());
					  logout(driver);
					  continue;
				  }
		  }
		  }catch(IOException ex){
			  LOGGER.log(Level.SEVERE, ex.getClass().getName()+ "   "+ex.getMessage());
		  }
	   
	  }
	

	public void runSelenium(Map<String, String> resultSet) throws ParseException, InterruptedException, GUIException {
		driver.get(properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL"));
	
	    this.selectProduct(driver, resultSet);
	    //String expectedURL= "https://staging-store-rcibsp.demandware.net/s/RCI_DE/login";
	    String expectedURL= properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL")
							+ properties.getProperty("LOGIN_URL");
	    login(driver, expectedURL,resultSet);
	    String URL = driver.getCurrentUrl();
	   
	    By bySaveBill = By.cssSelector("button[name=\"dwfrm_billing_save\"]");
	    //By bySaveBill = By.name("dwfrm_billing_save");
		new FluentWait<WebDriver>(driver)
	    .withTimeout(2, TimeUnit.MINUTES)
	    .pollingEvery(2, TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.presenceOfElementLocated(bySaveBill));
		
		WebElement billingSaveElment = driver.findElement(bySaveBill);
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
	    getSouscription(driver, resultSet);

	    makePayment(driver, resultSet);
		
	   // WebElement el = driver.findElement(By.xpath("//div[@class='header-banner-right']/ul/li/a/i"));
	    //String expectedUrl = "https://staging-store-rcibsp.demandware.net/s/RCI_DE/orderconfirmed";
	    String expectedUrl =properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL")
						+ properties.getProperty("SUBSCRIPTION_URL");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(1, TimeUnit.MINUTES)
	    .pollingEvery(2, TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.urlToBe(expectedUrl));
	 
		this.checkCurrentURL(driver, expectedUrl, LOGGER);
		isTestPassed = true;
	    //LOGGER.info("BEFORE LOGOUT---------->");
	    //logout(driver);
	   
	}
	
	public void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		String errorMessage = "";
		String successMessage = "";
		boolean errorExist = false;
		String field = "";
		String xPath = "";
		String URL = driver.getCurrentUrl();
		//LOGGER.log(Level.INFO, "GETSOUSCRIPTION URL :"+ URL);
		//String expectedUrl = "https://staging-store-rcibsp.demandware.net/s/RCI_DE/shipping";
		String expectedUrl = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL")
								+ properties.getProperty("SHIPPING_URL");
		this.checkCurrentURL(driver, expectedUrl, LOGGER);
	
		driver.findElement(By.id("dwfrm_billing_subscriptionInformation_agreeInvoices")).click();
		
		//WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		//datepicker.click();
		
		driver.findElement(By.id("dwfrm_billing_subscriptionInformation_contractStartDate")).click();
	    String strCarDate = resultSet.get("Vehicle Insurance Date");
		Date dateVehicle =  sf.parse(strCarDate);
	 	c.setTime(dateVehicle);
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		
	    WebElement vinElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vin"));
	    jse2.executeScript("arguments[0].scrollIntoView()", vinElement); 
		vinElement.clear();
	    vinElement.sendKeys(resultSet.get("VIN"));
	    
	    WebElement hsnElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_hsn"));
	    jse2.executeScript("arguments[0].scrollIntoView()", hsnElement); 
		hsnElement.clear();
	    hsnElement.sendKeys(resultSet.get("HSN"));
	    
	    WebElement tsnElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_tsn"));
		tsnElement.clear();
	    tsnElement.sendKeys(resultSet.get("TSN"));
		
	    String registration = resultSet.get("Licence plate");
	    WebElement plate1Element = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate1"));
	    plate1Element.clear();
	    plate1Element.sendKeys(registration.substring(0,3 ));
	    
	    WebElement plate2Element = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate2"));
	    plate2Element.clear();
	    plate2Element.sendKeys(registration.substring(3, 5));
	    
	    WebElement plate3Element = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate3"));
	    plate3Element.clear();
	    plate3Element.sendKeys(registration.substring(5, 9));
	    
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_brand"))).selectByVisibleText(resultSet.get("Brand").toUpperCase());
	   
	    //WebElement datepicker1 = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
	    //datepicker1.click();
	    //driver.findElement(By.xpath("(//img[@alt='...'])[2]")).click();
	    driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleRegistrationDate")).click();
	    
	    String strDate1 = resultSet.get("Vehicle Insurance Date");
	    Date dateVehicle1 =  sf.parse(strDate1);
	 	c.setTime(dateVehicle1);
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		
	    By byCriteriaConfirmation = By.name("dwfrm_billing_subscriptionInformation_criteriaconfirmation");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(1, TimeUnit.MINUTES)
	    .pollingEvery(2, TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.elementToBeClickable(byCriteriaConfirmation));
	    
	  
	    WebElement criteriaConfirmationElement = driver.findElement(byCriteriaConfirmation);
	    jse2.executeScript("arguments[0].click()", criteriaConfirmationElement); 
	    
	    
	    By byCriteriaConfirmation2 = By.name("dwfrm_billing_subscriptionInformation_readconfirmation");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(1, TimeUnit.MINUTES)
	    .pollingEvery(2, TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.elementToBeClickable(byCriteriaConfirmation2));
	    jse2.executeScript("arguments[0].click()", driver.findElement(byCriteriaConfirmation2)); 
	    
	    driver.findElement(By.id("plate")).click();
		//logError(driver);
	}
	
	public void selectProduct(WebDriver driver, Map<String, String> resultSet) throws GUIException, ParseException{
		
		 //driver.findElement(By.linkText("Securplus")).click();
		 driver.findElement(By.cssSelector("ul.menu-category.level-1 li:nth-of-type(2) a")).click();
		 new Select(driver.findElement(By.id("va-billingFrequency"))).selectByVisibleText("Vierteljährlich");
		 
		 new FluentWait<WebDriver>(driver)
		    .withTimeout(1, TimeUnit.MINUTES)
		    .pollingEvery(2, TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("dwvar_DE-C-SECURPLUS-COVER300_termsDuration"))));
		 new Select(driver.findElement(By.name("dwvar_DE-C-SECURPLUS-COVER300_termsDuration"))).selectByVisibleText("24 Monate");
		 
		 WebElement addCartElement = driver.findElement(By.cssSelector("button.add-all-to-cart.product-0"));
		 jse2.executeScript("arguments[0].scrollIntoView()", addCartElement); 
			new FluentWait<WebDriver>(driver)
		    .withTimeout(1, TimeUnit.MINUTES)
		    .pollingEvery(2, TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(addCartElement));
		 addCartElement.click();
		 //driver.findElement(By.cssSelector("#ui-id-4 > div.actions > a.action.dialog-cart-show")).click();
		 driver.findElement(By.name("dwfrm_cart_checkoutCart")).click();
		 driver.findElement(By.name("dwfrm_cart_checkoutCart")).click();
		 
	}
	
	public void makePayment(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		String field = "";
		String xPath = "";
		String errorMessage = "";
		String successMessage = "";
		boolean errorExist = false;
		
		field = "CONTINUE TO ORDER";
		LOGGER.log(Level.INFO, "CLICK ON BUTTON  '"+ field + "' \n");
		xPath = "//*[@name='dwfrm_billing_save']";
		//WebElement billingSaveElment = driver.findElement(By.xpath(xPath));
		WebElement billingSaveElment = driver.findElement(By.name("dwfrm_billing_save"));
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		
		//String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL")
		//						+ properties.getProperty("PAYMENT_URL");
		//this.checkCurrentURL(driver, expectedURL, LOGGER);
	    driver.findElement(By.id("is-WorldPay")).click();
	    
	    String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_DE_URL")
								+ properties.getProperty("PLACE_ORDER_URL");
		this.checkCurrentURL(driver, expectedURL, LOGGER);
		
	    WebElement placeOrderElement = driver.findElement(By.id("placeOrder"));
	    jse2.executeScript("arguments[0].scrollIntoView()", placeOrderElement); 
	    placeOrderElement.click();
	    
	    driver.findElement(By.id("cardNumber")).clear();
	    driver.findElement(By.id("cardNumber")).sendKeys(resultSet.get("Card number"));
	 
	    String strDate = resultSet.get("Expiry date");
		
		Date date =  sf.parse(strDate);
		
		c.setTime(date);
	    new Select(driver.findElement(By.id("expiryMonth"))).selectByValue(String.format("%02d", c.get(Calendar.MONTH)));
	    new Select(driver.findElement(By.id("expiryYear"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		
	    driver.findElement(By.id("securityCode")).clear();
	    driver.findElement(By.id("securityCode")).sendKeys(resultSet.get("Security number"));
	    
	    WebElement submitElement = driver.findElement(By.id("submitButton"));
	    jse2.executeScript("arguments[0].scrollIntoView()", submitElement); 
	    submitElement.click();
	    //driver.findElement(By.id("submitButton")).click();
	    GUILogger.raiseError(errorExist);
	    
	}
	

	 
	private void enterUserInfo(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
			
		    new Select(driver.findElement(By.id("dwfrm_billing_title"))).selectByVisibleText("Miss");
		    java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		    Calendar c = Calendar.getInstance();
		    
		    String strCarDate = resultSet.get("date of birth");
		    //System.out.println("date of birth"+strCarDate);
			Date dateVehicle =  sf.parse(strCarDate);
			// System.out.println("date of birth"+dateVehicle +"   type "+dateVehicle.getClass().getTypeName());
		 	c.setTime(dateVehicle);
		 	// System.out.println("date of birth"+strCarDate);
		    WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		    //JavascriptExecutor jse2 = (JavascriptExecutor)driver;
			jse2.executeScript("arguments[0].scrollIntoView()", datepicker);
		    datepicker.click();
		    
		    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
		    wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(""+c.get(Calendar.DATE))));
		    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		   

		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).sendKeys(resultSet.get("phone number"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address1")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address1")).sendKeys(resultSet.get("Property number / Name"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address2")).clear();
		    WebElement streetElement = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address2"));
		    jse2.executeScript("arguments[0].scrollIntoView()", streetElement);
		    streetElement.sendKeys(resultSet.get("Street"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal")).clear();
		    WebElement postalCodeElement = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal"));
		    jse2.executeScript("arguments[0].scrollIntoView()", postalCodeElement);
		    postalCodeElement.sendKeys(resultSet.get("Postal Code"));
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_city")).clear();
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_city")).sendKeys(resultSet.get("City"));
		    
		  
	}
	

}