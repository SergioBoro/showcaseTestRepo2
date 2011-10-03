package ru.curs.showcase.model.geomap;

import java.sql.SQLException;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.element.LegendPosition;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.model.event.CompBasedElementFactory;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.SAXTagHandler;

/**
 * Класс абстрактной фабрики карт - не содержащий кода для считывания данных из
 * конкретного источника типа SQL ResultSet.
 * 
 * @author den
 * 
 */
public abstract class AbstractGeoMapFactory extends CompBasedElementFactory {

	private static final String OBJECT_NAME_TAG = "ObjectName";
	private static final String LAYER_NAME_TAG = "LayerName";
	/**
	 * Заголовок ошибки при считывании настроек карты.
	 */
	private static final String MAP_ERROR_CAPTION = "настройки карты";
	protected static final String LAYER_ID_TAG = "LayerID";
	protected static final String OBJECT_TYPE_TAG = "ObjectType";
	protected static final String OBJECT_ID_TAG = "ObjectID";
	protected static final String LAT_TAG = "Lat";
	protected static final String LON_TAG = "Lon";

	/**
	 * Результат работы фабрики - карта.
	 */
	private GeoMap result;

	public AbstractGeoMapFactory(final ElementRawData aSource) {
		super(aSource);
	}

	@Override
	public GeoMap getResult() {
		return result;
	}

	@Override
	public GeoMap build() throws Exception {
		return (GeoMap) super.build();
	}

	@Override
	protected void fillResultByData() throws SQLException {
		fillLayers();
		fillPolygons();
		fillPoints();
		fillIndicators();
		correctIndicators();
		fillIndicatorValues();
	}

	/**
	 * Функция генерирует правильные ID для всех показателей, в частности
	 * устанавливает ID показателя для раскраски полигонов на всех слоях равным
	 * специальному значению MAIN_IND_NAME. Это связано с тем, что в шаблоне
	 * сейчас задается одна функция для расчета цветов для раскраски, и поэтому
	 * нужно единое название показателя.
	 */
	private void correctIndicators() {
		for (GeoMapLayer layer : result.getJavaDynamicData().getLayers()) {
			layer.generateIndicatorsIds();
		}
	}

	/**
	 * Функция заполнения данных о слоях.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillLayers() throws SQLException;

	/**
	 * Функция заполнения данных о полигонах.
	 * 
	 */
	protected abstract void fillPolygons() throws SQLException;

	/**
	 * Функция заполнения данных о точках.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillPoints() throws SQLException;

	/**
	 * Функция заполнения данных об показателях.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillIndicators() throws SQLException;

	/**
	 * Функция заполнения данных об значениях показателей.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillIndicatorValues() throws SQLException;

	@Override
	protected void initResult() {
		result = new GeoMap(new GeoMapData());
	}

	/**
	 * Класс считывателя настроек карты.
	 * 
	 * @author den
	 * 
	 */
	private class MapDynamicSettingsReader extends SAXTagHandler {
		/**
		 * Стартовые тэги, которые будут обработаны.
		 */
		private final String[] startTags = { TEMPLATE_TAG, PROPS_TAG };

		/**
		 * Закрывающие тэги, которые будут обрабатываться.
		 */
		private final String[] endTags = { TEMPLATE_TAG };

		/**
		 * Признак чтения шаблона.
		 */
		private boolean readingTemplate = false;

		@Override
		public Object handleStartTag(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			String value;
			Integer intValue = null;
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = true;
				getResult().setTemplate("");
				return null;
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				value = attrs.getValue(LEGEND_TAG);
				value = value.toUpperCase().trim();
				getResult().setLegendPosition(LegendPosition.valueOf(value));
				if (attrs.getIndex(WIDTH_TAG) > -1) {
					value = attrs.getValue(WIDTH_TAG);
					intValue = TextUtils.getIntSizeValue(value);
					getResult().getJavaDynamicData().setWidth(intValue);
				}
				if (attrs.getIndex(HEIGHT_TAG) > -1) {
					value = attrs.getValue(HEIGHT_TAG);
					intValue = TextUtils.getIntSizeValue(value);
					getResult().getJavaDynamicData().setHeight(intValue);
				}
				return null;
			}
			return null;
		}

		@Override
		public Object handleEndTag(final String namespaceURI, final String lname,
				final String qname) {
			if (qname.equalsIgnoreCase(TEMPLATE_TAG)) {
				readingTemplate = false;
				return null;
			}
			return null;
		}

		@Override
		public void handleCharacters(final char[] arg0, final int arg1, final int arg2) {
			if (readingTemplate) {
				getResult().setTemplate(
						getResult().getTemplate() + String.copyValueOf(arg0, arg1, arg2));
				return;
			}
		}

		@Override
		protected String[] getStartTags() {
			return startTags;
		}

		@Override
		protected String[] getEndTrags() {
			return endTags;
		}
	}

	@Override
	protected SAXTagHandler getConcreteHandler() {
		return new MapDynamicSettingsReader();
	}

	@Override
	protected String getSettingsErrorMes() {
		return MAP_ERROR_CAPTION;
	}

	@Override
	protected void correctSettingsAndData() {
		super.correctSettingsAndData();

		setupHints();
		getResult().determineAutoSize();
		replaceVariablesInTemplate();
	}

	private void replaceVariablesInTemplate() {
		getResult().setTemplate(replaceVariables(getResult().getTemplate()));
	}

	private void setupHints() {
		for (GeoMapLayer layer : result.getJavaDynamicData().getLayers()) {
			if (layer.getHintFormat() == null) {
				continue;
			}
			for (GeoMapFeature obj : layer.getFeatures()) {
				if (obj.getTooltip() == null) { // явная подсказка приоритетна
					String toolTip = generateTooltip(layer, obj);
					obj.setTooltip(toolTip);
				}
			}
			layer.setHintFormat(null); // теперь шаблон не нужен
		}
	}

	private String generateTooltip(final GeoMapLayer layer, final GeoMapObject obj) {
		String toolTip = layer.getHintFormat();
		toolTip = TextUtils.replaceCI(toolTip, "%" + LAYER_ID_TAG, layer.getId());
		toolTip = TextUtils.replaceCI(toolTip, "%" + LAYER_NAME_TAG, layer.getName());
		toolTip = TextUtils.replaceCI(toolTip, "%" + OBJECT_TYPE_TAG, layer.getType().toString());
		toolTip = TextUtils.replaceCI(toolTip, "%" + OBJECT_ID_TAG, obj.getId());
		toolTip = TextUtils.replaceCI(toolTip, "%" + OBJECT_NAME_TAG, obj.getName());
		if (layer.getType() == GeoMapFeatureType.POINT) {
			toolTip =
				TextUtils.replaceCI(toolTip, "%" + LAT_TAG, ((GeoMapFeature) obj).getLat()
						.toString());
			toolTip =
				TextUtils.replaceCI(toolTip, "%" + LON_TAG, ((GeoMapFeature) obj).getLon()
						.toString());
		}
		return toolTip;
	}
}
