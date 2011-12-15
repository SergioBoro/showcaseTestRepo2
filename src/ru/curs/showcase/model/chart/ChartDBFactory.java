package ru.curs.showcase.model.chart;

import java.sql.*;

import javax.sql.RowSet;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.chart.*;
import ru.curs.showcase.model.event.EventFactory;
import ru.curs.showcase.model.sp.*;
import ru.curs.showcase.util.SQLUtils;
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
	private RowSet sql;

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
			ResultSet rs = getResultSetAccordingToSQLServerType(getSource().getStatement());
			sql = SQLUtils.cacheResultSet(rs);
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
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
				.addAll(factory.getSubSetOfEvents(series.getName(), value));
	}
}
