/**
 * 
 */
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

import staging.rcibsp.Country;
import staging.rcibsp.DriverType;
import staging.rcibsp.ExcelReader;
import staging.rcibsp.GUIException;
import staging.rcibsp.GUILogger;
import staging.rcibsp.Loader;
import staging.rcibsp.WebDriverFactory;
import staging.rcibsp.ConstantUtils;


/**
 * @author galinabikoro
 *
 */
public class TestSouscriptionBRMultiExcel extends TestBase implements TestInterface{
	/**
	 * @throws java.lang.Exception
	 * TODO: Identify elements where the xpath is required and why?
	 *        
	 *       Product menu: no ID, no name just pure link
	 *       
	 * 		 Identity the elements for which the ID was dynamic
	 *        Login, Password
	 * 
	 */

	@Before
	public void setUp() throws Exception {
		 this.setProperties();
		 this.setCaptureFile("screenshotBR");
		 
		 driver = new WebDriverFactory().getDriver(DriverType.FIREFOX); 
		 //driver.manage().timeouts().implicitlyWait(ConstantUtils.IMPLICIT_WAIT_TIME, TimeUnit.MINUTES);
		 driver.manage().timeouts().implicitlyWait(Long.parseLong(properties.getProperty("IMPLICIT_WAIT_TIME")), TimeUnit.MINUTES);
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
	
	     this.setHandler("errLogBR");
	}
	
	  @Test
 public void testCaseSouscriptionUserExist() throws IOException,ParseException, InterruptedException {
		  
		  try{
			  isTestPassed = true;
			  
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.readFile(ConstantUtils.INPUT_FILE_PATH_BR, Country.BR);
			  String infotest = "EXECUTE "+ this.getClass().getSimpleName()+ "\n";
			  infotest += "READING DATA FROM FILE: "+ ConstantUtils.INPUT_FILE_PATH_BR +"\n";
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
		//driver.get(BASE_URL + "/s/RCI_BR/");
		
		driver.get(properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_BR_URL"));
		
	    this.selectProduct(driver, resultSet);
	    String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_BR_URL")
		+ properties.getProperty("LOGIN_URL");
	    login(driver, expectedURL, resultSet);
	    expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_BR_URL")
		+ properties.getProperty("SHIPPING_URL");
	    this.checkCurrentURL(driver, expectedURL, LOGGER);
	    LOGGER.log(Level.INFO, "USER "+resultSet.get("email")+ "  IS CONNECTED\n");


	   
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
		
	   //http://staging-store-rcibsp.demandware.net/s/RCI_BR/produtos/BR-P-MAINTENANCE.html?lang=pt_BR
	    //String expectedUrl = "https://staging-store-rcibsp.demandware.net/s/RCI_BR/orderconfirmed";
	    String expectedUrl =properties.getProperty("BASE_URL") 
	    					+ properties.getProperty("COUNTRY_BR_URL")+ properties.getProperty("PAYMENT_CONFIRMATION_URL");
		 new FluentWait<WebDriver>(driver)
			.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.urlContains(expectedUrl));
		this.checkCurrentURL(driver, expectedUrl, LOGGER);
		
	     LOGGER.log(Level.INFO,"PAYMENT COMPLETED WITH SUCCESS!\n");
		isTestPassed = true;
	    logout(driver);
	   
	}
	
	
	public void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		
		String errorMessage = "";
		String successMessage = "";
		boolean errorExist = false;
		String field = "";
		String xPath = "";
		//String URL = driver.getCurrentUrl();
		//String expectedUrl = "https://staging-store-rcibsp.demandware.net/s/RCI_BR/subscription";
		//this.checkCurrentURL(driver, expectedUrl, LOGGER);
	
		field = "VIN";
		successMessage = "SET "+ field +" FIELD TO : " + resultSet.get("VIN");
		xPath = "//*[@id='dwfrm_billing_subscriptionInformation_vin']";
	    // WebElement vinElement = driver.findElement(By.xpath(xPath));
	    WebElement vinElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vin"));
	    jse2.executeScript("arguments[0].scrollIntoView()", vinElement); 
		vinElement.clear();
	    vinElement.sendKeys(resultSet.get("VIN"));
	    vinElement.sendKeys(Keys.TAB);
	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    errorExist  |= !( errorMessage.isEmpty());
	 
	    field = "Registration number";
	    successMessage = "SET "+ field +" FIELD TO : " + resultSet.get("Registration number");
	    xPath = "//*[@id='dwfrm_billing_subscriptionInformation_plate']";
	    //WebElement plateElement = driver.findElement(By.xpath(xPath));
	    WebElement plateElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate"));
	    plateElement.clear();
	    plateElement.sendKeys(resultSet.get("Registration number"));
	    plateElement.sendKeys(Keys.TAB);
	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    errorExist  |= !( errorMessage.isEmpty());
	
	    By byTermConditions = By.id("dwfrm_billing_subscriptionInformation_agreeTerms");
	    WebElement termConditionsElement = driver.findElement( byTermConditions);
	    jse2.executeScript("arguments[0].scrollIntoView()", termConditionsElement); 
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.elementToBeClickable(termConditionsElement));
	    jse2.executeScript("arguments[0].click()", termConditionsElement); 
	    
	    driver.findElement(By.name("dwfrm_billing_save")).click();
	    LOGGER.log(Level.INFO, "CLICK AGREE ON TERMS : OK!\n");
		GUILogger.raiseError(errorExist);
	}
	
	public void selectProduct(WebDriver driver, Map<String, String> resultSet) throws GUIException, ParseException{
		
		
		//driver.findElement(By.linkText("REVISÃO + FÁCIL")).click();
		driver.findElement(By.cssSelector("ul.menu-category.level-1 li:nth-of-type(1) a")).click();
		driver.findElement(By.cssSelector("ul.level-2-menu.menu-vertical li:nth-of-type(3) a")).click();
		
		WebElement vehicleVersionElement = driver.findElement(By.id("va-VehicleVersion"));
		 jse2.executeScript("arguments[0].scrollIntoView()", vehicleVersionElement);
		new Select(driver.findElement(By.id("va-VehicleVersion"))).selectByVisibleText(resultSet.get("Model"));
		 
		 new Select(driver.findElement(By.id("va-duration"))).selectByVisibleText(resultSet.get("Souscription duration"));
		 driver.findElement(By.id("va-duration")).sendKeys(Keys.TAB);
		 //By byMileage = By.cssSelector("#va-mileage");
		 By byMileage =By.id("va-mileage");
//		 new FluentWait<WebDriver>(driver)
//		    .withTimeout(4, TimeUnit.MINUTES)
//		    .pollingEvery(2, TimeUnit.SECONDS)
//		    .ignoring(WebDriverException.class)
//		    .until(ExpectedConditions.elementToBeClickable(byMileage));
//		 new FluentWait<WebDriver>(driver)
//		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
//			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
//		    .ignoring(WebDriverException.class)
//		    .until(ExpectedConditions.elementToBeClickable(byMileage));
//		
		 new Select(driver.findElement(byMileage)).selectByVisibleText(resultSet.get("Mileage covered"));
		    
		
		    
		By bySubProduct = By.cssSelector("a.add-all-to-cart.product-0");
		
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(bySubProduct));
		 WebElement subProductElement = driver.findElement(bySubProduct);
		 jse2.executeScript("arguments[0].scrollIntoView()", subProductElement);
		 subProductElement.click();
		
		driver.findElement(By.id("vehiclefirstservice2")).click();
		 
		driver.findElement(By.id("vehiclecurrentmilage")).clear();
		driver.findElement(By.id("vehiclecurrentmilage")).sendKeys(resultSet.get("Actual Mileage"));
		
		//WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		//datepicker.click();
		driver.findElement(By.id("vehicleregistrationdate")).click();
		String strCarDate = resultSet.get("Car registration date");
		Date dateVehicle =  sf.parse(strCarDate);
		c.setTime(dateVehicle);
		new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
		driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		
		driver.findElement(By.id("vehicleregistrationdate")).sendKeys(Keys.TAB);
		//driver.findElement(By.cssSelector("button.product-0")).click();
		//By comprarBy = By.xpath("//button[@id='actionlink']");
		By comprarBy = By.id("actionlink");
		//By comprarBy = By.cssSelector("button.product-0");
		//By comprarBy = By.xpath("//div[@id='firstStep']/button");
		//By comprarBy = By.xpath("//div[7]/div[2]/button");
//		 new FluentWait<WebDriver>(driver)
//		    .withTimeout(5, TimeUnit.MINUTES)
//		    .pollingEvery(2, TimeUnit.SECONDS)
//		    .ignoring(WebDriverException.class)
//		    .until(ExpectedConditions.elementToBeClickable(comprarBy)).click();
//		
		//driver.findElement(comprarBy).sendKeys(Keys.TAB);
		driver.findElement(comprarBy).sendKeys(Keys.ENTER);
		//driver.findElement(comprarBy).click();
		
		//driver.findElement(By.cssSelector("a.action.dialog-cart-show")).click();
		
		By byAddToCart = By.name("dwfrm_cart_checkoutCart");
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(byAddToCart));
		LOGGER.log(Level.INFO, "PRODUCT WAS SELECTED : OK! ");
		driver.findElement(byAddToCart).click();
		
		
		
		 
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
		By byMakePayment = By.name("dwfrm_billing_save");
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(byMakePayment));
		WebElement billingSaveElment = driver.findElement(byMakePayment);
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		//String expectedURL = "https://staging-store-rcibsp.demandware.net/s/RCI_BR/placeorder";
		String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_BR_URL")
		+ properties.getProperty("PLACE_ORDER_URL");
		//this.checkCurrentURL(driver, expectedURL, LOGGER);
		
		
	    driver.findElement(By.id("is-WorldPay")).click();
	    
	    WebElement placeOrderElement = driver.findElement(By.id("placeOrder"));
	    jse2.executeScript("arguments[0].scrollIntoView()", placeOrderElement); 
	    placeOrderElement.click();
	    
	    field = "Card number";
	    xPath = "//input[@name='cardNumber']";
	    successMessage = "SET '"+ field +"' VALUE TO : " +resultSet.get("Card number");
	    WebElement cardNumberElement = driver.findElement(By.id("cardNumber"));
	    cardNumberElement.clear();
	    cardNumberElement.sendKeys(resultSet.get("Card number"));
	    cardNumberElement.sendKeys(Keys.RETURN);
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	   // errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
	    
	 
		field = "Expiration Month";
		xPath = "//select[@id='expiryMonth']";
		successMessage = "ENTER  '"+ field +"' to: " +String.format("%02d", Integer.parseInt(resultSet.get("Expiry Month")));
	    //new Select(driver.findElement(By.id("expiryMonth"))).selectByValue(String.format("%02d", c.get(Calendar.MONTH)));
	 
	    new Select(driver.findElement(By.id("expiryMonth"))).selectByVisibleText(String.format("%02d",  Integer.parseInt(resultSet.get("Expiry Month"))));
//	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
//	    errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
//	    
	    field = "Expiration Year";
	    xPath = "//select[@id='expiryYear']";
	    successMessage = "ENTER  '"+ field +"' TO : " +resultSet.get("Expiry Year");
//	    new Select(driver.findElement(By.id("expiryYear"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.id("expiryYear"))).selectByVisibleText(resultSet.get("Expiry Year"));
//	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
//	    errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
//	    
	    field = "Security Code";
	    xPath = "//input[@id='securityCode']";
	    successMessage = "ENTER '"+ field +"' TO: " +resultSet.get("Security number");
	    WebElement securityCodeElement = driver.findElement(By.id("securityCode"));
	    securityCodeElement.clear();
	    securityCodeElement.sendKeys(resultSet.get("Security number"));
//	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
//	    errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
//	    
	    field = "Submit";
	    LOGGER.log(Level.INFO, "CLICK ON BUTTON '"+ field +"' \n");
	    driver.findElement(By.id("submitButton")).click();
	    GUILogger.raiseError(errorExist);
	 
	    
	}

	 

	private void enterUserInfo(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		
		    new Select(driver.findElement(By.id("dwfrm_billing_title"))).selectByVisibleText("Miss");
		    java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		    Calendar c = Calendar.getInstance();
		    
		    String strCarDate = resultSet.get("date of birth");
			Date dateVehicle =  sf.parse(strCarDate);
		 	c.setTime(dateVehicle);
		    WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
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