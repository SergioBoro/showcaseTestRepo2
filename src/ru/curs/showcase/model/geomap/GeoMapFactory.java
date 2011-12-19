package ru.curs.showcase.model.geomap;

import java.sql.*;

import javax.sql.RowSet;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.model.event.EventFactory;
import ru.curs.showcase.model.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Фабрика для создания карт для информационной панели.
 * 
 * @author den
 * 
 */
public final class GeoMapFactory extends AbstractGeoMapFactory {
	private static final String CODE_TAG = "Code";
	private static final String IS_MAIN_TAG = "IsMain";
	private static final String TOOLTIP_COL = "Tooltip";
	private static final String WRONG_LAYER_ERROR =
		"В переданных данных найден объект, ссылающийся на несуществующий слой";
	private static final String NO_IND_VALUES_TABLE_ERROR =
		"Не передана таблица со значениями показателей для объектов на карте";
	private static final String NO_POINTS_TABLE_ERROR = "Не передана таблица с точками для карты";
	private static final String WRONG_OBJ_ERROR =
		"В переданных данных найдено значение показателя, ссылающееся на несуществующий объект";
	private static final String INDICATOR_ID = "IndicatorID";
	private static final String POLYGON_TO_POINT_LAYER_ERROR =
		"В слой типа 'точки' нельзя добавлять области";

	private static final String POINT_TO_POLYGON_LAYER_ERROR =
		"В слой типа 'области' нельзя добавлять точки";

	/**
	 * Таблица с данными о слоях.
	 */
	private RowSet layersSql;

	/**
	 * Таблица с данными о выделенных областях.
	 */
	private RowSet areasSql;

	/**
	 * Таблица с данными о точках.
	 */
	private RowSet pointsSql;

	/**
	 * Таблица с данными о показателях для всех слоев.
	 */
	private RowSet indicatorsSql;

	/**
	 * Таблица с данными о значениях показателей.
	 */
	private RowSet indicatorValuesSql;

	public GeoMapFactory(final RecordSetElementRawData aSource) {
		super(aSource);
	}

	@Override
	protected void fillLayers() throws SQLException {
		while (layersSql.next()) {
			String value = layersSql.getString(OBJECT_TYPE_TAG).toUpperCase().trim();
			GeoMapLayer layer = getData().addLayer(GeoMapFeatureType.valueOf(value));
			layer.setId(layersSql.getString(ID_TAG.toUpperCase()));
			layer.setName(layersSql.getString(NAME_TAG.toUpperCase()));
			if (SQLUtils.existsColumn(layersSql.getMetaData(),
					TextUtils.capitalizeWord(HINT_FORMAT_TAG))) {
				layer.setHintFormat(layersSql.getString(TextUtils.capitalizeWord(HINT_FORMAT_TAG)));
			}

		}
	}

	private GeoMapData getData() {
		return getResult().getJavaDynamicData();
	}

	@Override
	protected void fillPolygons() throws SQLException {
		if (areasSql == null) {
			return;
		}
		while (areasSql.next()) {
			GeoMapLayer layer = getLayerForObject(areasSql);
			GeoMapFeature area =
				layer.addPolygon(areasSql.getString(ID_TAG.toUpperCase()),
						areasSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (area == null) {
				throw new InconsistentSettingsFromDBException(POLYGON_TO_POINT_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), CODE_TAG)) {
				area.setGeometryId(areasSql.getString(CODE_TAG));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), TextUtils.capitalizeWord(COLOR_TAG))) {
				area.setStyle(areasSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(),
					TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG))) {
				area.setStyleClass(areasSql.getString(TextUtils
						.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), TOOLTIP_COL)) {
				String value = areasSql.getString(TOOLTIP_COL);
				if (value != null) {
					area.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(areasSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(area.getId(), areasSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillPoints() throws SQLException {
		while (pointsSql.next()) {
			GeoMapLayer layer = getLayerForObject(pointsSql);
			GeoMapFeature point =
				layer.addPoint(pointsSql.getString(ID_TAG.toUpperCase()),
						pointsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (point == null) {
				throw new InconsistentSettingsFromDBException(POINT_TO_POLYGON_LAYER_ERROR);
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), LAT_TAG)
					&& SQLUtils.existsColumn(pointsSql.getMetaData(), LON_TAG)) {
				Double[] coords = { pointsSql.getDouble(LON_TAG), pointsSql.getDouble(LAT_TAG) };
				point.setPointCoords(coords);

			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), TOOLTIP_COL)) {
				String value = pointsSql.getString(TOOLTIP_COL);
				if (value != null) {
					point.setTooltip(value);
				}
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(),
					TextUtils.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG))) {
				point.setStyleClass(pointsSql.getString(TextUtils
						.capitalizeWord(GeneralConstants.STYLE_CLASS_TAG)));
			}
			if (SQLUtils.existsColumn(pointsSql.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(point.getId(), pointsSql.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	@Override
	protected void fillIndicators() throws SQLException {
		if (indicatorsSql == null) {
			return;
		}
		while (indicatorsSql.next()) {
			GeoMapLayer layer = getLayerForObject(indicatorsSql);
			GeoMapIndicator indicator =
				layer.addIndicator(indicatorsSql.getString(ID_TAG.toUpperCase()),
						indicatorsSql.getString(TextUtils.capitalizeWord(NAME_TAG)));
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(), IS_MAIN_TAG)) {
				indicator.setIsMain(indicatorsSql.getBoolean(IS_MAIN_TAG));
			}
			if (SQLUtils.existsColumn(indicatorsSql.getMetaData(),
					TextUtils.capitalizeWord(COLOR_TAG))) {
				indicator.setStyle(indicatorsSql.getString(TextUtils.capitalizeWord(COLOR_TAG)));
			}
		}
	}

	@Override
	protected void fillIndicatorValues() throws SQLException {
		if (indicatorValuesSql == null) {
			return;
		}
		while (indicatorValuesSql.next()) {
			String objectId = indicatorValuesSql.getString(OBJECT_ID_TAG);
			GeoMapLayer layer = getData().getLayerByObjectId(objectId);
			if (layer == null) {
				throw new ResultSetHandleException(WRONG_OBJ_ERROR);
			}
			GeoMapFeature feature = layer.getObjectById(objectId);
			Double value = indicatorValuesSql.getDouble(TextUtils.capitalizeWord(VALUE_TAG));
			String dbId = indicatorValuesSql.getString(INDICATOR_ID);
			feature.setValue(layer.getAttrIdByDBId(dbId), value);
		}

	}

	private GeoMapLayer getLayerForObject(final RowSet rowset) throws SQLException {
		String layerId = rowset.getString(LAYER_ID_TAG);
		GeoMapLayer layer = getData().getLayerById(layerId);
		if (layer == null) {
			throw new ResultSetHandleException(WRONG_LAYER_ERROR);
		}
		return layer;
	}

	private void readEvents(final String objectId, final String value) {
		EventFactory<GeoMapEvent> factory =
			new EventFactory<GeoMapEvent>(GeoMapEvent.class, getCallContext());
		factory.initForGetSimpleSubSetOfEvents(getElementInfo().getType().getPropsSchemaName());
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(objectId, value));
	}

	@Override
	protected void prepareData() {
		try {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				ResultSet rs = getSource().nextResultSet();
				layersSql = SQLUtils.cacheResultSet(rs);

				rs = getSource().nextResultSet();
				if (rs == null) {
					throw new ResultSetHandleException(NO_POINTS_TABLE_ERROR);
				}
				pointsSql = SQLUtils.cacheResultSet(rs);

				rs = getSource().nextResultSet();
				if (rs == null) {
					return; // разрешаем создавать карту, содержащую только
							// точки -
							// например карту региона.
				}
				areasSql = SQLUtils.cacheResultSet(rs);

				rs = getSource().nextResultSet();
				if (rs == null) {
					return; // разрешаем создавать карту без показателей.
				}
				indicatorsSql = SQLUtils.cacheResultSet(rs);

				rs = getSource().nextResultSet();
				if (rs == null) {
					throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
				}
				indicatorValuesSql = SQLUtils.cacheResultSet(rs);
			} else {
				CallableStatement cs = (CallableStatement) getSource().getStatement();
				ResultSet rs =
					(ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_1);
				layersSql = SQLUtils.cacheResultSet(rs);

				if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_2)) {
					throw new ResultSetHandleException(NO_POINTS_TABLE_ERROR);
				}
				rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_2);
				pointsSql = SQLUtils.cacheResultSet(rs);

				if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_3)) {
					return; // разрешаем создавать карту, содержащую только
							// точки -
							// например карту региона.
				}
				rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_3);
				areasSql = SQLUtils.cacheResultSet(rs);

				if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_4)) {
					return; // разрешаем создавать карту без показателей
				}
				rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_4);
				indicatorsSql = SQLUtils.cacheResultSet(rs);

				if (!isCursorOpen(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_5)) {
					throw new ResultSetHandleException(NO_IND_VALUES_TABLE_ERROR);
				}
				rs = (ResultSet) cs.getObject(GeoMapDBGateway.ORA_CURSOR_INDEX_DATA_AND_SETTINS_5);
				indicatorValuesSql = SQLUtils.cacheResultSet(rs);

			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private boolean isCursorOpen(final int index) {
		try {
			((CallableStatement) getSource().getStatement()).getObject(index);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	protected void fillResultByData() {
		try {
			super.fillResultByData();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}
}
