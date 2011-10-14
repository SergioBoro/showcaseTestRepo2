package ru.curs.showcase.model.xform;

import org.w3c.dom.*;

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
			form.setAttribute("target", getUploaderTargetName(element, procId));
			form.setAttribute("method", "post");
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
			iframe.setAttribute(NAME_TAG, getUploaderTargetName(element, procId));
			iframe.setAttribute("src", "javascript:''");
			iframe.setAttribute("style", "position:absolute;width:0;height:0;border:0");
			parent.appendChild(iframe);
		}

		return doc;
	}

	private static String getUploaderTargetName(final DataPanelElementInfo element,
			final String procId) {
		return String.format("%s__%s", element.getUploaderId(procId), "hidden_target");
	}

	private static void addServerElement(final org.w3c.dom.Document doc, final Node srv,
			final Object xsrElement) {
		Element inserted = XMLUtils.objectToXML(xsrElement).getDocumentElement();
		Node nn = doc.importNode(inserted, true);
		srv.appendChild(nn);
	}

	public static Document modify(final Document aTemplate, final CompositeContext aCallContext,
			final DataPanelElementInfo aElementInfo) {
		Document result =
			XFormTemplateModificator.addSrvInfo(aTemplate, aCallContext, aElementInfo);
		result = XFormTemplateModificator.generateUploaders(result, aElementInfo);
		return result;
	}

}
