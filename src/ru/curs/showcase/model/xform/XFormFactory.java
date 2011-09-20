package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.*;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.event.HTMLBasedElementFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormFactory extends HTMLBasedElementFactory {
	protected static final Logger LOGGER = LoggerFactory.getLogger(XFormFactory.class);

	private static final String LOG_TEMPLATE =
		"XSL %s \r\n userName=%s \r\n userData=%s \r\n requestId=%s \r\n commandName=%s \r\n xslTransform=%s \r\n %s";

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
		InputStream stream;
		String file =
			String.format("%s/%s", AppProps.XFORMS_DIR, getElementInfo().getTemplateName());
		try {
			stream = AppProps.loadUserDataToStream(file);

		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getElementInfo().getTemplateName(),
					SettingsFileType.XFORM);
		}

		try {
			template = db.parse(stream);
		} catch (Exception e1) {
			throw new XMLFormatException(file, e1);
		}

		try {
			template =
				XFormTemplateModificator.addSrvInfo(template, getSource().getCallContext(),
						getElementInfo());
			logInput(template);
			html = XFormProducer.getHTML(template, getSource().getData());
			logOutput();
			replaceVariables();
			result.setXFormParts(XFormCutter.xFormParts(html));
		} catch (Exception e) {
			throw new XSLTTransformException(String.format(XFORMS_CREATE_ERROR, getElementInfo()
					.getFullId()), e, new DataPanelElementContext(getCallContext(),
					getElementInfo()));
		}
	}

	private void logOutput() {
		LOGGER.info(String.format(LOG_TEMPLATE, LastLogEvents.OUTPUT, ServletUtils
				.getCurrentSessionUserName(), AppInfoSingleton.getAppInfo().getCurUserDataId(),
				getCallContext().getRequestId(), getCallContext().getCommandName(),
				XFormProducer.XSLTFORMS_XSL, html));
	}

	private void logInput(final Document template) {
		if (!LOGGER.isInfoEnabled()) {
			return;
		}
		LOGGER.info(String.format(LOG_TEMPLATE, LastLogEvents.INPUT, ServletUtils
				.getCurrentSessionUserName(), AppInfoSingleton.getAppInfo().getCurUserDataId(),
				getCallContext().getRequestId(), getCallContext().getCommandName(),
				XFormProducer.XSLTFORMS_XSL, XMLUtils.documentToString(template)));
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
