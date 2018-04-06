package staging.rcibsp;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GUILogger {
	public static void raiseError(boolean errorMessageExist) throws GUIException {
		if (errorMessageExist){
			throw new GUIException();
		}
	}
	public static String logError(WebDriver driver, String xPath, String field, String successMessage,  Logger LOGGER) throws GUIException {
		String errorMessage = "";
		errorMessage = getGUIError(driver, xPath);
		if ((errorMessage != null )&&(!errorMessage.isEmpty())){
			LOGGER.log(Level.SEVERE, "ERROR on field: '"+ field + "' :"+errorMessage+ "\n");
		}
		else {
			errorMessage = "";
			LOGGER.log(Level.INFO, successMessage+ ": OK! \n");
		}
		return errorMessage;
	}
	public static String getGUIErrorHelper(WebElement element) {
		String errorMessage = "";
		String msg = null;
		if (element != null){
			msg = element.getAttribute("innerHTML") ;
			if (msg != null){
				errorMessage = String.join("\n", msg);
			}
			return errorMessage;
		}
		return errorMessage;
	}
	
	public static String getGUIError(WebDriver driver, String fieldXpath) {
		String errorMessage = "";
		String xPath = "";
		xPath = fieldXpath+"/following-sibling::span[contains(@class, 'error')]";
		if (driver.findElements(By.xpath(xPath)).size() >0){
			errorMessage = getGUIErrorHelper(driver.findElement(By.xpath(xPath)));
			if (errorMessage.isEmpty()){
				return errorMessage;
			}
		}
		xPath = fieldXpath+"/following-sibling::div[contains(@class, 'error')]";
		if (driver.findElements(By.xpath(xPath)).size() > 0){
			errorMessage = getGUIErrorHelper(driver.findElement(By.xpath(xPath)));
		}
		return errorMessage;
		}
	public static String getGUIError(WebDriver driver) {
		String errorMessage = "";
	
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

	
}
