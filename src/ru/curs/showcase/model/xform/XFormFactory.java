package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.html.XForm;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.model.event.HTMLBasedElementFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormFactory extends HTMLBasedElementFactory {
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
			html =
				XFormProducer.getHTML(template, getSource().getData(), getElementInfo().getId());
			replaceVariables();
			result.setXFormParts(XFormCutter.xFormParts(html));
		} catch (Exception e) {
			throw new XSLTTransformException(String.format(XFORMS_CREATE_ERROR, getElementInfo()
					.getFullId()), e, new DataPanelElementContext(getCallContext(),
					getElementInfo()));
		}
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
