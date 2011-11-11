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
	private static final String SUBMIT_TAG = "submit";
	private static final String SUBMIT_LABEL_TAG = "submitLabel";
	private static final String SINGLE_FILE_TAG = "singleFile";
	private static final String ROOT_SRV_DATA_TAG = "srvdata";

	private static final String LOAD = "load";
	private static final String RESOURCE = "resource";
	private static final String VALUE = "value";
	private static final String SHOW_SELECTOR = "showSelector";
	private static final String SHOW_MULTISELECTOR = "showMultiSelector";
	private static final String TEMP_TAG_FOR_SELECTOR_ID = "tempTagForSelector";

	// CHECKSTYLE:OFF
	private static final String JS_SIMPLE_UPLOAD =
		"javascript:gwtXFormSimpleUpload('xformId', '%s', Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))";

	// CHECKSTYLE:ON

	private static final String DEFAULT_SUBMIT_LABEL = "Загрузить";

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

	/**
	 * Возвращает ноду xf:instance c id=ROOT_SRV_DATA_TAG.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент c id=ROOT_SRV_DATA_TAG.
	 */
	public static Node getSrvDataInstance(final org.w3c.dom.Document doc) {
		NodeList l = doc.getElementsByTagName(XFormProducer.XF_INSTANCE);
		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);
			if ((n != null) && (ROOT_SRV_DATA_TAG.equals(n.getTextContent()))) {
				return l.item(i);
			}
		}
		return null;
	}

	public static org.w3c.dom.Document generateUploaders(final org.w3c.dom.Document doc,
			final DataPanelElementInfo element) {
		NodeList nl = doc.getElementsByTagNameNS(XFormProducer.XFORMS_URI, "upload");
		for (int i = nl.getLength() - 1; i >= 0; i--) {
			Node old = nl.item(i);
			String procId = old.getAttributes().getNamedItem(ID_TAG).getTextContent();
			Node parent = old.getParentNode();
			Element table = doc.createElement("table");
			table.setAttribute("border", "0");
			table.setAttribute("cellpadding", "0");
			table.setAttribute("cellspacing", "0");
			table.setAttribute("frame", "void");
			table.setAttribute("rules", "none");
			table.setAttribute("cols", "1");
			parent.replaceChild(table, old);
			Element tr = doc.createElement("tr");
			tr.setAttribute("valign", "baseline");
			table.appendChild(tr);
			Element td = doc.createElement("td");
			tr.appendChild(td);
			Element form = doc.createElement("form");
			td.appendChild(form);
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
			boolean singleFile = false;
			Node node = old.getAttributes().getNamedItem(SINGLE_FILE_TAG);
			if (node != null) {
				singleFile = Boolean.parseBoolean(node.getTextContent());
			}
			if (!singleFile) {
				input.setAttribute("multiple", "multiple");
			}
			input.setAttribute(TYPE_TAG, "file");
			form.setAttribute("class", "sc-uploader-comp");
			form.appendChild(input);

			node = old.getAttributes().getNamedItem(SUBMIT_TAG);
			if (node != null) {
				boolean submit = Boolean.parseBoolean(node.getTextContent());
				if (submit) {
					String submitLabel;
					node = old.getAttributes().getNamedItem(SUBMIT_LABEL_TAG);
					if (node != null) {
						submitLabel = node.getTextContent();
					} else {
						submitLabel = DEFAULT_SUBMIT_LABEL;
					}

					table.setAttribute("cols", "2");

					td = doc.createElement("td");
					tr.appendChild(td);

					Element trigger = doc.createElementNS(XFormProducer.XFORMS_URI, "trigger");
					td.appendChild(trigger);

					Element label = doc.createElementNS(XFormProducer.XFORMS_URI, "label");
					label.setTextContent(submitLabel);
					trigger.appendChild(label);

					Element action = doc.createElementNS(XFormProducer.XFORMS_URI, "action");
					action.setAttributeNS(XFormProducer.EVENTS_URI, "ev:event", "DOMActivate");
					trigger.appendChild(action);

					Element load = doc.createElementNS(XFormProducer.XFORMS_URI, "load");
					load.setAttribute("resource", String.format(JS_SIMPLE_UPLOAD, procId));
					action.appendChild(load);
				}
			}

			Element iframe = doc.createElement("iframe");
			iframe.setAttribute(NAME_TAG, getUploaderTargetName(element, procId, i));
			iframe.setAttribute("src", "javascript:''");
			iframe.setAttribute("style", "position:absolute;width:0;height:0;border:0");
			iframe.setAttribute("isSetOnSubmitComplete", "");
			parent.appendChild(iframe);
		}

		// LoggerFactory.getLogger(XFormTemplateModificator.class).info(
		// XMLUtils.documentToString(doc));

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

		adjustArrayXPathsForMultiSelectors(selectors, xpaths);

		Document result = setDataForSelectors(xml, xpaths);

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
		for (String selector : selectors) {
			if ((selector.toLowerCase().indexOf(SHOW_SELECTOR.toLowerCase()) > -1)
					|| (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) > -1)) {
				addXPathsFromStringToArrayXPaths(selector, xpaths);
			}
		}
		return xpaths;
	}

	private static void addIfNotContains(final ArrayList<String> arrList, final String s) {
		if (!arrList.contains(s)) {
			arrList.add(s);
		}
	}

	private static void addXPathsFromStringToArrayXPaths(final String selector,
			final ArrayList<String> xpaths) {
		Pattern pXPath =
			Pattern.compile("XPath\\((\\S*)\\)", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Pattern pQuot =
			Pattern.compile("quot\\((\\w*)\\)", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Matcher mXPath;
		Matcher mQuot;
		String s;

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
			addIfNotContains(xpaths, s);

		}
	}

	private static void adjustArrayXPathsForMultiSelectors(final ArrayList<String> selectors,
			final ArrayList<String> xpaths) {
		Pattern pXPathMapping =
			Pattern.compile("xpathMapping\\s*\\:\\s*\\{([\\s\\S]*)\\}", Pattern.CASE_INSENSITIVE
					+ Pattern.UNICODE_CASE);

		Pattern pXPathRoot =
			Pattern.compile("xpathRoot\\s*\\:\\s*\\'(XPath\\(\\S*\\))\\'",
					Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Pattern pQuot =
			Pattern.compile("'([^']*)'", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Pattern pLastPartXPath =
			Pattern.compile("\\/([^\\/]*)$", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

		Matcher mXPathMapping;
		Matcher mXPathRoot;
		Matcher mQuot;
		Matcher mLastPartXPath;
		String s;

		for (String selector : selectors) {
			if (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) > -1) {
				ArrayList<String> localXPaths = new ArrayList<String>();
				ArrayList<String> xpathMapping = new ArrayList<String>();
				ArrayList<String> xpathRoot = new ArrayList<String>();

				mXPathMapping = pXPathMapping.matcher(selector);
				if (mXPathMapping.find()) {
					s = mXPathMapping.group(1);
					addXPathsFromStringToArrayXPaths(s, xpathMapping);

					mQuot = pQuot.matcher(s);
					while (mQuot.find()) {
						addIfNotContains(localXPaths, mQuot.group(1));
					}
				}

				mXPathRoot = pXPathRoot.matcher(selector);
				if (mXPathRoot.find()) {
					s = mXPathRoot.group(1);
					addXPathsFromStringToArrayXPaths(s, xpathRoot);
				}

				if (!xpathMapping.isEmpty()) {
					String sLastPartXPath = null;
					mLastPartXPath = pLastPartXPath.matcher(xpathMapping.get(0));
					if (mLastPartXPath.find()) {
						sLastPartXPath = mLastPartXPath.group(1);
						addIfNotContains(xpaths, sLastPartXPath);
					}

					for (String localXPath : localXPaths) {
						if (!localXPath.toLowerCase().contains("XPath".toLowerCase())) {
							s = xpathMapping.get(0) + "/" + localXPath;
							addIfNotContains(xpaths, s);

							if (sLastPartXPath != null) {
								s = xpathRoot.get(0) + "/" + sLastPartXPath + "/" + localXPath;
								addIfNotContains(xpaths, s);
							}
						}
					}
				}

			}
		}
	}

	private static Document setDataForSelectors(final org.w3c.dom.Document xml,
			final ArrayList<String> xpaths) {
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

		return xml;
	}

}
