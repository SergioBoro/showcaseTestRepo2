package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.SQLException;

import javax.xml.stream.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Вспомогательный класс для получения данных элементов инф. панели, основанных
 * на компонентах.
 * 
 * @author den
 * 
 */
public abstract class CompBasedElementSPQuery extends ElementSPQuery {

	/**
	 * Стандартная функция выполнения запроса с проверкой на возврат результата.
	 */
	protected void stdGetResults() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			boolean hasResult = execute();
			if (getElementInfo().getType() == DataPanelElementType.GEOMAP) {
				// временно, пока идет работа над XML-датасетами
				if (!hasResult) {
					checkErrorCode();
					throw new DBQueryException(getElementInfo(),
							CompBasedElementSPQuery.NO_RESULTSET_ERROR);
				}
			}
		} else {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				getConn().setAutoCommit(false);
			}
			execute();
		}
	}

	public static final String NO_RESULTSET_ERROR = "хранимая процедура не возвратила данные";

	/**
	 * Стандартный метод возврата данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - инф. об элементе.
	 */
	protected RecordSetElementRawData stdGetData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		try {
			prepareStdStatement();
			stdGetResults();
			return new RecordSetElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();

		registerOutParameterCursor();
	}

	protected abstract void registerOutParameterCursor() throws SQLException;

	private static final String SAX_ERROR_MES = "обобщенные настройки (настройки плюс данные)";

	@Override
	protected void fillValidatedSettings() throws SQLException {
		InputStream settings = getInputStreamForXMLParam(getOutSettingsParam());
		if (settings != null) {
			ByteArrayOutputStream osSettings = new ByteArrayOutputStream();
			ByteArrayOutputStream osDS = new ByteArrayOutputStream();

			SimpleSAX sax =
				new SimpleSAX(settings, new StreamDivider(osSettings, osDS), SAX_ERROR_MES);
			sax.parse();

			InputStream isSettings = StreamConvertor.outputToInputStream(osSettings);
			if (getSettingsSchema() != null) {
				setValidatedSettings(XMLUtils.xsdValidateAppDataSafe(isSettings,
						getSettingsSchema()));
			} else {
				setValidatedSettings(isSettings);
			}

			if (osDS.size() == 0) {
				setXmlDS(null);
			} else {
				setXmlDS(StreamConvertor.outputToInputStream(osDS));
			}

		} else {
			setValidatedSettings(null);
			setXmlDS(null);
		}
	}

	/**
	 * Делитель поля settings на 2 потока -- собственно сами настройки и
	 * данные(XML-датасет).
	 */
	private class StreamDivider extends DefaultHandler {

		private static final String XML_DATASET_TAG = "records";

		private final XMLStreamWriter writerSettings;
		private final XMLStreamWriter writerDS;

		private boolean forDS = false;

		StreamDivider(final OutputStream osSettings, final OutputStream osDS) {
			super();

			try {
				writerSettings =
					XMLOutputFactory.newInstance().createXMLStreamWriter(osSettings,
							TextUtils.DEF_ENCODING);
				writerDS =
					XMLOutputFactory.newInstance().createXMLStreamWriter(osDS,
							TextUtils.DEF_ENCODING);
			} catch (XMLStreamException e) {
				throw new SAXError(e);
			}
		}

		private XMLStreamWriter getWriter() {
			if (forDS) {
				return writerDS;
			} else {
				return writerSettings;
			}
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			try {
				if (XML_DATASET_TAG.equalsIgnoreCase(localName)) {
					forDS = true;
				}

				getWriter().writeStartElement(localName);

				for (int i = 0; i < atts.getLength(); i++) {
					getWriter().writeAttribute(atts.getQName(i), atts.getValue(i));
				}
			} catch (XMLStreamException e) {
				throw new SAXError(e);
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			try {
				getWriter().writeCharacters(ch, start, length);
			} catch (XMLStreamException e) {
				throw new SAXError(e);
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			try {
				getWriter().writeEndElement();

				if (XML_DATASET_TAG.equalsIgnoreCase(localName)) {
					forDS = false;
				}
			} catch (XMLStreamException e) {
				throw new SAXError(e);
			}
		}

	}

}
