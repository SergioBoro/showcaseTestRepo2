package ru.curs.showcase.core.chart;

import java.io.*;
import java.sql.*;

import javax.sql.RowSet;
import javax.xml.stream.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания графика на основе данных из БД.
 * 
 * @author den
 * 
 */
public class ChartDBFactory extends AbstractChartFactory {
	/**
	 * SQL ResultSet с данными грида.
	 */
	private RowSet sql = null;

	/**
	 * Признак того, что в RecordSet заданы события.
	 */
	private boolean eventsDefined = false;

	/**
	 * Номер записи, содержащей данные о событии. Используется только в случае
	 * транспонированных данных.
	 */
	private Integer eventRowNumber = 0;

	public ChartDBFactory(final RecordSetElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void prepareData() {
		try {
			ResultSet rs = getResultSetAccordingToSQLServerType(getSource());
			if (rs != null) {
				sql = SQLUtils.cacheResultSet(rs);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
		if ((sql == null) && (getXmlDS() == null)) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}

	private void resetRowPosition() {
		try {
			sql.beforeFirst();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void fillLabelsX() {
		if (isFlip()) {
			fillLabelsXIfFlip();
			resetRowPosition();
		} else {
			fillLabelsXIfNotFlip();
		}
	}

	private void fillLabelsXIfFlip() {
		addZeroLabelForX();
		try {
			int counter = 1;
			while (sql.next()) {
				String value = sql.getString(getSelectorColumn());
				if (PROPERTIES_SQL_TAG.equals(value)) {
					eventsDefined = true;
					eventRowNumber = counter;
					continue;
				}
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counter++);
				curLabel.setText(value);
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void fillLabelsXIfNotFlip() {
		ResultSetMetaData md;
		try {
			md = sql.getMetaData();
		} catch (SQLException e1) {
			throw new ResultSetHandleException(e1);
		}
		try {
			addZeroLabelForX();
			int counter = 1;
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (getSelectorColumn().equals(md.getColumnLabel(i))) {
					continue;
				}
				if (PROPERTIES_SQL_TAG.equals(md.getColumnLabel(i))) {
					eventsDefined = true;
					continue;
				}
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counter++);
				curLabel.setText(md.getColumnLabel(i));
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		} catch (SQLException e2) {
			throw new ResultSetHandleException(e2);
		}
	}

	private void addZeroLabelForX() {
		ChartLabel curLabel;
		curLabel = new ChartLabel();
		curLabel.setValue(0);
		curLabel.setText("");
		getResult().getJavaDynamicData().getLabelsX().add(curLabel);
	}

	@Override
	protected void fillSeries() {
		if (isFlip()) {
			fillSeriesIfFlip();
		} else {
			fillSeriesIfNotFlip();
		}
	}

	private void fillSeriesIfFlip() {
		ResultSetMetaData md;
		try {
			md = sql.getMetaData();

			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (getSelectorColumn().equals(md.getColumnLabel(i))) {
					continue;
				}
				ChartSeries series = new ChartSeries();
				series.setName(md.getColumnLabel(i));
				while (sql.next()) {
					String value = sql.getString(md.getColumnLabel(i));
					if (sql.getRow() == eventRowNumber) {
						readEvents(series, value);
					} else {
						addValueToSeries(series, value);
					}
				}
				getResult().getJavaDynamicData().getSeries().add(series);
				resetRowPosition();
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void addValueToSeries(final ChartSeries series, final String value) {
		if (value != null) {
			series.addValue(Double.valueOf(value));
		} else {
			series.addValue(null);
		}
	}

	private void fillSeriesIfNotFlip() {
		try {
			while (sql.next()) {
				ChartSeries series = new ChartSeries();
				series.setName(sql.getString(getSelectorColumn()));

				boolean skipZeroLabelForX = true;
				for (ChartLabel label : getResult().getJavaDynamicData().getLabelsX()) {
					if (skipZeroLabelForX) {
						skipZeroLabelForX = false;
						continue;
					}
					String value = sql.getString(label.getText());
					addValueToSeries(series, value);
				}
				if (eventsDefined) {
					readEvents(series, sql.getString(PROPERTIES_SQL_TAG));
				}
				getResult().getJavaDynamicData().getSeries().add(series);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void readEvents(final ChartSeries series, final String value) {
		EventFactory<ChartEvent> factory =
			new EventFactory<ChartEvent>(ChartEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(X_TAG, VALUE_TAG, getElementInfo().getType()
				.getPropsSchemaName());
		SAXTagHandler colorHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				series.setColor(attrs.getValue(VALUE_TAG));
				return series;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { COLOR_TAG };
				return tags;
			}

		};
		factory.addHandler(colorHandler);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(new ID(series.getName()), value));
	}

	private static final String SAX_ERROR_MES = "XML-датасет графика";

	@Override
	protected void fillLabelsXAndSeriesByXmlDS() {

		addZeroLabelForX();

		SimpleSAX sax = new SimpleSAX(getXmlDS(), new XmlDSHandler(), SAX_ERROR_MES);
		sax.parse();

		try {
			getXmlDS().close();
			setXmlDS(null);
			getSource().setXmlDS(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

	}

	/**
	 * Формирует LabelsX и Series на основе XML-датасета.
	 */
	private class XmlDSHandler extends DefaultHandler {

		private boolean processRecord = false;
		private boolean processValue = false;
		private boolean processProps = false;
		private boolean processSelectorColumn = false;

		private int counterLabel = 1;
		private int counterRecord = 0;
		private ChartSeries series = null;
		private String value = "";

		private ByteArrayOutputStream osProps = null;
		private XMLStreamWriter writerProps = null;

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				counterRecord++;
				processRecord = true;
				series = new ChartSeries();
				return;
			}

			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = true;
				return;
			}

			if (PROPS_TAG.equals(localName)) {
				processProps = true;
				osProps = new ByteArrayOutputStream();
				try {
					writerProps =
						XMLOutputFactory.newInstance().createXMLStreamWriter(osProps,
								TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (processProps) {
				try {
					writerProps.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerProps.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			if (!processRecord || processProps || processSelectorColumn) {
				return;
			}

			processValue = true;
			value = "";

			if (counterRecord == 1) {
				ChartLabel curLabel = new ChartLabel();
				curLabel.setValue(counterLabel++);
				curLabel.setText(XMLUtils.unEscapeTagXml(localName));
				getResult().getJavaDynamicData().getLabelsX().add(curLabel);
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (processSelectorColumn) {
				String name = new String(ch, start, length);
				if (series.getName() != null) {
					name = series.getName() + name;
				}
				series.setName(name);
				return;
			}

			if (processProps) {
				try {
					writerProps.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}

			if (processValue) {
				value = value + new String(ch, start, length);
				return;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (getSelectorColumn().equals(localName)) {
				processSelectorColumn = false;
				return;
			}

			if (processProps) {
				try {
					writerProps.writeEndElement();
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}

			if (PROPS_TAG.equals(localName)) {
				try {
					readEvents(series, osProps.toString(TextUtils.DEF_ENCODING));
					writerProps.close();
				} catch (UnsupportedEncodingException | XMLStreamException e) {
					throw new SAXError(e);
				}
				processProps = false;
				return;
			}

			if (RECORD_TAG.equals(localName)) {
				processRecord = false;
				getResult().getJavaDynamicData().getSeries().add(series);
				return;
			}

			if (processValue) {
				addValueToSeries(series, value);
				processValue = false;
			}
		}
	}

}
