package ru.curs.showcase.test;

import java.io.*;

import org.junit.Test;

import ru.curs.showcase.util.SVGConvertor;

/**
 * Тесты для модуля SVGUtils.
 * 
 * @author den
 * 
 */
public class SVGUtilsTest extends AbstractTestWithDefaultUserData {

	@Test
	public void testSVGFileToJPEGFile() throws IOException {
		String pathToData = "ru\\curs\\showcase\\test\\";
		String inputFile = pathToData + "geomap.svg";
		String outputFile = "tmp/" + "geomap.jpg";
		File file = new File(outputFile);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToJPEGFile(inputFile, outputFile);
	}

	@Test
	public void testSVGFileToPNGFile() throws IOException {
		String pathToData = "ru\\curs\\showcase\\test\\";
		String inputFile = pathToData + "geomap.svg";
		String outputFile = "tmp/" + "geomap.png";
		File file = new File(outputFile);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		SVGConvertor convertor = new SVGConvertor();
		convertor.svgFileToPNGFile(inputFile, outputFile);
	}

}
