/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.io.Files;

import staging.rcibsp.Country;
import staging.rcibsp.ExcelReader;
import staging.rcibsp.Loader;


/**
 * @author galinabikoro
 *
 */
public class TestSouscriptionUKMultiExcelFantome {
	private WebDriver driver;
	private WebDriverWait wait;
	private final String BASE_URL = "https://staging-store-rcibsp.demandware.net";
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private java.text.SimpleDateFormat sf;
	private Calendar c;
	private JavascriptExecutor jse2;
	private String phantomJsBinaryPath;
	public static Logger LOGGER = Logger.getLogger(TestSouscriptionUKMultiExcelFantome.class.getName());  
	public FileHandler fileHandler;  
	private String errorMessage = "";
	/**
	 * @throws java.lang.Exception
	 */
	enum DriverType{
		FIREFOX, CHROME, PHANTOMEJS, HTMLUNITDRIVER
	};
	
	@Before
	public void setUp() throws Exception {
		
		 driver = this.getDriver(DriverType.PHANTOMEJS, true);
		 //driver = this.getDriver(DriverType.FIREFOX, true);
		 //driver = this.getDriver(DriverType.PHANTOMEJS, true);
		 //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		 driver.manage().timeouts().implicitlyWait(600, TimeUnit.SECONDS);
		 wait = new WebDriverWait(driver, 1);
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
	
	private WebDriver getDriver( DriverType driverType, boolean headless){
		WebDriver _Driver = null;
		switch(driverType){
			case FIREFOX:
				 System.setProperty("webdriver.gecko.driver", "C:\\SELENIUM_DRIVERS\\geckodriver-v0.19.1-win64\\geckodriver.exe");
				 
				 if (headless){
					 FirefoxBinary firefoxBinary = new FirefoxBinary();
					 firefoxBinary.addCommandLineOptions("--headless");
					 FirefoxOptions firefoxOptions = new FirefoxOptions();
					 firefoxOptions.setBinary(firefoxBinary);
					 _Driver =  new FirefoxDriver(firefoxOptions);	 
				 }
				 else{
					DesiredCapabilities capabilities=DesiredCapabilities.firefox();
					capabilities.setCapability("marionette", false);
					_Driver = new FirefoxDriver(capabilities);
					_Driver.manage().window().maximize();
				 }
				 
			     break;
			case CHROME:
				 System.setProperty("webdriver.chrome.driver", "C:\\SELENIUM_DRIVERS\\chromedriver_win32\\chromedriver.exe");
				 ChromeOptions options = new ChromeOptions();
				 if (headless){
					 options.addArguments("headless");
				 }
				 else{
					 options.addArguments("--start-maximized");
				 }
				 _Driver = new ChromeDriver(options);
				 break;	
			case HTMLUNITDRIVER:
				//_Driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_11);
				_Driver = new HtmlUnitDriver();
				
				break;
			case PHANTOMEJS:
				String path = "C:\\SELENIUM_DRIVERS\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
				 phantomJsBinaryPath = System.setProperty("phantomjs.binary.path", path);
				 DesiredCapabilities caps = new DesiredCapabilities();
			     caps.setCapability(
			                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
			                phantomJsBinaryPath);
			     
			     caps.setJavascriptEnabled(true);
			     
			     ArrayList<String> cliArgsCap = new ArrayList();
			     cliArgsCap.add("--web-security=false");
			     cliArgsCap.add("--ssl-protocol=any");
			     cliArgsCap.add("--ignore-ssl-errors=true");
			     cliArgsCap.add("--webdriver-loglevel=ERROR");

			     caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
				 caps.setCapability("takesScreenshot", true);
			     _Driver = new PhantomJSDriver(caps);
			   _Driver.manage().window().maximize();
			
			     
				
		}
		return _Driver;
	}
	


	  @Test
	  public void testCaseSouscriptionUserExist() throws Exception {
		  try{
			  ExcelReader objExcelFile = new ExcelReader();
			  String filePath = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO";
			  Loader loader = new Loader();
			  loader.setReader(new ExcelReader());
			  List<Map<String, String>> result = loader.read(filePath, Country.UK);
			  
			  for (Map m : result){
				  if (m.isEmpty()){
					  continue;
				  }
			    System.out.println(m);
			    runSelenium(m);
			     //break;
			    	
			  }
		  }catch(Exception ex){
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
		  LOGGER.log(Level.SEVERE, ex.getMessage());
	}

	public void runSelenium(Map<String, String> resultSet) throws ParseException, InterruptedException {
		driver.get(BASE_URL + "/s/RCI_UK/");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("KEY COVER")));
	    driver.findElement(By.linkText("KEY COVER")).click();
	    //By byCookiesElement = By.xpath("//div[@class='cookie-banner']/button");
	    /*
	    By byCookiesElement = By.id("deleteMeetingClose");
	    if (driver.findElements(byCookiesElement).size() > 0){
	    	System.out.println("TRY TO ACCEPT COOKIES");
	    	driver.findElement(byCookiesElement).click();
	    	 System.out.println("ACCEPTING COOKIES: "+ driver.getCurrentUrl());
	    }
	    */
	    System.out.println("CURRENT PAGE1: "+ driver.getCurrentUrl());
	    driver.findElement(By.cssSelector("button.add-all-to-cart.product-0")).click();
	    System.out.println("CURRENT PAGE2: "+ driver.getCurrentUrl());
	    driver.findElement(By.cssSelector("a.action.dialog-cart-show")).click();
	    System.out.println("CURRENT PAGE3: "+ driver.getCurrentUrl());
	    
	    By byElement = By.name("dwfrm_cart_checkoutCart");
	    
	    try{
	    	wait.until(ExpectedConditions.presenceOfElementLocated(byElement));
	    	WebElement checkoutElement = driver.findElement(byElement);
	    	jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement);
	    	//jse2.executeScript("arguments[0].click()", checkoutElement);
	    	checkoutElement.click();
	    }
	    catch(Exception ex) {
	    	LOGGER.log(Level.SEVERE,  ex.getMessage());
	    	try{
	    		By byElement1 = By.cssSelector("button[name=\"dwfrm_cart_checkoutCart\"]");
	    		wait.until(ExpectedConditions.presenceOfElementLocated(byElement1));
		    	WebElement checkoutElement = driver.findElement(byElement1);
		    	jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement);
		    	checkoutElement.click();
	    	}
	    	catch(Exception ex1) {
	    		LOGGER.log(Level.SEVERE,  ex1.getMessage());
	    		try{
		    		By byElement2 = By.xpath("//form[@id='checkout-form']/fieldset/button");
		    		wait.until(ExpectedConditions.presenceOfElementLocated(byElement2));
			    	WebElement checkoutElement = driver.findElement(byElement2);
			    	jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement);
			    	checkoutElement.click();
	    		}
	    		catch(Exception ex2){
	    			try{
	    				LOGGER.log(Level.SEVERE,  ex2.getMessage());
		    			By byElement3 = By.xpath("//button[@name='dwfrm_cart_checkoutCart'])[2]");
		    			wait.until(ExpectedConditions.presenceOfElementLocated(byElement3));
				    	WebElement checkoutElement = driver.findElement(byElement3);
				    	jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement);
				    	checkoutElement.click();
	    			}
	    			catch(Exception ex3){
		    			LOGGER.log(Level.SEVERE,  ex3.getMessage());
		    			By byElement4 = By.xpath("//div[2]/form/fieldset/button");
		    			wait.until(ExpectedConditions.presenceOfElementLocated(byElement4));
				    	WebElement checkoutElement = driver.findElement(byElement4);
				    	jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement);
				    	checkoutElement.click();
	    			}
	    		}
	    	}
	
	    }
	    /*
	     *  WebElement checkoutElement =driver.findElement(By.xpath("//form[@id='checkout-form']/fieldset/button"));
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(120, TimeUnit.SECONDS)
	    .pollingEvery(2, TimeUnit.MILLISECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.presenceOfElementLocated(
	    		By.cssSelector("button[name=\"dwfrm_cart_checkoutCart\"]")));
	    
	    WebElement checkoutElement = driver.findElement(By.cssSelector("button[name=\"dwfrm_cart_checkoutCart\"]"));
		jse2.executeScript("arguments[0].scrollIntoView()", checkoutElement); 
		*/
	    //driver.findElement(By.cssSelector("button[name=\"dwfrm_cart_checkoutCart\"]")).click();
	    //driver.findElement(By.xpath("//form[@id='checkout-form']/fieldset/button)[2]")).click();
	    
	  
	    System.out.println("CURRENT PAGE4: "+ driver.getCurrentUrl());
	    login(driver, resultSet);
	    
	    String URL = driver.getCurrentUrl();
	    if (URL.equalsIgnoreCase("https://staging-store-rcibsp.demandware.net/s/RCI_UK/shipping")){
	    	//user who never made a suscription
	    	 System.out.println("CURRENT PAGE5: "+ driver.getCurrentUrl());
	    	enterUserInfo(driver, resultSet);
	    }
	    System.out.println("CURRENT PAGE6: "+ driver.getCurrentUrl());
	    getSouscription(driver, resultSet);
	    
	    System.out.println("CURRENT PAGE7: "+ driver.getCurrentUrl());
	    makePayment(driver, resultSet);
		
	    WebElement el = driver.findElement(By.xpath("//div[@class='header-banner-right']/ul/li/a/i"));
	    System.out.println("CURRENT PAGE8: "+ driver.getCurrentUrl());
	    new FluentWait<WebDriver>(driver)
	    .withTimeout(30, TimeUnit.SECONDS)
	    .pollingEvery(2, TimeUnit.MILLISECONDS)
	    .ignoring(WebDriverException.class)
	    .until(ExpectedConditions.urlToBe("https://staging-store-rcibsp.demandware.net/s/RCI_UK/orderconfirmed"));
	    //wait.until(ExpectedConditions.urlToBe("https://staging-store-rcibsp.demandware.net/s/RCI_UK/orderconfirmed"));
	    System.out.println("CURRENT PAGE9: "+ driver.getCurrentUrl());
	    logout(driver, resultSet);
	    System.out.println("CURRENT PAGE10: "+ driver.getCurrentUrl());
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

	 
	  public void tearDown() throws Exception {
	    driver.quit();
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) {
	    	LOGGER.log(Level.SEVERE, verificationErrorString);
	        fail(verificationErrorString);
	    	
	    }
	  }
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
		    // By.xpath("//*[contains(@class, 'error')]")
		    	//System.out.println("CLASS ERROR"+driver.findElement(By.xpath("//*[contains(@class, 'error')]")).getText());
		   // }
		   
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
		    
		   
		    new FluentWait<WebDriver>(driver)
		    .withTimeout(30, TimeUnit.SECONDS)
		    .pollingEvery(2, TimeUnit.MILLISECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[name=\"dwfrm_billing_save\"]")));
		   
		    WebElement billSaveElement = driver.findElement(By.cssSelector("button[name=\"dwfrm_billing_save\"]"));
		    jse2.executeScript("arguments[0].click()", billSaveElement ); 
		    //wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[name=\"dwfrm_billing_save\"]"))).click();
		    //driver.findElement(By.cssSelector("button[name=\"dwfrm_billing_save\"]")).click();
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
		if (driver.findElements(By.xpath("//*[contains(@class, 'error')]")).size() > 0 ){
			errorMessage += getGUIErrorHelper(driver.findElement(By.xpath("//*[contains(@class, 'error')]")) );
		}
		if (driver.findElements(By.xpath("//span[contains(@class, 'error')]")).size() >0){
			errorMessage += getGUIErrorHelper(driver.findElement(By.xpath("//span[contains(@class, 'error')]")));
		}
		if (driver.findElements(By.xpath("//div[contains(@class, 'error')]")).size() > 0){
			errorMessage += getGUIErrorHelper(driver.findElement(By.xpath("//div[contains(@class, 'error')]")));
		}
		return errorMessage;
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