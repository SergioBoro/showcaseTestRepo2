package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.element.DataPanelElementWithLegend;

/**
 * Класс карты - элемента информационной панели. На карте отображаются
 * географические данные.
 * 
 * @author den
 * 
 */
public final class GeoMap extends DataPanelElementWithLegend {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3147374218653835207L;

	/**
	 * Динамические данные Java.
	 */
	private GeoMapData javaDynamicData;

	/**
	 * Признак того, что размеры карты должны подстраиватся под размеры инф.
	 * панели. Данный режим включается, если не заданы размеры карты.
	 */
	private Boolean autoSize = false;

	public GeoMap(final GeoMapData aGeoMapData) {
		super();
		javaDynamicData = aGeoMapData;
	}

	public GeoMap() {
		super();
	}

	@Override
	protected GeoMapEventManager initEventManager() {
		return new GeoMapEventManager();
	}

	@Override
	public GeoMapEventManager getEventManager() {
		return (GeoMapEventManager) super.getEventManager();
	}

	@Override
	public GeoMapData getJavaDynamicData() {
		return javaDynamicData;
	}

	@Override
	public void resetJavaDynamicData() {
		javaDynamicData = null;
	}

	public void setJavaDynamicData(final GeoMapData aJavaDynamicData) {
		javaDynamicData = aJavaDynamicData;
	}

	public Boolean getAutoSize() {
		return autoSize;
	}

	public void setAutoSize(final Boolean aAutoSize) {
		autoSize = aAutoSize;
	}

	/**
	 * Функция для определения значения признака autoSize. Должна быть вызвана
	 * до сериализации карты в JSON, например из фабрики.
	 */
	public void determineAutoSize() {
		autoSize = ((javaDynamicData.getHeight() == null) && (javaDynamicData.getWidth() == null));
	}
}
