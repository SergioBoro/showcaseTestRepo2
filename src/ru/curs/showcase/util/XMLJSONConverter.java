package ru.curs.showcase.util;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.json.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.util.xml.XMLUtils;

import com.google.gson.JsonElement;

/**
 * Класс преобразования xml в JSON и обратно - JSON в xml.
 * 
 * @author bogatov
 * 
 * 
 */
public final class XMLJSONConverter {

	private XMLJSONConverter() {

	}

	/**
	 * Преобразования XML в JSON. Все атрибуты тега переносятся в атрибуты json,
	 * имена которох начинаются с префикса @. В случа если встречен тег sorted,
	 * то все подобные теги становятся элементами json массива с именем #sorted
	 * с сохранением порядка следования в xml. Если тег содержащий атрибуты,
	 * содержит также значение, то оно переносится в json в атрибут с именем
	 * #text
	 * 
	 * @param xml
	 *            - XML строка.
	 * @return строка в формате json.
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String xmlToJson(final String xml) throws SAXException, IOException {

		if (xml == null || xml.isEmpty()) {
			return "";
		}
		SAXParser parser = XMLUtils.createSAXParser();
		XMLToJSONConverterSaxHandler handler = new XMLToJSONConverterSaxHandler();
		// Данный код вставлен для разрешения случая, когда в xml-файле имеется
		// несколько корневых эелементов, тогда как на вход SaxParser
		// должен подаваться xml-файл только с одним корневым элементом.
		String newXml = "<tempRootForResolvingProblem>" + xml + "</tempRootForResolvingProblem>";
		InputStream in = TextUtils.stringToStream(newXml);
		parser.parse(in, handler);
		JsonElement result = handler.getResult();
		String str = result.toString();
		int ind = str.indexOf(':');
		str = str.substring(ind + 1);
		ind = str.lastIndexOf("}");
		str = str.substring(0, ind);
		return str;
	}

	/**
	 * Преобразование JSON в XML. Все атрибуты имена которох начинаются с
	 * префикса @ переносятся в xml в виде соответствующего атрибута тега. В
	 * случа если встречен атрибут с именем #sorted, то все дочернии элементы
	 * переносятся в xml с сохранением порядка элементов в json
	 * массиве(#sorted). В случа если встречен атрибут с именем #text, то его
	 * значение переносится как значение соответствующего (с именем
	 * родительского атрибута) тега.
	 * 
	 * @param json
	 *            - JSON строка
	 * @return строка в формате xml.
	 * @throws JSONException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static String jsonToXml(final String json) throws JSONException, TransformerException,
			ParserConfigurationException {
		// throw new NotImplementedYetException();
		JSONToXMLParser jtxParser = new JSONToXMLParser(json);
		String result = jtxParser.outPrint();
		return result;
	}

	/**
	 * Преобразования XML в JSONObject. Все атрибуты тега переносятся в атрибуты
	 * json, имена которох начинаются с префикса @. В случа если встречен тег
	 * sorted, то все подобные теги становятся элементами json массива с именем
	 * #sorted с сохранением порядка следования в xml. Если тег содержащий
	 * атрибуты, содержит также значение, то оно переносится в json в атрибут с
	 * именем #text
	 * 
	 * @param xml
	 *            - XML строка.
	 * @return объект JSONObject.
	 * @throws SAXException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject xmlToJsonObject(final String xml) throws JSONException, SAXException,
			IOException {
		String str = XMLJSONConverter.xmlToJson(xml);
		JSONObject jsonObj = new JSONObject(str);
		return jsonObj;
	}
}
