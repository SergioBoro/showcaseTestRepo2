package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.util.XMLJSONConverter;

/**
 * Набор тестов класса XMLJSONConverter.
 * 
 * @author bogatov
 * 
 */
public class XMLJSONConverterTest {

	@Test
	public void xmlToJson() throws Exception {
		String xml =
			"<elem1 attr1=\"True\" attr0=\"\" attr3=\"Русский текст\" attr4=\"1\" attr5=\"3.14\" attr2=\"Text\">"
					+ "<elem2 sorted=\"True\">5</elem2>"
					+ "<elem3 sorted=\"True\">Снова текст</elem3>"
					+ "<elem4 sorted=\"True\" attr6=\"False\"/>"
					+ "<element5 attr7=\"1\"/><element5 attr8=\"2\"/>"
					+ "<element6 attr9=\"Ещё текст\"/>" + "<element7/>" + "</elem1>";
		String result = XMLJSONConverter.xmlToJson(xml);
		assertNotNull(result);
		assertEquals(
				result.toString(),
				"{\"elem1\":{\"element6\":{\"@attr9\":\"Ещё текст\"},"
						+ "\"element7\":\"None\","
						+ "\"@attr0\":\"None\","
						+ "\"element5\":[{\"@attr7\":\"1\"},{\"@attr8\":\"2\"}],"
						+ "\"@attr2\":\"Text\","
						+ "\"@attr1\":\"True\","
						+ "\"@attr4\":\"1\","
						+ "\"@attr3\":\"Русский текст\","
						+ "\"@attr5\":\"3.14\","
						+ "\"#sorted\":[{\"elem2\":\"5\"},{\"elem3\":\"Снова текст\"},{\"elem4\":{\"@attr6\":\"False\"}}]}}");
		// System.out.println(result.toString());
	}
}
