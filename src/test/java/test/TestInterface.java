package test;

import java.text.ParseException;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import staging.rcibsp.GUIException;

public interface TestInterface {
	public void runSelenium(Map<String, String> resultSet) throws ParseException, InterruptedException, GUIException;
	public void getSouscription(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException;
	public void removeProduct(WebDriver driver);
	public void selectProduct(WebDriver driver, Map<String, String> resultSet) throws GUIException, ParseException;
	public void makePayment(WebDriver driver, Map<String, String> resultSet) throws ParseException, GUIException;
	public void logout(WebDriver driver);
	public void login(WebDriver driver, String url, Map<String, String> resultSet) throws GUIException ;
}
