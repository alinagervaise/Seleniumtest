package staging.rcibsp;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;



public class WebDriverFactory {
	
	
	public  WebDriver getDriver( DriverType driverType){
		WebDriver _Driver = null;
		DesiredCapabilities capabilities;
		String osName = System.getProperty("os.name");
		switch(driverType){
			case FIREFOX:
				if (osName.toLowerCase().contains("windows")){
					 System.setProperty("webdriver.gecko.driver", ConstantUtils.FIREFOX_DRIVER_PATH_WIN);
				}
				else{
					System.setProperty("webdriver.gecko.driver", ConstantUtils.FIREFOX_DRIVER_PATH_LINUX);
				}
				 capabilities=DesiredCapabilities.firefox();
				 capabilities.setCapability("marionette", false);
			     _Driver = new FirefoxDriver(capabilities);
			  
			     break;
			case CHROME:
				if (osName.toLowerCase().contains("windows")){
					System.setProperty("webdriver.chrome.driver", ConstantUtils.CHROME_DRIVER_PATH_WIN);
				}
				else{
					System.setProperty("webdriver.chrome.driver", ConstantUtils.CHROME_DRIVER_PATH_LINUX);
				}
				 ChromeOptions options = new ChromeOptions();
				 options.addArguments("--headless");
				 options.addArguments("--start-maximized");
				 _Driver = new ChromeDriver(options);
				 break;	
			case INTERNETEXPLORER:
				if (osName.toLowerCase().contains("windows")){
				 System.setProperty("webdriver.ie.driver", ConstantUtils.IE_DRIVER_PATH_WIN);
				}
				else{
					System.setProperty("webdriver.ie.driver", ConstantUtils.IE_DRIVER_PATH_LINUX);
				}
				 capabilities = DesiredCapabilities.internetExplorer();
				 capabilities.setCapability(InternetExplorerDriver.
						 INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
				 capabilities.setVersion("11");
			     _Driver = new InternetExplorerDriver(capabilities);
			    
			     break;
			case HTMLUNITDRIVER:
				//_Driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_11);
				_Driver = new HtmlUnitDriver();
				
				break;
			case PHANTOMEJS:
				String phantomJsBinaryPath = System.setProperty("phantomjs.binary.path",
										ConstantUtils.FANTOME_DRIVER_PATH_WIN);
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

}
