package ru.curs.showcase.model.event;

import java.sql.*;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.*;

/**
 * Абстрактная фабрика для элементов инф. панели, основанных на компонентах -
 * GRID, CHART, GEOMAP.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementFactory extends TemplateMethodFactory {

	@Override
	public abstract DataPanelCompBasedElement getResult();

	public CompBasedElementFactory(final ElementRawData aSource) {
		super(aSource);
	}

	/**
	 * Конкретный обработчик для считывания динамических настроек в процедуре
	 * setupDynamicSettings.
	 * 
	 * @return - обработчик.
	 */
	protected abstract SAXTagHandler getConcreteHandler();

	@Override
	protected void setupDynamicSettings() {
		final SAXTagHandler addHandler = getConcreteHandler();
		DefaultHandler myHandler = new DefaultHandler() {

			private boolean readingHeader = false;
			private boolean readingFooter = false;
			private final ActionFactory actionFactory = new ActionFactory(getCallContext());

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(HEADER_TAG)) {
					readingHeader = true;
					getResult().setHeader("");
					return;
				}
				if (qname.equalsIgnoreCase(FOOTER_TAG)) {
					readingFooter = true;
					getResult().setFooter("");
					return;
				}
				if (addHandler != null) {
					if (addHandler.canHandleStartTag(qname)) {
						addHandler.handleStartTag(namespaceURI, lname, qname, attrs);
						return;
					}
				}
				if (actionFactory.canHandleStartTag(qname)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					getResult().setDefaultAction(action);
					return;
				}
				if (readingHeader) {
					addToHeader(XMLUtils.saxTagWithAttrsToString(qname, attrs));
					return;
				}
				if (readingFooter) {
					addToFooter(XMLUtils.saxTagWithAttrsToString(qname, attrs));
					return;
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(HEADER_TAG)) {
					readingHeader = false;
					getResult().setHeader(getResult().getHeader().trim());
					return;
				}
				if (qname.equalsIgnoreCase(FOOTER_TAG)) {
					readingFooter = false;
					getResult().setFooter(getResult().getFooter().trim());
					return;
				}
				if (addHandler != null) {
					if (addHandler.canHandleEndTag(qname)) {
						addHandler.handleEndTag(namespaceURI, lname, qname);
						return;
					}
				}

				if (actionFactory.canHandleEndTag(qname)) {
					Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
					getResult().setDefaultAction(action);
					return;
				}

				if (readingHeader) {
					addToHeader("</" + qname + ">");
					return;
				}
				if (readingFooter) {
					addToFooter("</" + qname + ">");
					return;
				}
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				if (readingHeader) {
					addToHeader(String.copyValueOf(arg0, arg1, arg2));
					return;
				}
				if (readingFooter) {
					addToFooter(String.copyValueOf(arg0, arg1, arg2));
					return;
				}
				if (addHandler != null) {
					addHandler.handleCharacters(arg0, arg1, arg2);
				}
				actionFactory.handleCharacters(arg0, arg1, arg2);
			}
		};

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(getSettings(), myHandler);
		} catch (Exception e) {
			XMLUtils.stdSAXErrorHandler(e, getSettingsErrorMes());
		}
	}

	/**
	 * Возвращает специфичную для типа элемента часть ошибки при разборе
	 * настроек элемента.
	 * 
	 * @return - строка с ошибкой.
	 */
	protected abstract String getSettingsErrorMes();

	@Override
	protected void correctSettingsAndData() {
		String html = getResult().getHeader();
		html = replaceVariables(html);
		getResult().setHeader(html);
		html = getResult().getFooter();
		html = replaceVariables(html);
		getResult().setFooter(html);
	}

	@Override
	protected void prepareSettings() {
		getSource().prepareSettings();
	}

	@Override
	protected void releaseResources() {
		getSource().releaseResources();
	}

	private void addToHeader(final String data) {
		getResult().setHeader(getResult().getHeader() + data);
	}

	private void addToFooter(final String data) {
		getResult().setFooter(getResult().getFooter() + data);
	}

	protected ResultSet getResultSetAccordingToSQLServerType(final CallableStatement cs)
			throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return cs.getResultSet();
		} else {
			try {
				return (ResultSet) cs.getObject(1);
			} catch (Exception e) {
				return (ResultSet) cs.getObject(cs.getParameterMetaData().getParameterCount());
			}
		}
	}

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
	
		getSource().getSpCallHelper().checkErrorCode();
	}
}
