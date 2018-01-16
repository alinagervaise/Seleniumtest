/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
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

import com.codeborne.selenide.Configuration;
import com.google.common.io.Files;

import staging.rcibsp.Country;
import staging.rcibsp.ExcelReader;
import staging.rcibsp.GUIException;
import staging.rcibsp.Loader;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.$;
import com.codeborne.selenide.Condition;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.Selectors.byText;
import com.codeborne.selenide.SelenideElement;

/**
 * @author galinabikoro
 *
 */
public class TestSouscriptionUKSeleniteMultiExcel {
	private WebDriver driver;
	private WebDriverWait wait;
	private final String BASE_URL = "https://staging-store-rcibsp.demandware.net";
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private java.text.SimpleDateFormat sf;
	private Calendar c;
	private JavascriptExecutor jse2;
	private DesiredCapabilities capabilities;
	 public static Logger LOGGER = Logger.getLogger(TestSouscriptionUKSeleniteMultiExcel.class.getName());  
	 public FileHandler fileHandler;  
	 String errorMessage = "";
	/**
	 * @throws java.lang.Exception
	 */
	enum DriverType{
		FIREFOX, CHROME,INTERNETEXPLORER
	};

	@Before
	public void setUp() throws Exception {
		
		 this.setDriver(DriverType.CHROME);
		 Configuration.baseUrl = BASE_URL;
		
		 sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		 c = Calendar.getInstance();
		 jse2 = (JavascriptExecutor)driver;
		 
		 java.text.SimpleDateFormat sf0 = new java.text.SimpleDateFormat("dd_MM_yyyy");
		 Date date = new Date(System.currentTimeMillis());
		 String currentDateStr = sf0.format(date);
		 String logFile = "errLog"+currentDateStr+".log";
		 fileHandler = new FileHandler( System.getProperty("user.dir")
				 					+"\\src\\errorScreenshots\\"+logFile, true);  
	     LOGGER.addHandler(fileHandler);
	     SimpleFormatter formatter = new SimpleFormatter();  
	     fileHandler.setFormatter(formatter);
	}
	
	private void setDriver( DriverType driverType){
		switch(driverType){
			case FIREFOX:
				 System.setProperty("webdriver.gecko.driver",  System.getProperty("user.dir")+"\\src\\SELENIUM_DRIVERS\\geckodriver-v0.19.1-win64\\geckodriver.exe");
				 Configuration.browser ="firefox";
			     break;
			case CHROME:
				 System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\src\\SELENIUM_DRIVERS\\chromedriver_win32\\chromedriver.exe");
				 Configuration.browser ="chrome";
				 break;	
			case INTERNETEXPLORER:
				 System.setProperty("webdriver.ie.driver",  System.getProperty("user.dir")
						 +"\\src\\SELENIUM_DRIVERS\\IEDriverServer_x64_3.8.0\\IEDriverServer.exe");
				 
				Configuration.browser ="ie";
				Configuration.browserVersion = "11";
			     break;
		}
		Configuration.headless = true;
		Configuration.screenshots = true;
		Configuration.startMaximized = true;
	    
	}
	


	  @Test
	  public void testCaseSouscriptionUserExist() throws IOException,ParseException, InterruptedException {
		  try{
			  ExcelReader objExcelFile = new ExcelReader();
			  String filePath = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeudetestFormated.xlsx";
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.readFile(filePath, Country.UK);
			  
			  for (Map m : result){
				  errorMessage = "";
				  if (m.isEmpty()){
					  continue;
				  }
				 
				  LOGGER.info("Execute Souscription for :\n"+ m +"\n");
				  try{
				    	runSelenium(m);
				  }
				  catch(GUIException e){
					  if (e != null){
				
						  generateLog(e);
					
					  LOGGER.log(Level.WARNING, e.getMessage());
					  logout(driver);
					  continue;
					  }
					  
				  }
				  catch(Exception ex){
					  LOGGER.log(Level.SEVERE, ex.getClass().getName()+ "   "+ex.getMessage());
					  generateLog(ex);
				  
				  }
				  
		  }
		  }catch(IOException ex){
			  generateLog(ex);
		  }
	   
	  }

	private void generateLog(Exception ex) throws IOException {
		File errFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		  java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("dd_MM_yyyy_HHmmss");
		  Date date = new Date(System.currentTimeMillis());
		  String currentDateStr = sf.format(date);
		  String outputPath = System.getProperty("user.dir")
				  	+"\\src\\errorScreenshots\\screenshot"
				  +currentDateStr+".png";
		  Files.copy( errFile, new File(outputPath));
		  
	}

	public void runSelenium(Map<String, String> resultSet) throws ParseException, InterruptedException, GUIException {
		open(BASE_URL + "/s/RCI_UK/");
		SelenideElement productElement = $(byText("KEY COVER"));
		$(productElement).click();
		
		WebElement addAllToCartElement = $(By.cssSelector("button.close-dialog.add-all-to-cart.dialog-cart-show"));
		if ($(addAllToCartElement).isDisplayed()){
			addAllToCartElement.click();
	    }
	    $(By.cssSelector("button.add-all-to-cart.product-0")).click();
	    $(By.cssSelector("a.action.dialog-cart-show")).click();
	    $(By.xpath("(//form[@id='checkout-form']/fieldset/button)[2]")).click();
	    
	    login(driver, resultSet);
	    
	    String URL = getWebDriver().getCurrentUrl();
	    if (URL.equalsIgnoreCase("https://staging-store-rcibsp.demandware.net/s/RCI_UK/shipping")){
	    	//user who never made a suscription
	    	enterUserInfo(driver, resultSet);
	    }
	   
	    SelenideElement billingSaveElement = $(byText("Continue to Place Order >"));//("button[name=\"dwfrm_billing_save\"]"));
	    System.out.println("BILLING SAVE    ======"+billingSaveElement.exists());
	    jse2 = (JavascriptExecutor)getWebDriver();
	    jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElement); 
	    System.out.println("BILLING SAVE    ======"+billingSaveElement.is(Condition.visible));
	    billingSaveElement.shouldBe(Condition.visible).click();
	    
	    
	    getSouscription(resultSet);
	    makePayment(resultSet);
		
	    WebElement el = $(By.xpath("//div[@class='header-banner-right']/ul/li/a/i"));
	    
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(30, TimeUnit.SECONDS)
	    .pollingEvery(2, TimeUnit.MILLISECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.urlToBe("https://staging-store-rcibsp.demandware.net/s/RCI_UK/orderconfirmed"));
	    //wait.until(ExpectedConditions.urlToBe("https://staging-store-rcibsp.demandware.net/s/RCI_UK/orderconfirmed"));
	    logout(driver);
	   
	}
	private void logout(WebDriver driver) {
		 $(By.xpath("//div[@class='header-banner-right']/ul/li/a/i")).click();
		 //if ($(By.linkText("KEY COVER")) != null){
		 new FluentWait<WebDriver>(driver)
		    .withTimeout(300, TimeUnit.SECONDS)
		    .pollingEvery(2, TimeUnit.MILLISECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(By.linkText("MY ACCOUNT")));
		    $(By.linkText("MY ACCOUNT")).click();
		    //.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='account-logout']/a")));
		    
		// }
		 $(By.xpath("//span[@class='account-logout']/a")).click();
	
	}
	private void getSouscription(Map<String, String> resultSet) throws ParseException, GUIException{
	    $(By.id("dwfrm_billing_subscriptionInformation_vin")).clear();
	    $(By.id("dwfrm_billing_subscriptionInformation_vin")).sendKeys(resultSet.get("VIN"));
	    $(By.id("dwfrm_billing_subscriptionInformation_plate")).clear();
	    $(By.id("dwfrm_billing_subscriptionInformation_plate")).sendKeys(resultSet.get("Registration"));
	    new Select($(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoBrand"))).selectByVisibleText(resultSet.get("Brand").toUpperCase());
	    new Select($(By.id("dwfrm_billing_subscriptionInformation_vehicleInfoModel"))).selectByVisibleText(resultSet.get("Model"));
	    
	    String strCarDate = resultSet.get("Vehicle Insurance Date");
		Date dateVehicle =  sf.parse(strCarDate);
	   
	 	c.setTime(dateVehicle);
	    WebElement datepicker = $(By.cssSelector("img.ui-datepicker-trigger"));
	    datepicker.click();
	   
	    new Select($(By.cssSelector("select.ui-datepicker-year"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
	    new Select($(By.cssSelector("select.ui-datepicker-month"))).selectByValue(Integer.toString(c.get(Calendar.MONTH)));
	    $(By.linkText(""+c.get(Calendar.DATE))).click();
	    
	    WebElement agreeTermsElment = $(By.id("dwfrm_billing_subscriptionInformation_agreeTerms"));
		jse2.executeScript("arguments[0].scrollIntoView()", agreeTermsElment); 
		
		agreeTermsElment.click();
		 logError(driver);
	}

	private void logError(WebDriver driver) throws GUIException {
		String errorMessage = getGUIError(driver);
		if ((errorMessage != null )&&(!errorMessage.isEmpty())){
			throw new GUIException(errorMessage);
		}
	}
	private void makePayment(Map<String, String> resultSet) throws ParseException, GUIException{
		WebElement billingSaveElment = $(By.name("dwfrm_billing_save"));
		//jse2.executeScript("arguments[0].scrollIntoView()", billingSaveElment); 
		billingSaveElment.click();
		
		$(By.cssSelector("button[value=\"Continue to Place Order >\"]")).click();
	    $(By.name("dwfrm_billing_paymentMethods_selectedPaymentMethodID")).click();
		
		WebElement paymentMethodElment = $(By.name("dwfrm_billing_paymentMethods_selectedPaymentMethodID"));
		jse2.executeScript("arguments[0].scrollIntoView()", paymentMethodElment); 
		paymentMethodElment.click();
	    
	    $(By.id("placeOrder")).click();
	    $(By.id("cardNumber")).clear();
	    $(By.id("cardNumber")).sendKeys(resultSet.get("Card number"));
	 
	    String strDate = resultSet.get("Expiry date");
		
		Date date =  sf.parse(strDate);
		
		c.setTime(date);
	    new Select($(By.id("expiryMonth"))).selectByValue(String.format("%02d", c.get(Calendar.MONTH)));
	    new Select($(By.id("expiryYear"))).selectByValue(Integer.toString(c.get(Calendar.YEAR)));
		
	    $(By.id("securityCode")).clear();
	    $(By.id("securityCode")).sendKeys(resultSet.get("Security number"));
	    $(By.id("submitButton")).click();
	    logError(driver);
	    
	}
	private void login(WebDriver driver, Map<String, String> resultSet) throws GUIException {
		$(By.xpath("//form[@id='dwfrm_login']/fieldset/div/div/input")).clear();
	    $(By.xpath("//form[@id='dwfrm_login']/fieldset/div/div/input")).sendKeys(resultSet.get("email"));
	    $(By.xpath("//form[@id='dwfrm_login']/fieldset/div[2]/div/input")).clear();

	    $(By.xpath("//form[@id='dwfrm_login']/fieldset/div[2]/div/input")).sendKeys(resultSet.get("password").trim());
	    WebElement loginElement = $(By.name("dwfrm_login_login")); 
	    loginElement.click();
	    logError(driver);
	}

	 
	  public void tearDown() throws Exception {
	    driver.quit();
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString) || (errorMessage != null)&& !errorMessage.isEmpty()) {
	    	LOGGER.log(Level.SEVERE, verificationErrorString);
	    	LOGGER.log(Level.SEVERE, errorMessage);
	        fail(verificationErrorString);
	    	
	    }
	  }
	private void enterUserInfo(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException{
			
		    new Select($(By.id("dwfrm_billing_title"))).selectByVisibleText("Miss");
		    java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("EEE, MM dd HH:mm:ss yyyy");
		    Calendar c = Calendar.getInstance();
		    
		    String strCarDate = resultSet.get("date of birth");
			Date dateVehicle =  sf.parse(strCarDate);
			c.setTime(dateVehicle);
		 
		    WebElement datepicker = $(By.cssSelector("img.ui-datepicker-trigger"));
		    datepicker.click();
		    
		    $(By.cssSelector("select.ui-datepicker-year")).selectOption(Integer.toString(c.get(Calendar.YEAR)));
		    $(By.cssSelector("select.ui-datepicker-month")).selectOptionByValue(Integer.toString(c.get(Calendar.MONTH)));
		    //wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(""+c.get(Calendar.DATE))));
		    $(By.linkText(""+c.get(Calendar.DATE))).click();
		   

		    $(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).clear();
		    $(By.id("dwfrm_billing_billingAddress_addressFields_mobilephone")).sendKeys(resultSet.get("phone number"));
		    $(By.id("dwfrm_billing_billingAddress_addressFields_address1")).clear();
		    $(By.id("dwfrm_billing_billingAddress_addressFields_address1")).sendKeys(resultSet.get("Property number / Name"));
		    $(By.id("dwfrm_billing_billingAddress_addressFields_address2")).clear();
		    $(By.id("dwfrm_billing_billingAddress_addressFields_address2")).sendKeys(resultSet.get("Street"));
		    $(By.id("dwfrm_billing_billingAddress_addressFields_postal")).clear();
		    $(By.id("dwfrm_billing_billingAddress_addressFields_postal")).sendKeys(resultSet.get("Postal Code"));
		    $(By.id("dwfrm_billing_billingAddress_addressFields_city")).clear();
		    $(By.id("dwfrm_billing_billingAddress_addressFields_city")).sendKeys(resultSet.get("City"));
		    
		    logError(driver);
	}
	private String getGUIErrorHelper(WebElement element) {
		String errorMessage = "";
		String msg = null;
		if (element != null){
			msg = element.getAttribute("value") ;
			if (msg != null){
				errorMessage += String.join("\n", msg);
			}
			msg = element.getText();
			if (msg != null){
				errorMessage += String.join("\n", msg);
			}
			msg = element.getAttribute("innerHTML") ;
			if (msg != null){
				errorMessage += String.join("\n", msg);
			}
			return errorMessage;
		}
		return errorMessage;
	}
	private String getGUIError(WebDriver driver) {
		errorMessage = "";
		if ($(By.xpath("//*[contains(@class, 'error')]")).exists() ){
			errorMessage += getGUIErrorHelper($(By.xpath("//*[contains(@class, 'error')]")) );
		}
		if ($(By.xpath("//span[contains(@class, 'error')]")).exists()){
			errorMessage += getGUIErrorHelper($(By.xpath("//span[contains(@class, 'error')]")));
		}
		if ($(By.xpath("//div[contains(@class, 'error')]")).exists()){
			errorMessage += getGUIErrorHelper($(By.xpath("//div[contains(@class, 'error')]")));
		}
		return errorMessage;
		}


}