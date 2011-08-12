package ru.curs.showcase.app.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import org.w3c.dom.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Класс, содержащий функции для получения информации о текущей сессией
 * приложения и создании XML документа, ее содержащего.
 * 
 * @author den
 * 
 */
public final class SessionContextGenerator extends GeneralXMLHelper {

	private static final String SESSION_CONTEXT_TAG = "sessioncontext";
	public static final String USERNAME_TAG = "username";
	public static final String URL_PARAMS_TAG = "urlparams";
	public static final String URL_PARAM_TAG = "urlparam";
	public static final String USERDATA_TAG = ExchangeConstants.URL_PARAM_USERDATA;

	private SessionContextGenerator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Формирует контекст сессии в виде XML объекта.
	 * 
	 * 
	 * @return - строку с XML.
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param aContext
	 *            - параметры.
	 * @throws UnsupportedEncodingException
	 */
	public static String generate(final String sessionId, final CompositeContext aContext)
			throws UnsupportedEncodingException {
		Document info = createXML();
		addUserNode(info);
		fillURLParams(info, aContext.getSessionParamsMap());
		addUserData(info, aContext.getSessionParamsMap());
		addRelatedContext(info, aContext.getRelated());
		String result = XMLUtils.documentToString(info);
		result = XMLUtils.xmlServiceSymbolsToNormal(result);
		return result;
	}

	private static void addRelatedContext(final Document info,
			final Map<String, CompositeContext> aRelated) {
		Element root = info.createElement(RELATED_TAG);
		info.getDocumentElement().appendChild(root);
		for (Entry<String, CompositeContext> rc : aRelated.entrySet()) {
			Document doc = XMLUtils.objectToXML(rc.getValue());
			Element inserted = doc.getDocumentElement();
			Element child = (Element) info.importNode(inserted, true);
			child.setAttribute(ID_TAG, rc.getKey());
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
		node.appendChild(info.createTextNode(ServletUtils.getUserNameFromSession()));
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
			value = ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT;
		}
		node.setTextContent(value);

	}

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
