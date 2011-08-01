package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.html.XForms;
import ru.curs.showcase.app.server.AppInfoSingleton;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.event.HTMLBasedElementFactory;
import ru.curs.showcase.util.*;

/**
 * Фабрика по созданию объектов XForms.
 * 
 * @author den
 * 
 */
public final class XFormsFactory extends HTMLBasedElementFactory {
	private static final String XFORMS_CREATE_ERROR =
		"Ошибка при формировании XForms для элемента '%s'";

	/**
	 * Результат работы фабрики.
	 */
	private XForms result;

	/**
	 * Промежуточный результат работы фабрики.
	 */
	private String html;

	public XFormsFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public XForms build() throws Exception {
		return (XForms) super.build();
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
				XFormsTemplateModificator.addSrvInfo(template, getSource().getCallContext(),
						getElementInfo());
			html =
				XFormProducer.getHTML(template, getSource().getData(), getElementInfo().getId());
			replaceVariables();
			result.setXFormParts(XFormCutter.xFormParts(html));
		} catch (Exception e) {
			throw new XSLTTransformException(String.format(XFORMS_CREATE_ERROR, getElementInfo()
					.getFullId()), e);
		}
	}

	private String replaceVariables() {
		html = super.replaceVariables(html);
		html = html.replace("xformId", getElementInfo().getId());
		addUserDataToSubmissions();
		return html;
	}

	private void addUserDataToSubmissions() {
		String servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/submit?";
		String userDataParam =
			"?userdata=" + AppInfoSingleton.getAppInfo().getCurUserDataId() + "&amp;";
		html = html.replace(servletQuery, servletQuery + userDataParam);
		servletQuery = ExchangeConstants.SECURED_SERVLET_PREFIX + "/xslttransformer?";
		html = html.replace(servletQuery, servletQuery + userDataParam);
	}

	@Override
	public XForms getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new XForms();
	}
}
