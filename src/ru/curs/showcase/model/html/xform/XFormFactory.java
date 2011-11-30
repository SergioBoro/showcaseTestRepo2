package ru.curs.showcase.model.html.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.slf4j.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.model.html.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormFactory extends HTMLBasedElementFactory {
	protected static final Logger LOGGER = LoggerFactory.getLogger(XFormFactory.class);

	private static final String XFORMS_CREATE_ERROR =
		"Ошибка при формировании XForms для элемента '%s'";

	/**
	 * Результат работы фабрики.
	 */
	private XForm result;

	/**
	 * Промежуточный результат работы фабрики.
	 */
	private String html;

	public XFormFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public XForm build() throws Exception {
		return (XForm) super.build();
	}

	@Override
	protected void transformData() {
		DocumentBuilder db = XMLUtils.createBuilder();
		Document template = null;
		XFormTemplateSelector selector = new XFormTemplateSelector(getElementInfo());
		DataFile<InputStream> data =
			selector.getGateway().getRawData(getCallContext(), getElementInfo());
		try {
			template = db.parse(data.getData());
		} catch (SAXException | IOException e1) {
			throw new XMLFormatException(getElementInfo().getTemplateName(), e1);
		}

		try {
			template =
				XFormTemplateModificator.modify(template, getSource().getCallContext(),
						getElementInfo());
			logInput(template);
			html = XFormProducer.getHTML(template, getSource().getData());
			logOutput();
			replaceVariables();
			result.setXFormParts(XFormCutter.xFormParts(html));
		} catch (TransformerException | XMLStreamException e) {
			throw new XSLTTransformException(String.format(XFORMS_CREATE_ERROR, getElementInfo()
					.getFullId()), e, new DataPanelElementContext(getCallContext(),
					getElementInfo()));
		}
	}

	private void logOutput() {
		Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
		marker.add(MarkerFactory.getMarker(LastLogEvents.OUTPUT));
		marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s",
				XFormProducer.XSLTFORMS_XSL)));
		LOGGER.info(marker, html);
	}

	private void logInput(final Document template) {
		if (!LOGGER.isInfoEnabled()) {
			return;
		}
		Marker marker = MarkerFactory.getDetachedMarker(XMLUtils.XSL_MARKER);
		marker.add(MarkerFactory.getMarker(LastLogEvents.INPUT));
		marker.add(MarkerFactory.getMarker(String.format("xslTransform=%s",
				XFormProducer.XSLTFORMS_XSL)));
		LOGGER.info(marker, XMLUtils.documentToString(template));
	}

	private String replaceVariables() {
		html = super.replaceVariables(html);
		html = html.replace("xformId", getElementInfo().getId());
		addUserDataToSubmissions();
		return html;
	}

	private void addUserDataToSubmissions() {
		String servletQuery =
			ExchangeConstants.SECURED_SERVLET_PREFIX + "/" + ExchangeConstants.SUBMIT_SERVLET
					+ "?";
		String userDataParam =
			"userdata=" + AppInfoSingleton.getAppInfo().getCurUserDataId() + "&amp;";
		html = html.replace(servletQuery, servletQuery + userDataParam);
		servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/xslttransformer?";
		html = html.replace(servletQuery, servletQuery + userDataParam);
	}

	@Override
	public XForm getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new XForm();
	}

	@Override
	protected void correctSettingsAndData() {
		// ничего не нужно

	}
}
