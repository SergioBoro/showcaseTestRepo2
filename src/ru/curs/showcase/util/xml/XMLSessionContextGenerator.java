package ru.curs.showcase.util.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import org.slf4j.*;
import org.w3c.dom.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.SessionUtils;
import ru.curs.showcase.util.TextUtils;

/**
 * Класс, содержащий функции для получения информации о текущей сессией
 * приложения и создании XML документа, ее содержащего.
 * 
 * @author den
 * 
 */
public final class XMLSessionContextGenerator extends GeneralXMLHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLSessionContextGenerator.class);

	public static final String SESSION_CONTEXT_TAG = "sessioncontext";
	public static final String URL_PARAMS_TAG = "urlparams";
	public static final String URL_PARAM_TAG = "urlparam";
	public static final String USERDATA_TAG = ExchangeConstants.URL_PARAM_USERDATA;
	public static final String SID_TAG = "sid";
	public static final String FULLUSERNAME_TAG = "fullusername";
	public static final String EMAIL_TAG = "email";
	public static final String PHONE_TAG = "phone";

	private XMLSessionContextGenerator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Формирует контекст сессии в виде XML объекта.
	 * 
	 * @param aContext
	 *            - параметры.
	 * @return - строку с XML.
	 * @throws UnsupportedEncodingException
	 */
	public static String generate(final CompositeContext aContext)
			throws UnsupportedEncodingException {
		Document info = createXML();
		addUserNode(info);
		fillURLParams(info, aContext.getSessionParamsMap());
		addUserData(info, aContext.getSessionParamsMap());
		addRelatedContext(info, aContext.getRelated());
		String result = XMLUtils.documentToString(info);
		result = XMLUtils.xmlServiceSymbolsToNormal(result);
		LOGGER.debug("XMLSessionContextGenerator.generate()"
				+ System.getProperty("line.separator") + result);
		return result;
	}

	private static void addRelatedContext(final Document info,
			final Map<ID, CompositeContext> aRelated) {
		Element root = info.createElement(RELATED_TAG);
		info.getDocumentElement().appendChild(root);
		for (Entry<ID, CompositeContext> rc : aRelated.entrySet()) {
			Document doc = XMLUtils.objectToXML(rc.getValue());
			Element inserted = doc.getDocumentElement();
			Element child = (Element) info.importNode(inserted, true);
			child.setAttribute(ID_TAG, rc.getKey().getString());
			root.appendChild(child);
		}
	}

	private static Document createXML() {
		Document info =
			XMLUtils.createBuilder().getDOMImplementation()
					.createDocument("", SESSION_CONTEXT_TAG, null);
		return info;
	}

	private static void addUserNode(final Document info) {
		Element node = info.createElement(USERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(ru.curs.showcase.runtime.SessionUtils
				.getCurrentSessionUserName()));

		node = info.createElement(SID_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserSID()));

		node = info.createElement(EMAIL_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserEmail()));

		node = info.createElement(FULLUSERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserFullName()));

		node = info.createElement(PHONE_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserPhone()));
	}

	private static void
			addUserData(final Document info, final Map<String, ArrayList<String>> aMap) {
		Element node = info.createElement(USERDATA_TAG);
		info.getDocumentElement().appendChild(node);
		String value = null;
		if (aMap.get(ExchangeConstants.URL_PARAM_USERDATA) != null) {
			value =
				Arrays.toString(aMap.get(ExchangeConstants.URL_PARAM_USERDATA).toArray())
						.replace("[", "").replace("]", "");
		} else {
			value = ExchangeConstants.DEFAULT_USERDATA;
		}
		node.setTextContent(value);

	}

	/**
	 * Создает фрагмент XML документа с параметрами URL. Перекодировка ключа URL
	 * функцией URLDecoder.decode параметра необходима, т.к. в случае русских
	 * символов ключ содержит URL коды.
	 * 
	 */
	private static void fillURLParams(final Document info,
			final Map<String, ArrayList<String>> aMap) throws UnsupportedEncodingException {
		Element node;

		if (!aMap.isEmpty()) {
			node = info.createElement(URL_PARAMS_TAG);
			info.getDocumentElement().appendChild(node);

			for (Map.Entry<String, ArrayList<String>> entry : aMap.entrySet()) {
				if (!(ExchangeConstants.URL_PARAM_USERDATA.equals(entry.getKey()))) {
					Element child = info.createElement(URL_PARAM_TAG);
					node.appendChild(child);

					child.setAttribute(NAME_TAG,
							URLDecoder.decode(entry.getKey(), TextUtils.DEF_ENCODING));
					String value = "";
					if (entry.getValue() != null) {
						value = Arrays.toString(entry.getValue().toArray());
					}
					child.setAttribute(VALUE_TAG, value);
				}
			}
		}
	}
}
