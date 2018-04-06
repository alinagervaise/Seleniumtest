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
public class TestSouscriptionUKMultiExcel extends TestBase implements TestInterface{
	
	/**
	 * @throws java.lang.Exception
	 * 
	 * TODO: Elements with no ID  button "add to cart"
	 *
	 */


	@Before
	public void setUp() throws Exception {
		 this.setProperties();
		 this.setCaptureFile("screenshotUK");
		
		 driver = new WebDriverFactory().getDriver(DriverType.FIREFOX); 
		 //driver.manage().timeouts().implicitlyWait(ConstantUtils.IMPLICIT_WAIT_TIME, TimeUnit.MINUTES);
		 driver.manage().timeouts().implicitlyWait(Long.parseLong(properties.getProperty("IMPLICIT_WAIT_TIME")), TimeUnit.MINUTES);
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
	
	     this.setHandler("errLogUK");
	}
	

	  @Test
	  public void testCaseSouscriptionUserExist() throws IOException,ParseException, InterruptedException {
		  
		  try{
			  isTestPassed = true;
			  ExcelReader objExcelFile = new ExcelReader();
			  
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.readFile(ConstantUtils.INPUT_FILE_PATH_UK, Country.UK);
			  String infotest = "EXECUTE "+ this.getClass().getSimpleName()+ "\n";
			  infotest += "READING DATA FROM FILE: "+ ConstantUtils.INPUT_FILE_PATH_UK +"\n";
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
		driver.get(properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL"));
		
		selectProduct(driver, resultSet);
		
		String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
		+ properties.getProperty("LOGIN_URL");
		login(driver, expectedURL, resultSet);
		expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
						+ properties.getProperty("SHIPPING_URL");
	    this.checkCurrentURL(driver, expectedURL, LOGGER);
		LOGGER.log(Level.INFO, "USER "+resultSet.get("email")+ "  IS CONNECTED\n");
	    //checkUserInfo(driver, resultSet);
		 
		 expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
			+ properties.getProperty("SHIPPING_URL");
		 this.checkCurrentURL(driver, expectedURL, LOGGER);
	     By byBillingSave = By.xpath("//button[@name='dwfrm_billing_save']");

    
		new FluentWait<WebDriver>(driver)
		 		    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		 		    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		 		    .ignoring(WebDriverException.class)
		 		    .until(ExpectedConditions.elementToBeClickable(byBillingSave)).click();
		
		expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
		+ properties.getProperty("SUBSCRIPTION_URL");
		this.checkCurrentURL(driver, expectedURL, LOGGER);
	    getSouscription(driver, resultSet);
       
	    makePayment(driver, resultSet);
		
	  

        expectedURL = properties.getProperty("BASE_URL") 
        		+ properties.getProperty("COUNTRY_UK_URL")+ properties.getProperty("PAYMENT_CONFIRMATION_URL");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.urlContains(expectedURL));
	    
	    LOGGER.log(Level.INFO,"PAYMENT COMPLETED WITH SUCCESS!\n");
	
	    logout(driver);
	   
	}


	public void selectProduct(WebDriver driver, Map<String, String> resultSet) throws GUIException {
		By menuBy =By.xpath("//nav[@id='navigation']/div/ul/li[2]/a");
//	    new FluentWait<WebDriver>(driver)
//	    .withTimeout(1, TimeUnit.MINUTES)
//	    .pollingEvery(5, TimeUnit.SECONDS)
//	    .ignoring(WebDriverException.class)
//	    .until(ExpectedConditions.and(
//	    		ExpectedConditions.visibilityOfElementLocated(menuBy),
//	    		ExpectedConditions.elementToBeClickable(menuBy)
//	    		));
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
	    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.and(
	    		ExpectedConditions.visibilityOfElementLocated(menuBy),
	    		ExpectedConditions.elementToBeClickable(menuBy)
	    		));
	  
	    WebElement menuProductElement = driver.findElement(menuBy);
	    menuProductElement.click();
	  
	    //By agreementBy = By.xpath("//input[@class='required-for-add-to-cart']");
	    By agreementBy = By.id("checkbox-eligibility-criteria");
	    WebElement agreementElement = driver.findElement(agreementBy);
	    agreementElement.click();
	    //By agreementBy2 = By.xpath("//p/input[2]");
	    By agreementBy2 = By.id("checkbox-read-documents");
	    
	    WebElement agreementElement2 = driver.findElement(agreementBy2);
	    agreementElement2.click();
	    //driver.findElement(By.xpath("//button[@class='add-all-to-cart product-0']")).click();
	    driver.findElement(By.xpath("//div/button")).click();
	    
	    
	    By byAddToCart = By.name("dwfrm_cart_checkoutCart");
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
			.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(byAddToCart));
		LOGGER.log(Level.INFO, "PRODUCT WAS SELECTED : OK! ");
		driver.findElement(byAddToCart).click();
	    
	}

	
	public void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		String errorMessage = "";
		String successMessage = "";
		boolean errorExist = false;
		String field = "";
		String xPath = "";
		
		//String expectedUrl = "https://staging-store-rcibsp.demandware.net/s/RCI_UK/subscription";
		String expectedUrl = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
							+ properties.getProperty("SUBSCRIPTION_URL");
		this.checkCurrentURL(driver, expectedUrl, LOGGER);
	
		
		field = "Registration";
		successMessage = "SET "+ field +" FIELD TO : " + resultSet.get("Registration");
		xPath = "//*[@id='dwfrm_billing_subscriptionInformation_plate']";
	    WebElement plateElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_plate"));
	    jse2.executeScript("arguments[0].scrollIntoView()", plateElement); 
	    plateElement.clear();
	    plateElement.sendKeys(resultSet.get("Registration"));
	    plateElement.sendKeys(Keys.TAB);
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	   // errorExist  |= !( errorMessage.isEmpty());
	    
	   
	    field = "Brand";
	    successMessage = "SET "+ field +" FIELD TO : " + resultSet.get("Brand").toUpperCase();
	    xPath = "//*[@id='dwfrm_billing_subscriptionInformation_vehicleInfoBrand']";
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoBrand"))).selectByVisibleText(resultSet.get("Brand").toUpperCase());
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage.isEmpty());
	    
	    field = "Model";
	    successMessage = "SET "+ field +" FIELD TO: " + resultSet.get("Model").toUpperCase();
	    xPath = "//*[@id='dwfrm_billing_subscriptionInformation_vehicleInfoModel']";
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoModel"))).selectByVisibleText(resultSet.get("Model"));
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage.isEmpty());
	
	   
	    String strCarDate = resultSet.get("Vehicle Insurance Date");
	    field = "Vehicle insurance Date";
	    LOGGER.log(Level.INFO, "SET "+ field +" FIELD TO: " + strCarDate+"\n");
		Date dateVehicle =  sf.parse(strCarDate);
	   
	 	c.setTime(dateVehicle);
	 	//xPath = "//img[@class='ui-datepicker-trigger']";
	    //WebElement datepicker = driver.findElement(By.xpath("//img[@class='ui-datepicker-trigger']"));
	    //datepicker.click();
	   
	    //new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    //new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    //driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
	 	String subscription_date = String.join("/", String.format("%02d", c.get(Calendar.DAY_OF_MONTH))
	 											, String.format("%02d", c.get(Calendar.MONTH))
	 											, String.format("%04d", c.get(Calendar.YEAR)));
	 	driver.findElement(By.id("dwfrm_billing_subscriptionInformation_vehicleInsuranceDate")).sendKeys(subscription_date);
	 	
	    
		By byAgreeTerms = By.name("dwfrm_billing_subscriptionInformation_agreeTerms"); 

		 
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		 	.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(byAgreeTerms));
		
		 WebElement agreeTermsElment = driver.findElement(byAgreeTerms); 
		 
		 
		 
		 jse2.executeScript("arguments[0].scrollIntoView()", agreeTermsElment);

		 jse2.executeScript("arguments[0].click()", agreeTermsElment);
		 LOGGER.log(Level.INFO, "CLICK AGREE ON TERMS : OK!\n");
		 GUILogger.raiseError(errorExist);
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
		WebElement billingSaveElment = driver.findElement(By.xpath(xPath));
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		//String expectedURL = "https://staging-store-rcibsp.demandware.net/s/RCI_UK/placeorder";
		String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_UK_URL")
							+ properties.getProperty("PLACE_ORDER_URL");
		this.checkCurrentURL(driver, expectedURL, LOGGER);
		 driver.findElement(By.id("is-WorldPay")).click();
		    
		 WebElement placeOrderElement = driver.findElement(By.id("placeOrder"));
		 jse2.executeScript("arguments[0].scrollIntoView()", placeOrderElement); 
		placeOrderElement.click();
		
		
	    field = "Card number";
	    xPath = "//input[@name='cardNumber']";
	    successMessage = "SET '"+ field +"' VALUE TO : " +resultSet.get("Card number");
	    WebElement cardNumberElement = driver.findElement(By.name("cardNumber"));
	    cardNumberElement.clear();
	    cardNumberElement.sendKeys(resultSet.get("Card number"));
	    cardNumberElement.sendKeys(Keys.RETURN);
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
	 
	    String strDate = resultSet.get("Expiry date");
		Date date =  sf.parse(strDate);
		c.setTime(date);
		
		field = "Expiration Month";
		xPath = "//select[@id='expiryMonth']";
		successMessage = "ENTER  '"+ field +"' to: " +String.format("%02d", c.get(Calendar.MONTH));
	    new Select(driver.findElement(By.id("expiryMonth"))).selectByValue(String.format("%02d", c.get(Calendar.MONTH)));
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
	    
	    field = "Expiration Year";
	    xPath = "//select[@id='expiryYear']";
	    successMessage = "ENTER  '"+ field +"' TO : " +String.format("%02d", c.get(Calendar.YEAR));
	    new Select(driver.findElement(By.id("expiryYear"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
	    
	    field = "Security Code";
	    xPath = "//input[@id='securityCode']";
	    successMessage = "ENTER '"+ field +"' TO: " +resultSet.get("Security number");
	    WebElement securityCodeElement = driver.findElement(By.id("securityCode"));
	    securityCodeElement.clear();
	    securityCodeElement.sendKeys(resultSet.get("Security number"));
	    //errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
	    //errorExist  |= !(errorMessage == null || errorMessage.isEmpty());
	    
	    field = "Submit";
	    LOGGER.log(Level.INFO, "CLICK ON BUTTON '"+ field +"' \n");
	    driver.findElement(By.id("submitButton")).click();
	    GUILogger.raiseError(errorExist);
	    
	}
	
	
	
	
	private void checkUserInfo(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
			String actualValue = "";
			String errorMessage= "FAIL TO FILL FIELD : ";
			String field;
		    new Select(driver.findElement(By.id("dwfrm_billing_title"))).selectByVisibleText("Miss");
		    
		    java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		    Calendar c = Calendar.getInstance();
		    
		    field = "Date of birth";
		    String strCarDate = resultSet.get("date of birth");
			Date dateVehicle =  sf.parse(strCarDate);
		 	c.setTime(dateVehicle);
		    WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		   
			jse2.executeScript("arguments[0].scrollIntoView()", datepicker);
		   
		    String value = String.format("%02d",c.get(Calendar.DATE))+"-"
		    				+String.format("%02d",c.get(Calendar.MONTH))+"-"
		    				+Integer.toString(c.get(Calendar.YEAR));

		    String xPath = "//input[@id='dwfrm_billing_billingAddress_addressFields_birthday']";
            String dateOfBirth = driver.findElement(By.xpath(xPath)).getText();
            Assert.assertEquals(errorMessage+field, value, dateOfBirth);
            
		    field = "Mobilephone";
		    actualValue = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).getText();
		    Assert.assertEquals(errorMessage+field, resultSet.get("phone number"), actualValue);
		    
		    field ="Address 1";
		    actualValue = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address1")).getText();
		    Assert.assertEquals(errorMessage+field, resultSet.get("Property number / Name"), actualValue);
		    
		    field ="Address 2";
		    WebElement streetElement = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_address2"));
		    jse2.executeScript("arguments[0].scrollIntoView()", streetElement);
		    actualValue = streetElement .getText();
		    Assert.assertEquals(errorMessage+field, resultSet.get("Street"), actualValue);
		    
		    
		    driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal")).clear();
		    WebElement postalCodeElement = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_postal"));
		    jse2.executeScript("arguments[0].scrollIntoView()", postalCodeElement);
		    postalCodeElement.sendKeys(resultSet.get("Postal Code"));
		    
		    
		    field = "city";
		    actualValue = driver.findElement(By.id("dwfrm_billing_billingAddress_addressFields_city")).getText();
		    Assert.assertEquals(errorMessage+field, resultSet.get("City"), actualValue);
		  
	}


}