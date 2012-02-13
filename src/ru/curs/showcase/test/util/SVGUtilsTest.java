package ru.curs.showcase.test.util;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.SVGConvertor;
import ru.curs.showcase.util.exception.SVGConvertException;

/**
 * Тесты для модуля SVGUtils.
 * 
 * @author den
 * 
 */
public class SVGUtilsTest extends AbstractTestWithDefaultUserData {

	private static final String TMP = "tmp/";
	private static final String GEOMAP_SVG = "geomap.svg";
	private static final String ROOT = "ru\\curs\\showcase\\test\\";

	@Test
	public void testSVGFileToJPGFile() throws IOException {
		String inputFile = ROOT + GEOMAP_SVG;
		String outputFile = TMP + "geomap.jpg";
		File file = new File(outputFile);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToJPGFile(inputFile, outputFile);
	}

	@Test(expected = SVGConvertException.class)
	public void testSVGFileToJPGFileException() {
		String inputFile = ROOT + "ShowcaseDataGrid_test.css";
		String outputFile = TMP + "geomap.jpg";
		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToJPGFile(inputFile, outputFile);
	}

	@Test
	public void testSVGFileToPNGFile() throws IOException {
		String inputFile = ROOT + GEOMAP_SVG;
		String outputFile = TMP + "geomap.png";
		File file = new File(outputFile);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToPNGFile(inputFile, outputFile);
	}

	@Test(expected = SVGConvertException.class)
	public void testSVGFileToPNGFileException() {
		String inputFile = ROOT + "ShowcaseDataGrid_test.css";
		String outputFile = TMP + "geomap.png";
		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToPNGFile(inputFile, outputFile);
	}

}
