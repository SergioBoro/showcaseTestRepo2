package ru.curs.showcase.model.xform;

import java.util.ArrayList;
import java.util.regex.*;

import org.w3c.dom.*;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.xml.*;

/**
 * Класс, модифицирующий шаблон XForms, добавляя в него служебную информацию,
 * необходимую для работы Showcase.
 * 
 * @author den
 * 
 */
public final class XFormTemplateModificator extends GeneralXMLHelper {
	private static final String INPUT_TAG = "input";
	private static final String ROOT_SRV_DATA_TAG = "srvdata";

	private static final String LOAD = "load";
	private static final String RESOURCE = "resource";
	private static final String VALUE = "value";
	private static final String SHOW_SELECTOR = "showSelector";
	private static final String SHOW_MULTISELECTOR = "showMultiSelector";
	private static final String TEMP_TAG_FOR_SELECTOR_ID = "tempTagForSelector";
	private static final String SELECTOR_DATA_TAG = "selectordata";
	private static final String ORIGIN = "instance('" + ROOT_SRV_DATA_TAG + "')/"
			+ SELECTOR_DATA_TAG + "/%s";

	private XFormTemplateModificator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Добавляет в шаблон информацию о текущем контексте и элементе.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - документ шаблона.
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента инф. панели.
	 */
	public static org.w3c.dom.Document addSrvInfo(final org.w3c.dom.Document doc,
			final CompositeContext context, final DataPanelElementInfo element) {
		Node node = XFormProducer.getMainInstance(doc);
		Node parent = node.getParentNode();

		Element el = doc.createElementNS("", XFormProducer.XF_INSTANCE);
		el.setAttribute(ID_TAG, ROOT_SRV_DATA_TAG);
		Node srv = parent.appendChild(el);
		el = doc.createElement(SCHEMA_TAG);
		srv = srv.appendChild(el);

		addServerElement(doc, srv, context);
		addServerElement(doc, srv, element);

		doc.normalizeDocument();

		return doc;
	}

	public static org.w3c.dom.Document generateUploaders(final org.w3c.dom.Document doc,
			final DataPanelElementInfo element) {
		NodeList nl = doc.getElementsByTagNameNS(XFormProducer.XFORMS_URI, "upload");
		for (int i = nl.getLength() - 1; i >= 0; i--) {
			Node old = nl.item(i);
			String procId = old.getAttributes().getNamedItem(ID_TAG).getTextContent();
			Element form = doc.createElement("form");
			Node parent = old.getParentNode();
			parent.replaceChild(form, old);
			form.setAttribute(ID_TAG, element.getUploaderId(procId));
			form.setAttribute("class", "sc-uploader-form");
			form.setAttribute("target", getUploaderTargetName(element, procId, i));
			form.setAttribute("method", "post");
			form.setAttribute("accept-charset", "utf-8");
			form.setAttribute("enctype", "multipart/form-data");
			form.setAttribute("style", "width: 100%; height: 100%;");
			form.setAttribute("action", ExchangeConstants.SECURED_SERVLET_PREFIX + "/upload");
			Element input = doc.createElement(INPUT_TAG);
			input.setAttribute(NAME_TAG, XFormContext.class.getName());
			input.setAttribute(TYPE_TAG, "hidden");
			form.appendChild(input);
			input = doc.createElement(INPUT_TAG);
			input.setAttribute(NAME_TAG, DataPanelElementInfo.class.getName());
			input.setAttribute(TYPE_TAG, "hidden");
			form.appendChild(input);
			input = doc.createElement(INPUT_TAG);
			input.setAttribute(NAME_TAG, "@@filedata@@" + procId);
			input.setAttribute("multiple", "multiple");
			input.setAttribute(TYPE_TAG, "file");
			form.setAttribute("class", "sc-uploader-comp");
			form.appendChild(input);

			Element iframe = doc.createElement("iframe");
			iframe.setAttribute(NAME_TAG, getUploaderTargetName(element, procId, i));
			iframe.setAttribute("src", "javascript:''");
			iframe.setAttribute("style", "position:absolute;width:0;height:0;border:0");
			iframe.setAttribute("isSetOnSubmitComplete", "");
			parent.appendChild(iframe);
		}

		return doc;
	}

	private static String getUploaderTargetName(final DataPanelElementInfo element,
			final String procId, final int index) {
		return String.format("%s_%s__%s", element.getUploaderId(procId), index, "hidden_target");
	}

	private static void addServerElement(final org.w3c.dom.Document doc, final Node srv,
			final Object xsrElement) {
		Element inserted = XMLUtils.objectToXML(xsrElement).getDocumentElement();
		Node nn = doc.importNode(inserted, true);
		srv.appendChild(nn);
	}

	public static Document modify(final Document aTemplate, final CompositeContext aCallContext,
			final DataPanelElementInfo aElementInfo) {
		Document result = addSrvInfo(aTemplate, aCallContext, aElementInfo);
		result = generateUploaders(result, aElementInfo);
		result = insertDataForSelectors(result);
		return result;
	}

	private static Document insertDataForSelectors(final org.w3c.dom.Document xml) {

		ArrayList<String> selectors = getArraySelectors(xml);

		ArrayList<String> xpaths = getArrayXPaths(selectors);

		ArrayList<String> origins = getArrayOrigins(selectors);

		Document result = setDataForSelectors(xml, xpaths, origins);

		// LoggerFactory.getLogger(XFormTemplateModificator.class).info(
		// XMLUtils.documentToString(result));

		return result;
	}

	private static ArrayList<String> getArraySelectors(final org.w3c.dom.Document xml) {
		ArrayList<String> selectors = new ArrayList<String>();

		NodeList nl;
		Node n;

		nl = xml.getElementsByTagNameNS(XFormProducer.XFORMS_URI, LOAD);
		for (int i = 0; i < nl.getLength(); i++) {
			n = nl.item(i);
			if ((n.getAttributes() != null) && (n.getAttributes().getNamedItem(RESOURCE) != null)) {
				selectors.add(n.getAttributes().getNamedItem(RESOURCE).getTextContent());
			}
		}

		nl = xml.getElementsByTagNameNS(XFormProducer.XFORMS_URI, RESOURCE);
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
			if ((selector.toLowerCase().indexOf(SHOW_SELECTOR.toLowerCase()) > -1)
					|| (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) > -1)) {
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

	private static ArrayList<String> getArrayOrigins(final ArrayList<String> selectors) {
		ArrayList<String> origins = new ArrayList<String>();

		Pattern pXPathMapping =
			Pattern.compile("xpathMapping\\s*\\:\\s*\\{([\\s\\S]*)\\}", Pattern.CASE_INSENSITIVE
					+ Pattern.UNICODE_CASE);

		Pattern pQuot =
			Pattern.compile("'(\\w*)'", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Matcher mXPathMapping;
		Matcher mQuot;
		String s;

		for (String selector : selectors) {
			if (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) > -1) {
				mXPathMapping = pXPathMapping.matcher(selector);
				if (mXPathMapping.find()) {
					s = mXPathMapping.group(1);

					mQuot = pQuot.matcher(s);
					while (mQuot.find()) {
						if (!origins.contains(mQuot.group(1))) {
							origins.add(mQuot.group(1));
						}
					}
				}
			}
		}

		return origins;
	}

	/**
	 * Возвращает ноду xf:instance c id=ROOT_SRV_DATA_TAG.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент c id=ROOT_SRV_DATA_TAG.
	 */
	private static Node getSrvDataInstance(final org.w3c.dom.Document doc) {
		NodeList l = doc.getElementsByTagName(XFormProducer.XF_INSTANCE);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);
			if ((n != null) && (ROOT_SRV_DATA_TAG.equals(n.getTextContent()))) {
				return l.item(i);
			}
		}
		return null;
	}

	private static Document setDataForSelectors(final org.w3c.dom.Document xml,
			final ArrayList<String> xpaths, final ArrayList<String> origins) {
		String s;
		for (String origin : origins) {
			s = origin;
			if (!xpaths.contains(s)) {
				xpaths.add(s);
			}
		}
		for (String origin : origins) {
			s = String.format(ORIGIN, origin);
			if (!xpaths.contains(s)) {
				xpaths.add(s);
			}
		}

		if (!xpaths.isEmpty()) {
			NodeList body = xml.getElementsByTagName("body");

			Element div = xml.createElement("div");
			div.setAttribute("id", TEMP_TAG_FOR_SELECTOR_ID);
			// div.setAttribute("style", "clear: both;");
			div.setAttribute(GeneralConstants.STYLE_TAG, "display: none;");
			body.item(0).insertBefore(div, body.item(0).getFirstChild());

			for (String xpath : xpaths) {
				Element xfoutput = xml.createElementNS(XFormProducer.XFORMS_URI, "output");
				xfoutput.setAttribute("ref", xpath);
				div.appendChild(xfoutput);
			}
		}

		if (!origins.isEmpty()) {
			Node srv = getSrvDataInstance(xml);

			Element data = xml.createElement(SELECTOR_DATA_TAG);
			srv.getFirstChild().appendChild(data);

			for (String origin : origins) {
				Element el = xml.createElement(origin);
				data.appendChild(el);
			}
		}

		return xml;
	}

}
