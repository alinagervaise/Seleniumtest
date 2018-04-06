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
public class TestSouscriptionPLMultiExcel  extends TestBase implements TestInterface {
	/**
	 * @throws java.lang.Exception
	 */


	@Before
	public void setUp() throws Exception {
		 this.setProperties();
		 this.setCaptureFile("screenshotPL");
		
		 driver = new WebDriverFactory().getDriver(DriverType.FIREFOX); 
		 driver.manage().timeouts().implicitlyWait(Long.parseLong(properties.getProperty("IMPLICIT_WAIT_TIME")), TimeUnit.MINUTES);
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
	
	     this.setHandler("errLogPL");
	}

	  @Test
	  public void testCaseSouscriptionUserExist() throws IOException,ParseException, InterruptedException {
		  
		  try{
			  isTestPassed = true;
			  ExcelReader objExcelFile = new ExcelReader();
			  
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.readFile(ConstantUtils.INPUT_FILE_PATH_PL, Country.PL);
			  String infotest = "EXECUTE "+ this.getClass().getSimpleName()+ "\n";
			  infotest += "READING DATA FROM FILE: "+ ConstantUtils.INPUT_FILE_PATH_PL +"\n";
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
		//driver.get(BASE_URL + "/s/RCI_PL/");
		driver.get(properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_PL_URL"));
		
	
	    this.selectProduct(driver, resultSet);
	    //login(driver, resultSet);
		String expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_PL_URL")
		+ properties.getProperty("LOGIN_URL");
		login(driver, expectedURL, resultSet);
		
		jse2.executeScript("scroll(0, 1100);");
		
	    //By bySaveBill = By.cssSelector("button[name=\"dwfrm_billing_save\"]");
	    By bySaveBill = By.name("dwfrm_billing_save");
//		new FluentWait<WebDriver>(driver)
//		.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
//		.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
//	    .ignoring(WebDriverException.class)
//	    .until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(bySaveBill), 
//	    		ExpectedConditions.elementToBeClickable(bySaveBill)));
	    
	    
	    //.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(bySaveBill), 
	    //		ExpectedConditions.visibilityOfElementLocated(bySaveBill)));

		WebElement billingSaveElment = driver.findElement(bySaveBill);
		//jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		jse2.executeScript("arguments[0].click()", billingSaveElment);
		//billingSaveElment.click();
		
		
		expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_PL_URL")
						+ properties.getProperty("SUBSCRIPTION_URL");
		this.checkCurrentURL(driver, expectedURL, LOGGER);
	    getSouscription(driver, resultSet);

	    makePayment(driver, resultSet);
		
	   // WebElement el = driver.findElement(By.xpath("//div[@class='header-banner-right']/ul/li/a/i"));
	    //String expectedURL ="https://staging-store-rcibsp.demandware.net/s/RCI_PL/orderconfirmed";
	    expectedURL = properties.getProperty("BASE_URL") + properties.getProperty("COUNTRY_PL_URL")
	    					+ properties.getProperty("PAYMENT_CONFIRMATION_URL");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
	    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.urlContains(expectedURL));
	    
	    LOGGER.log(Level.INFO,"PAYMENT COMPLETED WITH SUCCESS!\n");
	    logout(driver);
	   
	}
	
	public void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		
		
		WebElement datepicker = driver.findElement(By.cssSelector("img.ui-datepicker-trigger"));
		datepicker.click();

	    String strCarDate = resultSet.get("Vehicle Insurance Date");
		Date dateVehicle =  sf.parse(strCarDate);
	 	c.setTime(dateVehicle);
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		
	    WebElement vinElement = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_tyreinsurance_vehicleInfoVIN"));
	    jse2.executeScript("arguments[0].scrollIntoView()", vinElement); 
		vinElement.clear();
	    vinElement.sendKeys(resultSet.get("VIN"));
	 
	    String registration = resultSet.get("Registration number");
	    WebElement plate1Element = driver.findElement(By.id("dwfrm_billing_subscriptionInformation_tyreinsurance_vehicleInfoPlateNo"));
	    plate1Element.clear();
	    plate1Element.sendKeys(registration);
	    
	  
	    driver.findElement(By.xpath("(//img[@alt='...'])[2]")).click();
	    
	    String strDate1 = resultSet.get("Date first registration");
	    Date dateVehicle1 =  sf.parse(strDate1);
	 	c.setTime(dateVehicle1);
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    driver.findElement(By.linkText(""+c.get(Calendar.DATE))).click();
		
	    By byDateProduction = By.id("dwfrm_billing_subscriptionInformation_tyreinsurance_vehicleModelYear");
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
	    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.presenceOfElementLocated(byDateProduction));
	 
	    WebElement criteriaConfirmationElement = driver.findElement(byDateProduction);
	    jse2.executeScript("arguments[0].scrollIntoView()", criteriaConfirmationElement); 
	    criteriaConfirmationElement.sendKeys(resultSet.get("Manufacturing Year"));
	    
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_tyreinsurance_vehicleInfoBrand")))
	    	.selectByValue(resultSet.get("brand"));
	    new Select(driver.findElement(By.id("dwfrm_billing_subscriptionInformation_tyreinsurance_vehicleInfoModel")))
	    	.selectByValue(resultSet.get("Model").toUpperCase());
	  
	    WebElement termsElement = driver.findElement(By.id("tyreinsurancepersonaldatalegaltext"));
	    jse2.executeScript("arguments[0].scroll(0, 500)", termsElement); 
	    
	    
	    By byTermConditions = By.cssSelector("input[id=\"dwfrm_billing_subscriptionInformation_tyreinsurance_termsconditions\"]");
	    WebElement termConditionsElement = driver.findElement( byTermConditions);
	    jse2.executeScript("arguments[0].scrollIntoView()", termConditionsElement); 
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
	    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.elementToBeClickable(termConditionsElement));
	    jse2.executeScript("arguments[0].click()", termConditionsElement); 
	    
	    driver.findElement(By.id("continue-to-place-order")).click();
		//logError(driver);
	}
	
	public void selectProduct(WebDriver driver, Map<String, String> resultSet) throws GUIException {
		 //driver.findElement(By.cssSelector("ul.menu-category.level-1 li:nth-of-type(2) a")).click();
		 
		 //driver.findElement(By.xpath("//div[@data-itemid='PL-P-TYREINSURANCE']")).click();
		 //By bySubProduct = By.cssSelector("button.add-all-to-cart.product-UBEZPIECZENIEOPON-Maxi");
		
		driver.findElement(By.xpath("//nav[@id='navigation']/div/ul/li[2]")).click(); 
		By bySubProduct = By.xpath("//nav[@id='navigation']/div/ul/li[2]/div/ul/li[4]/a");
		 new FluentWait<WebDriver>(driver)
		 	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(driver.findElement(bySubProduct)));
		 WebElement subProductElement = driver.findElement(bySubProduct);
		 //jse2.executeScript("arguments[0].scrollIntoView()", subProductElement);
		
		 subProductElement.click();
		 By byProductMini = By.xpath("//div[2]/form/div/button");
		 WebElement productMiniElement = driver.findElement(byProductMini);
		 jse2.executeScript("arguments[0].scrollIntoView()", productMiniElement);
		 productMiniElement.click();
		 
		 //driver.findElement(By.cssSelector("#ui-id-2 > div.actions > a.action.dialog-cart-show")).click();
		 driver.findElement(By.name("dwfrm_cart_checkoutCart")).click();
		 LOGGER.log(Level.INFO, "PRODUCT WAS SELECTED: OK!");
	}
	
	public void makePayment(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
		By byMakePayment = By.name("dwfrm_billing_save");
		 new FluentWait<WebDriver>(driver)
		    .withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		    .pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(byMakePayment));
		WebElement billingSaveElment = driver.findElement(byMakePayment);
		jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		
		
	    driver.findElement(By.id("is-WorldPay")).click();
	    
	    WebElement placeOrderElement = driver.findElement(By.id("placeOrder"));
	    jse2.executeScript("arguments[0].scrollIntoView()", placeOrderElement); 
	    placeOrderElement.click();
	    
	    driver.findElement(By.id("cardNumber")).clear();
	    //System.out.println(""+resultSet.get("Card number"));
	    driver.findElement(By.id("cardNumber")).sendKeys(resultSet.get("Card number"));
	    
	    new Select(driver.findElement(By.id("expiryMonth"))).selectByValue(String.format("%02d",
	    		Integer.parseInt(resultSet.get("Expiry Month"))));
		
	    new Select(driver.findElement(By.id("expiryYear"))).selectByValue(resultSet.get("Expiry Year"));
	   
	    driver.findElement(By.id("securityCode")).clear();
	    driver.findElement(By.id("securityCode")).sendKeys(resultSet.get("Security number"));
	    
	    WebElement submitElement = driver.findElement(By.id("submitButton"));
	    jse2.executeScript("arguments[0].scrollIntoView()", submitElement); 
	    submitElement.click();
	    //driver.findElement(By.id("submitButton")).click();
	    //logError(driver);
	    
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
		    
		    //logError(driver);
	}

	
	
}