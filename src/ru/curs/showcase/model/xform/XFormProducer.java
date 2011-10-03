package ru.curs.showcase.model.xform;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Класс, преобразующий документ в HTML-код XForm.
 */
public final class XFormProducer extends GeneralXMLHelper {
	public static final String XF_INSTANCE = "xf:instance";
	public static final String XSLTFORMS_XSL = "xsltforms.xsl";

	private static final String XFORMS_URI = "http://www.w3.org/2002/xforms";

	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private static final String MAIN_INSTANCE = "mainInstance";
	private static final String INSTANCE = "instance";

	private static final String LOAD = "load";
	private static final String RESOURCE = "resource";
	private static final String VALUE = "value";
	private static final String SHOW_SELECTOR = "showSelector";
	private static final String TEMP_TAG_FOR_SELECTOR_ID = "tempTagForSelector";

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(XFormProducer.class);

	private XFormProducer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает org.w3c.dom.Document с пустыми данными, полученными на основе
	 * шаблона.
	 * 
	 * @param template
	 *            Шаблон
	 * 
	 * @return org.w3c.dom.Document
	 * @throws ParserConfigurationException
	 * 
	 */
	public static org.w3c.dom.Document getEmptyData(final org.w3c.dom.Document template)
			throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.newDocument();

		Node n = getMainInstance(template).getFirstChild();

		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				break;
			}
			n = n.getNextSibling();
		}

		n = doc.importNode(n, true);
		doc.appendChild(n);

		return doc;
	}

	/**
	 * Возвращает ноду xf:instance c id=mainInstance. Такая нода должна
	 * существовать в шаблоне для того, чтобы он работал с Showcase.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент mainInstance.
	 */
	public static Node getMainInstance(final org.w3c.dom.Document doc) {
		NodeList l = doc.getElementsByTagName(XF_INSTANCE);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);
			if ((n != null) && (MAIN_INSTANCE.equals(n.getTextContent()))) {
				return l.item(i);
			}
		}
		return null;
	}

	/**
	 * Возвращает строку, являющуюся HTML-фрагментом для отображения документа в
	 * виде XForm.
	 * 
	 * @param xml
	 *            документ
	 * @param tempData
	 *            временные данные документа (если эта переменная не равна null,
	 *            эти данные подставляются в MainInstance). Необходимо для
	 *            просмотра проимпортированного содержимого формы.
	 * 
	 * @return HTML-фрагмент, пригодный для отображения в браузере
	 * @throws TransformerException
	 * 
	 */
	public static String getHTML(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData) throws TransformerException {
		insertHiddenTagForSelector(xml);
		insertActualData(xml, tempData);
		return transform(xml);
	}

	private static String transform(final org.w3c.dom.Document xml) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer tr =
			tf.newTransformer(new StreamSource(XFormProducer.class
					.getResourceAsStream(XSLTFORMS_XSL)));
		tr.setParameter("baseuri", "xsltforms/");
		StringWriter sw = new StringWriter(DEFAULT_BUFFER_SIZE);
		tr.transform(new DOMSource(xml), new StreamResult(sw));

		String ret = sw.toString();

		return ret;
	}

	private static void insertActualData(final org.w3c.dom.Document xml,
			final org.w3c.dom.Document tempData) {
		if (tempData != null) {
			NodeList nl = xml.getElementsByTagNameNS(XFORMS_URI, INSTANCE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (MAIN_INSTANCE.equals(n.getAttributes().getNamedItem(ID_TAG).getTextContent())) {
					n.setTextContent("");
					Node nn = xml.importNode(tempData.getDocumentElement(), true);
					n.appendChild(nn);
					break;
				}
			}
		}
	}

	private static void insertHiddenTagForSelector(final org.w3c.dom.Document xml) {

		ArrayList<String> selectors = getArraySelectors(xml);

		ArrayList<String> xpaths = getArrayXPaths(selectors);

		setHiddenTag(xml, xpaths);

		// LOGGER.info(XMLUtils.documentToString(xml));

	}

	private static ArrayList<String> getArraySelectors(final org.w3c.dom.Document xml) {
		ArrayList<String> selectors = new ArrayList<String>();

		NodeList nl;
		Node n;

		nl = xml.getElementsByTagNameNS(XFORMS_URI, LOAD);
		for (int i = 0; i < nl.getLength(); i++) {
			n = nl.item(i);
			if ((n.getAttributes() != null) && (n.getAttributes().getNamedItem(RESOURCE) != null)) {
				selectors.add(n.getAttributes().getNamedItem(RESOURCE).getTextContent());
			}
		}

		nl = xml.getElementsByTagNameNS(XFORMS_URI, RESOURCE);
		for (int i = 0; i < nl.getLength(); i++) {
			n = nl.item(i);
			if ((n.getAttributes() != null) && (n.getAttributes().getNamedItem(VALUE) != null)) {
				selectors.add(n.getAttributes().getNamedItem(VALUE).getTextContent());
			}
		}

		return selectors;
	}

	private static ArrayList<String> getArrayXPaths(final ArrayList<String> selectors) {
		ArrayList<String> xpaths = new ArrayList<String>();

		Pattern pXPath =
			Pattern.compile("XPath\\((\\S*)\\)", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Pattern pQuot =
			Pattern.compile("quot\\((\\w*)\\)", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Matcher mXPath;
		Matcher mQuot;
		String s;

		for (String selector : selectors) {
			if (selector.toLowerCase().indexOf(SHOW_SELECTOR.toLowerCase()) > -1) {
				mXPath = pXPath.matcher(selector);
				while (mXPath.find()) {
					s = mXPath.group(1);

					mQuot = pQuot.matcher(s);
					StringBuffer sb = new StringBuffer();
					while (mQuot.find()) {
						mQuot.appendReplacement(sb, "'" + mQuot.group(1) + "'");
					}
					mQuot.appendTail(sb);

					s = sb.toString();

					if (!xpaths.contains(s)) {
						xpaths.add(s);
					}
				}
			}
		}

		return xpaths;
	}

	private static void
			setHiddenTag(final org.w3c.dom.Document xml, final ArrayList<String> xpaths) {
		if (xpaths.isEmpty()) {
			return;
		}

		NodeList body = xml.getElementsByTagName("body");

		Element div = xml.createElement("div");
		div.setAttribute("id", TEMP_TAG_FOR_SELECTOR_ID);
		// div.setAttribute("style", "clear: both;");
		div.setAttribute("style", "display: none;");
		body.item(0).insertBefore(div, body.item(0).getFirstChild());

		for (String xpath : xpaths) {
			Element xfoutput = xml.createElementNS(XFORMS_URI, "output");
			xfoutput.setAttribute("ref", xpath);
			div.appendChild(xfoutput);
		}
	}

}