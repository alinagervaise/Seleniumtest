package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

import staging.rcibsp.ConstantUtils;
import staging.rcibsp.GUIException;
import staging.rcibsp.GUILogger;

public class TestBase {
	protected WebDriver driver;
	protected WebDriverWait wait;
	protected final String BASE_URL = "https://staging-store-rcibsp.demandware.net";
	protected java.text.SimpleDateFormat sf;
	protected Calendar c;
	protected JavascriptExecutor jse2;
	protected DesiredCapabilities capabilities;
	protected static Logger LOGGER = Logger.getLogger(TestSouscriptionBRMultiExcel.class.getName());  
	protected FileHandler fileHandler;  
	protected String errorMessage = "";
	protected boolean isTestPassed = true;
	protected String captureFilePath = "";
	protected Properties properties;
    
	
	
	public void setHandler(String logFileName) throws SecurityException, IOException{
		 
		 java.text.SimpleDateFormat sf0 = new java.text.SimpleDateFormat("dd_MM_yyyy");
		 Date date = new Date(System.currentTimeMillis());
		 String currentDateStr = sf0.format(date);
		 String logFile = logFileName +currentDateStr+".log";
		 String handlerLogFile = String.join(FileSystems.getDefault().getSeparator(),
				 						ConstantUtils.SCREENHOT_FOLDER_PATH, logFile);
		 fileHandler = new FileHandler( handlerLogFile, true);  
	     LOGGER.addHandler(fileHandler);
	     SimpleFormatter formatter = new SimpleFormatter();  
	     fileHandler.setFormatter(formatter);
	}
	public void setProperties(){
		properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(ConstantUtils.CONFIG_PATH);
			properties.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setCaptureFile(String filename){
		  java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("dd_MM_yyyy_HHmmss");
		  Date date = new Date(System.currentTimeMillis());
		  String currentDateStr = sf.format(date);
		  String screenshotFile = filename +currentDateStr+".png";
		  captureFilePath = String.join(FileSystems.getDefault().getSeparator(),
				  							ConstantUtils.SCREENHOT_FOLDER_PATH,
				  							screenshotFile);
	}

	public void takeScreenshot(Exception ex) throws IOException {
		File errFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		Files.copy( errFile, new File(captureFilePath));
		  
	}

	
	public void tearDown() throws Exception {
		driver.quit();
		if (!isTestPassed) {
			fail("TEST FAILED PLEASE CHECK LOG FOR DETAILS.\n");
		}
		
	}
	
	public void checkCurrentURL(WebDriver driver, String expectedURL, Logger LOGGER) {
		String URL = driver.getCurrentUrl();
		LOGGER.log(Level.INFO, "NOW ON PAGE:"+ URL + "\n");
	    LOGGER.log(Level.INFO, "EXPECTED PAGE IS:"+ expectedURL+"\n");
		//Assert.assertTrue(URL.startsWith(expectedURL));
		Assert.assertTrue(URL.equals(expectedURL) ||URL.startsWith(expectedURL));
	    LOGGER.log(Level.INFO, "URL OK"+ "\n");
	}
	

	public void logout(WebDriver driver) {
		try{
			removeProduct(driver);
		}
		catch(Exception ex){
			 LOGGER.log(Level.INFO,"FAIL TO REMOVE PRODUCT"+ ex.getMessage()+" \n");
		}
	    LOGGER.log(Level.INFO,"ATTEMPT TO LOGOUT THE CURRENT USER \n");
		//String xPath = "//div[@class='header-banner-right']/ul/li/a";
		String xPath = "//div[@id='wrapper']/div/div/div[2]/ul/li/a";
//		 new FluentWait<WebDriver>(driver)
//		    .withTimeout(300, TimeUnit.SECONDS)
//		    .pollingEvery(2, TimeUnit.MILLISECONDS)
//		    .ignoring(WebDriverException.class)
//		    .until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
		 new FluentWait<WebDriver>(driver)
		  	.withTimeout(Integer.parseInt(properties.getProperty("EXPLICIT_WAIT_TIME")), TimeUnit.MINUTES)
		  	.pollingEvery(Integer.parseInt(properties.getProperty("POOLING_TIME")), TimeUnit.SECONDS)
		    .ignoring(WebDriverException.class)
		    .until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
		 
		
		 driver.findElement(By.xpath(xPath)).click();
		// xPath = "//span[@class='account-logout']/a";
		 if (driver.findElements(By.xpath(xPath)).size() > 0){
			 WebElement accountElement = driver.findElement(By.xpath(xPath));
			 accountElement.click();
			 LOGGER.log(Level.INFO,"LOG OUT : OK\n");
		 }
	}
	public void removeProduct(WebDriver driver) {
	    LOGGER.log(Level.INFO,"APTEMPT TO REMOVE PRODUCT \n");
		//String xPath = "//div[@id='mini-cart']/div/a";
		String xPath = "//div[@id='mini-cart']";
		WebElement cartElement = driver.findElement(By.id("mini-cart"));
		jse2.executeScript("arguments[0].scrollIntoView()", cartElement);
		cartElement.click();
		 
		xPath = "//button[@name='dwfrm_cart_shipments_i0_items_i0_deleteProduct']";
		if (driver.findElements(By.xpath(xPath)).size() > 0){
			WebElement removeProductElement = driver.findElement(By.xpath(xPath));
			removeProductElement.click();
			LOGGER.log(Level.INFO,"REMOVE PRODUCT DONE : OK\n");
		}
		
	}
	public void login(WebDriver driver, String expectedURL, Map<String, String> resultSet) throws GUIException {
		String errorMessage = "";
		String successMessage = "";
		String xPath = "";
		boolean errorExist = false;
	
		String URL = driver.getCurrentUrl();
	    Assert.assertTrue(URL.startsWith(expectedURL));
	    LOGGER.log(Level.INFO, "NOW ON PAGE:"+ URL + "\n");
	    
		String field = "Username";
		successMessage = "SET  '"+ field +"' VALUE TO : " + resultSet.get("email");
		xPath = "//form[@id='dwfrm_login']/fieldset/div/div/div/div/input";
		WebElement loginElement = driver.findElement(By.xpath(xPath));
		loginElement.clear();
	    loginElement.sendKeys(resultSet.get("email"));
//	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
//	    errorExist  |= !(errorMessage.isEmpty() );
//	    
	    field = "Password";
	    successMessage ="SET '"+ field +"' VALUE TO: " + resultSet.get("password");
	    xPath = "//form[@id='dwfrm_login']/fieldset/div/div[2]/div/div/input";
	    WebElement passwordElement = driver.findElement(By.xpath(xPath));
	    passwordElement.clear();
	    passwordElement.sendKeys(resultSet.get("password"));
//	    errorMessage = GUILogger.logError(driver, xPath, field, successMessage, LOGGER);
//	    errorExist  |= !(errorMessage.isEmpty());

	    field = "Login";
	    WebElement loginButtonElement = driver.findElement(By.name("dwfrm_login_login"));
	    jse2.executeScript("arguments[0].scrollIntoView()", loginButtonElement); 
	    loginButtonElement.click();
	    GUILogger.raiseError(errorExist);
	    LOGGER.log(Level.INFO, "CLICK ON BUTTON '"+ field +"'OK! \n");
	 
	}

	
	

}
