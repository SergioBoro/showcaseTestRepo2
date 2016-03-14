package ru.curs.showcase.util.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.*;

import org.slf4j.*;
import org.w3c.dom.*;
import org.w3c.dom.Element;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.oauth.Oauth2Token;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.UTF8Exception;

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
	private static final String URL_PARAMS_TAG = "urlparams";
	private static final String SESSIONID_TAG = "sessionid";
	private static final String URL_PARAM_TAG = "urlparam";
	private static final String SID_TAG = "sid";
	private static final String FULLUSERNAME_TAG = "fullusername";
	private static final String EMAIL_TAG = "email";
	private static final String PHONE_TAG = "phone";
	private static final String IP_TAG = "ip";
	private static final String LOGIN_TAG = "login";
	// private static final String ADD_PARAM_TAG = "additionalParameter";

	private final CompositeContext context;

	private DataPanelElementInfo elInfo;

	private final Document info = createXML();

	/**
	 * Формирует контекст сессии в виде XML объекта.
	 * 
	 * @param aContext
	 *            - параметры.
	 * @return - строку с XML.
	 * @throws UnsupportedEncodingException
	 */
	public String generate() {
		addUserNode();
		fillURLParams(context.getSessionParamsMap());
		addUserData(context.getSessionParamsMap());
		addRelatedContext(context.getRelated());
		String result = XMLUtils.documentToString(info);
		result = XMLUtils.xmlServiceSymbolsToNormal(result);
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.debug("XMLSessionContextGenerator.generate()"
					+ System.getProperty("line.separator") + result);
		}
		return result;
	}

	private void addRelatedContext(final Map<ID, CompositeContext> aRelated) {
		Element root = info.createElement(RELATED_TAG);
		info.getDocumentElement().appendChild(root);
		if ((elInfo != null) && (elInfo.getRelated().indexOf(elInfo.getId()) > -1)) {
			context.addRelated(elInfo.getId(), context);
		}
		for (Entry<ID, CompositeContext> rc : aRelated.entrySet()) {
			addContextNode(root, rc.getKey(), rc.getValue());
		}
	}

	private void addContextNode(final Element root, final ID key, final CompositeContext value) {
		Document doc = XMLUtils.objectToXML(value);
		Element inserted = doc.getDocumentElement();
		Element child = (Element) info.importNode(inserted, true);
		child.setAttribute(ID_TAG, key.getString());
		root.appendChild(child);
	}

	private static Document createXML() {
		Document info =
			XMLUtils.createBuilder().getDOMImplementation()
					.createDocument("", SESSION_CONTEXT_TAG, null);
		return info;
	}

	private void addUserNode() {
		Element node = info.createElement(USERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(ru.curs.showcase.runtime.SessionUtils
				.getCurrentSessionUserName()));

		node = info.createElement(SID_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserSID()));

		node = info.createElement(LOGIN_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserCaption()));

		node = info.createElement(SESSIONID_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentSessionId()));

		node = info.createElement(EMAIL_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserEmail()));

		node = info.createElement(FULLUSERNAME_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserFullName()));

		node = info.createElement(PHONE_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getCurrentUserPhone()));

		node = info.createElement(IP_TAG);
		info.getDocumentElement().appendChild(node);
		node.appendChild(info.createTextNode(SessionUtils.getRemoteAddress()));

		// node = info.createElement(ADD_PARAM_TAG);
		// info.getDocumentElement().appendChild(node);
		// node.appendChild(info.createTextNode(SessionUtils.getAdditionalParameter()));

		String[] additionalParameters = SessionUtils.getAdditionalParameters();
		List<String> listAddPar = AppInfoSingleton.getAppInfo().getAdditionalParametersList();
		if (additionalParameters != null && additionalParameters.length > 0) {
			for (int k = 0; k < additionalParameters.length; k++) {
				if (!listAddPar.get(k).equals("SID") && !listAddPar.get(k).equals("login")
						&& !listAddPar.get(k).equals("name")) {
					node = info.createElement(listAddPar.get(k));
					info.getDocumentElement().appendChild(node);
					node.appendChild(info.createTextNode(additionalParameters[k]));
				}
			}
		}

		Oauth2Token oauth2Token = SessionUtils.getOauth2Token();
		if (oauth2Token != null) {
			try {
				JAXBContext jc = JAXBContext.newInstance(Oauth2Token.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(oauth2Token, info.getDocumentElement());
			} catch (JAXBException ex) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Error marshal Oauth2Token", ex);
				}
			}
		}
	}

	private void addUserData(final Map<String, ArrayList<String>> aMap) {
		Element node = info.createElement(ExchangeConstants.URL_PARAM_USERDATA);
		info.getDocumentElement().appendChild(node);
		String value = null;
		if (aMap.get(ExchangeConstants.URL_PARAM_PERSPECTIVE) != null) {
			value =
				Arrays.toString(aMap.get(ExchangeConstants.URL_PARAM_PERSPECTIVE).toArray())
						.replace("[", "").replace("]", "");
		} else if (aMap.get(ExchangeConstants.URL_PARAM_USERDATA) != null) {
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
	private void fillURLParams(final Map<String, ArrayList<String>> aMap) {
		Element node;

		if (!aMap.isEmpty()) {
			node = info.createElement(URL_PARAMS_TAG);
			info.getDocumentElement().appendChild(node);

			for (Map.Entry<String, ArrayList<String>> entry : aMap.entrySet()) {
				if (!(ExchangeConstants.URL_PARAM_USERDATA.equals(entry.getKey()))
						&& !(ExchangeConstants.URL_PARAM_PERSPECTIVE.equals(entry.getKey()))) {
					Element child = info.createElement(URL_PARAM_TAG);
					node.appendChild(child);

					try {
						child.setAttribute(NAME_TAG,
								URLDecoder.decode(entry.getKey(), TextUtils.DEF_ENCODING));
					} catch (UnsupportedEncodingException e) {
						throw new UTF8Exception();
					}
					String value = "";
					if (entry.getValue() != null) {
						value = Arrays.toString(entry.getValue().toArray());
						if (value.contains(" "))
							value = value.replace(" ", "");
					}
					child.setAttribute(VALUE_TAG, value);
				}
			}
		}
	}

	public XMLSessionContextGenerator(final CompositeContext aContext) {
		super();
		context = aContext;
	}

	public XMLSessionContextGenerator(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super();
		context = aContext;
		elInfo = aElInfo;
	}
}
