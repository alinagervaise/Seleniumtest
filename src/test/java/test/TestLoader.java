package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import staging.rcibsp.ExcelReader;
import staging.rcibsp.Loader;


public class TestLoader {
	 public static Logger LOGGER = Logger.getLogger(TestLoader.class.getName());  
	@Test
	public void test() {
		Loader loader = new Loader();
		loader.setReader(new ExcelReader());
		String inputFolder = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeudetestFormated.xlsx";
		List<Map<String, String>> result = loader.readFile(inputFolder);
		for (Map<String, String>map : result){
			LOGGER.log(Level.INFO, map.toString());
			assertEquals(20, map.size());
		}
		assertEquals(3, result.size());
	}

}
