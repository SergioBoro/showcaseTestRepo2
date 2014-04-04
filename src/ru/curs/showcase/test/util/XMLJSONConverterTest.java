package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.util.XMLJSONConverter;

/**
 * Набор тестов класса XMLJSONConverter.
 * 
 * @author bogatov
 * 
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

	@Test
	public void jsonToXml() throws Exception {
		String json =
			"{\"elem1\":"
					+ "{\"@attr0\":None,"
					+ "\"@attr1\":True,"
					+ "\"@attr2\":\"Text\","
					+ "\"@attr3\":u\"Русский текст\","
					+ "\"@attr4\":1,"
					+ "\"@attr5\":3.14,"
					+ "\"#text\":u\"Тоже текст\","
					+ "\"#sorted\":[{\"elem2\":5},"
					+ "{\"element222\":[True, None]},"
					+ "{\"elem3\":u\"Снова текст\"},"
					+ "{\"element33\":False},"
					+ "{\"element333\":None},"
					+ "{\"element44\":[{\"@attr74\":1, \"#text\": \"Test2\"},"
					+ " {\"@attr84\":2, \"@attr94\":None, \"@attr64\":\"True\"}]},"
					+ "{\"element444\":[111, 444]},"
					+ "{\"elem4\":{"
					+ "\"@attr6\":False, \"@attr10\":\"False\", \"@attr11\":\"true\", \"@attr12\":\"None\""
					+ "}" + "}]," + "\"element7\":None,"
					+ "\"element5\":[{\"@attr7\":1}, {\"@attr8\":2, \"#text\": \"Test3\"}],"
					+ "\"element555\":[None, False],"
					+ "\"element6\":{\"@attr9\":u\"Ещё текст\"}," + "\"element8\":True" + "}"
					+ "}";

		String result = XMLJSONConverter.jsonToXml(json);
		// System.out.println(result);

		assertNotNull(result);
		assertEquals(
				result.toString(),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<elem1 attr0=\"\" attr1=\"True\" attr2=\"Text\" attr3=\"Русский текст\" attr4=\"1\" attr5=\"3.14\">"
						+ "\r\nТоже текст\r\n"
						+ "<elem2 sorted=\"True\">5</elem2>\r\n"
						+ "<element222 sorted=\"True\">True</element222>\r\n"
						+ "<element222 sorted=\"True\"/>\r\n"
						+ "<elem3 sorted=\"True\">Снова текст</elem3>\r\n"
						+ "<element33 sorted=\"True\">False</element33>\r\n"
						+ "<element333 sorted=\"True\"/>\r\n"
						+ "<element44 attr74=\"1\" sorted=\"True\">Test2</element44>\r\n"
						+ "<element44 attr64=\"True\" attr84=\"2\" attr94=\"\" sorted=\"True\"/>\r\n"
						+ "<element444 sorted=\"True\">111</element444>\r\n"
						+ "<element444 sorted=\"True\">444</element444>\r\n"
						+ "<elem4 attr10=\"False\" attr11=\"true\" attr12=\"\" attr6=\"False\" sorted=\"True\"/>\r\n"
						+ "<element8>True</element8>\r\n" + "<element6 attr9=\"Ещё текст\"/>\r\n"
						+ "<element7/>\r\n" + "<element5 attr7=\"1\"/>\r\n"
						+ "<element5 attr8=\"2\">Test3</element5>\r\n" + "<element555/>\r\n"
						+ "<element555>False</element555>\r\n" + "</elem1>");

	}
}
