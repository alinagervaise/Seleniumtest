package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import staging.rcibsp.Country;
import staging.rcibsp.ExcelReader;
import staging.rcibsp.Loader;


public class TestLoader {
	 public static Logger LOGGER = Logger.getLogger(TestLoader.class.getName());  
	@Test
	public void testUK() {
		Loader loader = new Loader();
		loader.setReader(new ExcelReader());
		String inputFolder = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeu_de_test_UK.xlsx";
		List<Map<String, String>> result = loader.readFile(inputFolder, Country.UK);
		for (Map<String, String>map : result){
			LOGGER.log(Level.INFO, map.toString());
			assertEquals(20, map.size());
		}
		assertEquals(3, result.size());
	}
	@Test
	public void testDE() {
		Loader loader = new Loader();
		loader.setReader(new ExcelReader());
		String inputFolder = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeu_de_test_DE.xlsx";
		List<Map<String, String>> result = loader.readFile(inputFolder, Country.DE);
		for (Map<String, String>map : result){
			LOGGER.log(Level.INFO, map.toString());
			assertEquals(21, map.size());
		}
		assertEquals(1, result.size());
	}
	@Test
	public void testPL() {
		Loader loader = new Loader();
		loader.setReader(new ExcelReader());
		String inputFolder = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeu_de_test_PL.xlsx";
		List<Map<String, String>> result = loader.readFile(inputFolder, Country.PL);
		for (Map<String, String>map : result){
			LOGGER.log(Level.INFO, map.toString());
			assertEquals(23, map.size());
		}
		assertEquals(1, result.size());
	}
	@Test
	public void testBR() {
		Loader loader = new Loader();
		loader.setReader(new ExcelReader());
		String inputFolder = System.getProperty("user.dir")+"\\src\\excelExportAndFileIO\\jeu_de_test_BR.xlsx";
		List<Map<String, String>> result = loader.readFile(inputFolder, Country.BR);
		for (Map<String, String>map : result){
			LOGGER.log(Level.INFO, map.toString());
			assertEquals(28, map.size());
		}
		assertEquals(1, result.size());
	}
}
