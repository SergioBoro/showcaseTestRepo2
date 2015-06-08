package ru.curs.showcase.core.html.xform;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;
import org.xml.sax.*;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.*;
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
	private static final String UPLOAD_TAG = "upload";
	private static final String SELECTOR_TAG = "selector";
	private static final String MULTISELECTOR_TAG = "multiselector";
	private static final String SELECTOR_BUTTON_LABEL_TAG = "buttonLabel";
	private static final String SUBMIT_LABEL_TAG = "submitLabel";
	private static final String SINGLE_FILE_TAG = "singleFile";
	private static final String ADD_UPLOAD_TAG = "addUpload";
	private static final String ADD_UPLOAD_LABEL_TAG = "addUploadLabel";
	private static final String FILENAMES_MAPPING = "filenamesMapping";
	private static final String NEEDCLEAR_FILENAMES = "needClearFilenames";
	private static final String FILE = "file";
	private static final String UPLOAD_DATA_TAG = "uploaddata";
	private static final String ROOT_SRV_DATA_TAG = "srvdata";
	private static final String ORIGIN = "instance('%s" + ROOT_SRV_DATA_TAG + "')/"
			+ UPLOAD_DATA_TAG + "/%s";
	private static final String NEED_RELOAD_TAG = "needReload";
	private static final String XMLNS = "xmlns";

	private static final String DOM_ACTIVATE = "DOMActivate";
	private static final String LOAD = "load";
	private static final String RESOURCE = "resource";
	private static final String ACTION = "action";
	private static final String VALUE = "value";
	private static final String SHOW_SELECTOR = "showSelector";
	private static final String SHOW_MULTISELECTOR = "showMultiSelector";
	private static final String CREATEPLUGIN = "gwtCreatePlugin";
	private static final String TEMP_TAG_FOR_SELECTOR_ID = "tempTagForSelector";

	// CHECKSTYLE:OFF
	private static final String JS_SIMPLE_UPLOAD =
		"javascript:gwtXFormSimpleUpload('xformId', '%s', Writer.toString(getSubformInstanceDocument('%smainModel', '%smainInstance')))";

	// CHECKSTYLE:ON

	private static final String JS_ADD_UPLOAD = "javascript:addUpload('%s')";

	private static final String JS_SELECTOR_TEMPLATE = "javascript:%s({%s id:'xformId'});";

	private static final String JS_ON_CHOOSE_FILES =
		"gwtXFormOnChooseFiles('%s', '%s', '%s', %s, 'add_upload_index_0')";

	private static final String JS_ON_SUBMIT_COMPLETE = "gwtXFormOnSubmitComplete('%s')";

	private static final String DEFAULT_SUBMIT_LABEL = "Загрузить";

	private static final String DEFAULT_SELECTOR_LABEL = "Выбрать";

	private static final String DEFAULT_ADD_UPLOAD_LABEL = "Добавить выбор файла";

	private static boolean isFilenamesMapping = false;

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
			final CompositeContext context, final DataPanelElementInfo element,
			final String subformId) {
		Node node = XFormProducer.getMainInstance(doc);
		Node parent = node.getParentNode();

		// Element el = doc.createElementNS("", XFormProducer.XF_INSTANCE);
		Element el = doc.createElementNS(XFormProducer.XFORMS_URI, XFormProducer.INSTANCE);
		el.setAttribute(ID_TAG, subformId + ROOT_SRV_DATA_TAG);
		Node srv = parent.appendChild(el);
		el = doc.createElement(SCHEMA_TAG);
		srv = srv.appendChild(el);

		addServerElement(context, doc, srv, context);
		addServerElement(context, doc, srv, element);

		doc.normalizeDocument();

		return doc;
	}

	public static org.w3c.dom.Document adjustSrvInfo(final org.w3c.dom.Document doc,
			final String subformId) {

		Node srv = getSrvDataInstance(doc, subformId);

		Node xlmns = srv.getAttributes().getNamedItem(XMLNS);
		if (xlmns != null) {
			xlmns.setTextContent("");
			xlmns.setNodeValue("");
		}

		return doc;

	}

	/**
	 * Возвращает ноду xf:instance c id=ROOT_SRV_DATA_TAG.
	 * 
	 * @param doc
	 *            - документ шаблона.
	 * @return - элемент c id=ROOT_SRV_DATA_TAG.
	 */
	public static Node getSrvDataInstance(final org.w3c.dom.Document doc, final String subformId) {
		NodeList l = doc.getElementsByTagNameNS(XFormProducer.XFORMS_URI, XFormProducer.INSTANCE);

		for (int i = 0; i < l.getLength(); i++) {
			Node n = l.item(i).getAttributes().getNamedItem(ID_TAG);
			if ((n != null) && ((subformId + ROOT_SRV_DATA_TAG).equals(n.getTextContent()))) {
				return l.item(i);
			}
		}
		return null;
	}

	public static org.w3c.dom.Document generateSelectors(final org.w3c.dom.Document doc) {
		org.w3c.dom.Document result = generateSingleAndMultiSelector(doc, SELECTOR_TAG);
		result = generateSingleAndMultiSelector(result, MULTISELECTOR_TAG);

		// LoggerFactory.getLogger(XFormTemplateModificator.class).info(
		// XMLUtils.documentToString(result));

		return result;
	}

	public static org.w3c.dom.Document generateSingleAndMultiSelector(
			final org.w3c.dom.Document doc, final String selectorTag) {
		String selectorFunc;
		switch (selectorTag) {
		case SELECTOR_TAG:
			selectorFunc = SHOW_SELECTOR;
			break;
		case MULTISELECTOR_TAG:
			selectorFunc = SHOW_MULTISELECTOR;
			break;
		default:
			selectorFunc = SHOW_SELECTOR;
			break;
		}

		NodeList nl = doc.getElementsByTagNameNS(XFormProducer.XFORMS_URI, selectorTag);
		for (int i = nl.getLength() - 1; i >= 0; i--) {
			Node old = nl.item(i);
			Node parent = old.getParentNode();
			String params = "";
			String buttonLabel = DEFAULT_SELECTOR_LABEL;
			for (int j = 0; j < old.getAttributes().getLength(); j++) {
				if (SELECTOR_BUTTON_LABEL_TAG.endsWith(old.getAttributes().item(j).getNodeName())) {
					buttonLabel = old.getAttributes().item(j).getNodeValue();
				} else {
					if (!old.getAttributes().item(j).getNodeValue().trim().isEmpty()) {
						params =
							params + old.getAttributes().item(j).getNodeName() + " : "
									+ old.getAttributes().item(j).getNodeValue() + ", ";
					}
				}
			}

			Element trigger = doc.createElementNS(XFormProducer.XFORMS_URI, "trigger");
			parent.replaceChild(trigger, old);

			Element label = doc.createElementNS(XFormProducer.XFORMS_URI, "label");
			if (buttonLabel.toLowerCase().contains("XPath".toLowerCase())) {
				ArrayList<String> arr = new ArrayList<>();
				addXPathsFromStringToArrayXPaths(buttonLabel, arr);
				buttonLabel = arr.get(0);

				Element xfoutput = doc.createElementNS(XFormProducer.XFORMS_URI, "output");
				xfoutput.setAttribute("ref", buttonLabel);
				label.appendChild(xfoutput);
			} else {
				label.setTextContent(buttonLabel);
			}
			trigger.appendChild(label);

			Element action = doc.createElementNS(XFormProducer.XFORMS_URI, ACTION);
			action.setAttributeNS(XFormProducer.EVENTS_URI, "ev:event", DOM_ACTIVATE);
			trigger.appendChild(action);

			Element load = doc.createElementNS(XFormProducer.XFORMS_URI, LOAD);
			load.setAttribute(RESOURCE, String.format(JS_SELECTOR_TEMPLATE, selectorFunc, params));
			action.appendChild(load);
		}

		return doc;
	}

	// CHECKSTYLE:OFF
	public static org.w3c.dom.Document generateUploaders(final org.w3c.dom.Document doc,
			final DataPanelElementInfo element, final String subformId) {
		NodeList nl = doc.getElementsByTagNameNS(XFormProducer.XFORMS_URI, UPLOAD_TAG);
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
			form.setAttribute(ACTION, ExchangeConstants.SECURED_SERVLET_PREFIX + "/upload");
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

			String filenamesMapping = null;
			boolean needClearFilenames = false;
			node = old.getAttributes().getNamedItem(FILENAMES_MAPPING);
			if (node != null) {
				filenamesMapping = node.getTextContent();
			}
			if (filenamesMapping != null) {
				node = old.getAttributes().getNamedItem(NEEDCLEAR_FILENAMES);
				if (node != null) {
					needClearFilenames = Boolean.parseBoolean(node.getTextContent());
				}
				input.setAttribute("onchange", String.format(JS_ON_CHOOSE_FILES, subformId,
						"@@filedata@@" + procId, filenamesMapping, needClearFilenames));
			}

			input.setAttribute(TYPE_TAG, "file");
			form.setAttribute("class", "sc-uploader-comp");
			form.appendChild(input);

			node = old.getAttributes().getNamedItem(ADD_UPLOAD_TAG);
			if (node != null) {
				boolean addUpload = Boolean.parseBoolean(node.getTextContent());
				if (addUpload) {
					String addUploadLabel;
					node = old.getAttributes().getNamedItem(ADD_UPLOAD_LABEL_TAG);
					if (node != null) {
						addUploadLabel = node.getTextContent();
					} else {
						addUploadLabel = DEFAULT_ADD_UPLOAD_LABEL;
					}

					table.setAttribute("cols", "3");

					td = doc.createElement("td");
					tr.appendChild(td);

					Element trigger = doc.createElementNS(XFormProducer.XFORMS_URI, "trigger");
					td.appendChild(trigger);

					Element label = doc.createElementNS(XFormProducer.XFORMS_URI, "label");
					label.setTextContent(addUploadLabel);
					trigger.appendChild(label);

					Element action = doc.createElementNS(XFormProducer.XFORMS_URI, ACTION);
					action.setAttributeNS(XFormProducer.EVENTS_URI, "ev:event", DOM_ACTIVATE);
					trigger.appendChild(action);

					Element load = doc.createElementNS(XFormProducer.XFORMS_URI, LOAD);
					load.setAttribute(RESOURCE,
							String.format(JS_ADD_UPLOAD, element.getUploaderId(procId)));
					action.appendChild(load);

				}
			}

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

					Element action = doc.createElementNS(XFormProducer.XFORMS_URI, ACTION);
					action.setAttributeNS(XFormProducer.EVENTS_URI, "ev:event", DOM_ACTIVATE);
					trigger.appendChild(action);

					Element load = doc.createElementNS(XFormProducer.XFORMS_URI, LOAD);
					load.setAttribute(RESOURCE,
							String.format(JS_SIMPLE_UPLOAD, procId, subformId, subformId));
					action.appendChild(load);
				}
			}

			Element iframe = doc.createElement("iframe");
			iframe.setAttribute(NAME_TAG, getUploaderTargetName(element, procId, i));
			iframe.setAttribute("src", "javascript:''");
			iframe.setAttribute("style", "position:absolute;width:0;height:0;border:0");
			iframe.setAttribute("onload", String.format(JS_ON_SUBMIT_COMPLETE,
					getUploaderTargetName(element, procId, i)));
			parent.appendChild(iframe);
		}

		// LoggerFactory.getLogger(XFormTemplateModificator.class).info(
		// XMLUtils.documentToString(doc));

		return doc;
	}

	// CHECKSTYLE:ON

	private static String getUploaderTargetName(final DataPanelElementInfo element,
			final String procId, final int index) {
		return String.format("%s_%s__%s", element.getUploaderId(procId), index, "hidden_target");
	}

	private static void addServerElement(final CompositeContext context,
			final org.w3c.dom.Document doc, final Node srv, final Object xsrElement) {
		Element inserted = XMLUtils.objectToXML(xsrElement).getDocumentElement();
		Node nn = doc.importNode(inserted, true);
		if ((xsrElement instanceof DataPanelElementInfo) && (context instanceof XFormContext)) {
			Element needReload = doc.createElement(NEED_RELOAD_TAG);
			needReload.setTextContent(String.valueOf(((XFormContext) context).getNeedReload()));
			nn.appendChild(needReload);
		}
		srv.appendChild(nn);
	}

	public static Document modify(final Document aTemplate, final CompositeContext aCallContext,
			final DataPanelElementInfo aElementInfo, final String subformId) {
		Document result = addSrvInfo(aTemplate, aCallContext, aElementInfo, subformId);
		result = generateSelectors(result);
		result = insertDataForSelectors(result, subformId);
		result = generateUploaders(result, aElementInfo, subformId);
		result = adjustSrvInfo(result, subformId);
		if (aElementInfo.getBuildTemplate()) {
			result = insertPartTemplate(result, aCallContext, aElementInfo);
		}
		return result;
	}

	private static Document insertDataForSelectors(final org.w3c.dom.Document xml,
			final String subformId) {

		isFilenamesMapping = false;

		ArrayList<String> selectors = getArraySelectors(xml);

		ArrayList<String> xpaths = getArrayXPaths(xml, selectors);

		adjustArrayXPathsForMultiSelectors(selectors, xpaths);

		Document result = setDataForSelectors(xml, xpaths, subformId);

		// LoggerFactory.getLogger(XFormTemplateModificator.class).info(
		// XMLUtils.documentToString(result));

		return result;
	}

	private static ArrayList<String> getArraySelectors(final org.w3c.dom.Document xml) {
		ArrayList<String> selectors = new ArrayList<>();

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

	private static ArrayList<String> getArrayXPaths(final org.w3c.dom.Document xml,
			final ArrayList<String> selectors) {
		ArrayList<String> xpaths = new ArrayList<>();

		for (String selector : selectors) {
			if ((selector.toLowerCase().indexOf(SHOW_SELECTOR.toLowerCase()) > -1)
					|| (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) > -1)
					|| (selector.toLowerCase().indexOf(CREATEPLUGIN.toLowerCase()) > -1)) {
				addXPathsFromStringToArrayXPaths(selector, xpaths);
			}
		}

		NodeList nl = xml.getElementsByTagNameNS(XFormProducer.XFORMS_URI, UPLOAD_TAG);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if ((n.getAttributes() != null)
					&& (n.getAttributes().getNamedItem(FILENAMES_MAPPING) != null)) {
				isFilenamesMapping = true;
				addXPathsFromStringToArrayXPaths(n.getAttributes().getNamedItem(FILENAMES_MAPPING)
						.getTextContent(), xpaths);
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
			Pattern.compile("quot\\(([a-zA-Z_0-9-]*)\\)", Pattern.CASE_INSENSITIVE
					+ Pattern.UNICODE_CASE);

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

	// CHECKSTYLE:OFF
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
			if (selector.toLowerCase().indexOf(SHOW_MULTISELECTOR.toLowerCase()) == -1
					&& selector.toLowerCase().indexOf(CREATEPLUGIN.toLowerCase()) == -1) {
				continue;
			}

			ArrayList<String> localXPaths = new ArrayList<>();
			ArrayList<String> xpathMapping = new ArrayList<>();
			ArrayList<String> xpathRoot = new ArrayList<>();

			mXPathMapping = pXPathMapping.matcher(selector);
			if (mXPathMapping.find()) {
				s = mXPathMapping.group(1);
				addXPathsFromStringToArrayXPaths(s, xpathMapping);

				mQuot = pQuot.matcher(s);
				while (mQuot.find()) {
					if (!"xformId".equalsIgnoreCase(mQuot.group(1))) {
						addIfNotContains(localXPaths, mQuot.group(1));
					}
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

	// CHECKSTYLE:ON

	private static Document setDataForSelectors(final org.w3c.dom.Document xml,
			final ArrayList<String> xpaths, final String subformId) {
		if (isFilenamesMapping) {
			addIfNotContains(xpaths, FILE);
			addIfNotContains(xpaths, String.format(ORIGIN, subformId, FILE));

			Node srv = getSrvDataInstance(xml, subformId);

			Element data = xml.createElement(UPLOAD_DATA_TAG);
			srv.getFirstChild().appendChild(data);

			Element el = xml.createElement(FILE);
			data.appendChild(el);
		}

		if (!xpaths.isEmpty()) {
			NodeList body = xml.getElementsByTagName("body");

			Element div = xml.createElement("div");
			div.setAttribute("id", subformId + TEMP_TAG_FOR_SELECTOR_ID);
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

	/**
	 * Осуществляет парсинг документа, соответсвующего родительской иксформе, и
	 * вызов методов непосредственной вставки частей искформы, которые находятся
	 * в файлах, указанных как значения атрибутов тегов <div>, а именно:
	 * insertTemplate (соответсвует вставке части иксформы в указанное место) и
	 * insertBind (соответсвует вставке биндов, сабмишенов и дополнительных
	 * инстансов).
	 * 
	 * @param xml
	 *            - документ, соответствующий родительской иксформе
	 * @param aCallContext
	 *            - родительский контекст
	 * @param dpei
	 *            - родительский элемент (соответсвует родельской иксформе)
	 * @return - документ, дополненный описанными вставками
	 */

	private static Document insertPartTemplate(final org.w3c.dom.Document xml,
			CompositeContext aCallContext,
			DataPanelElementInfo dpei) {
		String name = "";
		NodeList nl = xml.getElementsByTagName("div");
		NodeList nl2 = xml.getElementsByTagName("xf:model");
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).hasAttributes()
					&& nl.item(i).getAttributes().getNamedItem("insertTemplate") != null) {
				Node node = nl.item(i).getAttributes().getNamedItem("insertTemplate");
				name = node.getTextContent();
				insertingXml(xml, nl.item(i), aCallContext, dpei, name);
			}
			if (nl.item(i).hasAttributes()
					&& nl.item(i).getAttributes().getNamedItem("insertBind") != null) {
				Node node = nl.item(i).getAttributes().getNamedItem("insertBind");
				name = node.getTextContent();
				insertingXml(xml, nl2.item(0), aCallContext, dpei, name);
			}
		}
		return xml;
	}

	/**
	 * Осуществляет непосредственную вставку частей иксформы в документ,
	 * соответствующий родительской иксформе.
	 * 
	 * @param xml
	 *            - документ, соответствующий родительской иксформе
	 * @param rootNode
	 *            - узел родительского документа, куда осуществляется вставка
	 *            частей иксформы
	 * @param aCallContext
	 *            - родительский контекст
	 * @param dpei
	 *            - родительский элемент (соответсвует родительской иксформе)
	 * @param name
	 *            - имя файла (xml-, питон- или челеста-файла), из которого
	 *            осуществляется вставка частей иксформы
	 */

	private static void insertingXml(org.w3c.dom.Document xml, Node rootNode,
			CompositeContext aCallContext, DataPanelElementInfo dpei, String name) {
		XFormTemplateSelector selector = new XFormTemplateSelector(name);
		DataFile<InputStream> data =
			selector.getGateway().getRawDataForPartTemplate(aCallContext, dpei);
		DocumentBuilder db = XMLUtils.createBuilder();
		Document template = null;
		String stringTemplate = null;
		try {
			stringTemplate = TextUtils.streamToString(data.getData(), data.getEncoding());
			stringTemplate = TextUtils.removeUTF8BOM(stringTemplate);
			stringTemplate = UserDataUtils.replaceVariables(stringTemplate);
			StringReader reader = new StringReader(stringTemplate);
			try {
				InputSource inputSource = new InputSource(reader);
				template = db.parse(inputSource);
			} finally {
				reader.close();
			}
		} catch (SAXException | IOException e1) {
			throw new XMLFormatException(data.getName(), e1);
		}

		NodeList nl = template.getElementsByTagName("partOfXFormTemplate");
		for (int j = 0; j < nl.getLength(); j++) {
			for (int n = 0; n < nl.item(j).getChildNodes().getLength(); n++) {
				Node nd = nl.item(j).getChildNodes().item(n);
				if (nd != null) {
					rootNode.appendChild(xml.importNode(nd, true));
				}
			}
		}
	}
}
