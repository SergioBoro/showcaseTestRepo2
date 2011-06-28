package ru.curs.showcase.app.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import org.w3c.dom.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Класс, содержащий функции для получения информации о текущей сессией
 * приложения и создании XML документа, ее содержащего.
 * 
 * @author den
 * 
 */
public final class SessionInfoGenerator extends GeneralXMLHelper {

	static final String SESSION_CONTEXT_TAG = "sessioncontext";
	public static final String USERNAME_TAG = "username";
	public static final String URL_PARAMS_TAG = "urlparams";
	public static final String URL_PARAM_TAG = "urlparam";
	public static final String USERDATA_TAG = "userdata";

	private SessionInfoGenerator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Формирует контекст сессии в виде XML объекта.
	 * 
	 * 
	 * @return - строку с XML.
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param aMap
	 *            - параметры.
	 * @throws UnsupportedEncodingException
	 */
	static String generateSessionContext(final String sessionId,
			final Map<String, ArrayList<String>> aMap) throws UnsupportedEncodingException {
		Document info =
			XMLUtils.createBuilder().getDOMImplementation()
					.createDocument("", SESSION_CONTEXT_TAG, null);

		Element node = info.createElement(USERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(ServletUtils.getUserNameFromSession()));

		fillURLParams(info, sessionId, aMap);

		addUserData(info, aMap);

		String result = XMLUtils.xsltTransform(info, null);
		return result;
	}

	private static void
			addUserData(final Document info, final Map<String, ArrayList<String>> aMap) {
		Element node = info.createElement(USERDATA_TAG);
		info.getDocumentElement().appendChild(node);
		String value = null;
		if ((aMap != null) && (aMap.get(AppProps.URL_PARAM_USERDATA) != null)) {
			value =
				Arrays.toString(aMap.get(AppProps.URL_PARAM_USERDATA).toArray()).replace("[", "")
						.replace("]", "");
		} else {
			value = ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT;
		}
		node.setTextContent(value);

	}

	private static void fillURLParams(final Document info, final String sessionId,
			final Map<String, ArrayList<String>> aMap) throws UnsupportedEncodingException {
		Element node;

		if ((aMap != null) && (!aMap.isEmpty())) {
			node = info.createElement(URL_PARAMS_TAG);
			info.getDocumentElement().appendChild(node);
			Iterator<String> iterator = aMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (!(AppProps.URL_PARAM_USERDATA.equals(key))) {
					Element child = info.createElement(URL_PARAM_TAG);
					node.appendChild(child);
					child.setAttribute(NAME_TAG, URLDecoder.decode(key, TextUtils.DEF_ENCODING));
					String value = "";
					if (aMap.get(key) != null) {
						value = Arrays.toString(aMap.get(key).toArray());
					}
					child.setAttribute(VALUE_TAG, value);
				}
			}
		}
	}
}
