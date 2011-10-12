package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.geomap.*;

/**
 * Тесты для фабрики карт.
 * 
 * @author den
 * 
 */
public class GeoMapFactoryTest extends AbstractTestWithDefaultUserData {

	private static final String RU_AL_ID = "2";

	/**
	 * Простой тест на создание объекта карты.
	 */
	@Test
	public void testSimpleCreateObj() {
		GeoMap map = new GeoMap();
		assertNotNull(map.getJavaDynamicData());
		assertNull(map.getJsDynamicData());
		map.getJavaDynamicData().addLayer(GeoMapFeatureType.POLYGON);
		assertEquals(GeoMapFeatureType.POLYGON, map.getJavaDynamicData().getLayers().get(0)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(0).getFeatures());
		GeoMapLayer layer = map.getJavaDynamicData().addLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapFeatureType.POINT, map.getJavaDynamicData().getLayers().get(1)
				.getType());
		assertNotNull(map.getJavaDynamicData().getLayers().get(1).getFeatures());
		assertNull(layer.addPolygon("aa", "bb"));
		final String pointId = "pointId1";
		final String pointName = "Москва";
		assertNotNull(layer.addPoint(pointId, pointName));
		GeoMapFeature feature = layer.getFeatures().get(0);
		assertEquals(pointId, feature.getId());
		assertEquals(pointName, feature.getName());
		final String indId = "12345";
		final String indName = "Надои";
		GeoMapIndicator ind = layer.addIndicator(indId, indName);
		assertNotNull(ind);
		assertEquals(indId, ind.getId());
		assertEquals(indName, layer.getIndicatorById(indId).getName());
		final double value = 1.0;
		feature.setValue(indId, value);
		assertEquals(value, feature.getValueForIndicator(ind).doubleValue(), 0);
		assertEquals(false, ind.getIsMain());

		assertTrue(map.getAutoSize());
		assertEquals(GeoMapData.AUTOSIZE_CONSTANT, map.getJavaDynamicData().getHeight());
		assertEquals(GeoMapData.AUTOSIZE_CONSTANT, map.getJavaDynamicData().getWidth());
	}

	/**
	 * Тест на проверку динамических свойств карты, созданной на основе данных
	 * из БД.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFromDBDynamicData() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "05");

		GeoMapGateway gateway = new GeoMapDBGateway();
		ElementRawData raw = gateway.getRawData(context, element);
		GeoMapFactory factory = new GeoMapFactory(raw);
		GeoMap map = factory.build();

		final int size = 500;
		GeoMapData data = map.getJavaDynamicData();
		assertEquals(size, data.getHeight().intValue());
		assertEquals(size, data.getWidth().intValue());
		assertEquals(2, data.getLayers().size());
		assertNotNull(data.getLayerById("l1"));
		assertNotNull(data.getLayerById("l2"));
		assertNull(data.getLayerById("l3"));
		assertNotNull(data.getLayerByObjectId("1849"));
		GeoMapLayer layer = data.getLayerByObjectId(RU_AL_ID);
		assertNotNull(layer);
		assertNull(layer.getProjection());
		assertNull(data.getLayerByObjectId("blaaa"));
		assertNull(layer.getHintFormat());
		assertEquals(GeoMapFeatureType.POLYGON, layer.getType());
		final int areasCount = 5;
		assertEquals(areasCount, layer.getFeatures().size());
		assertEquals(2, layer.getIndicators().size());
		GeoMapFeature altay = layer.getObjectById(RU_AL_ID);
		assertEquals("Алтай - регион с показателями", altay.getTooltip());
		assertEquals("#FAEC7B", altay.getStyle());
		assertEquals(altay.getGeometryId(), altay.getStyleClass());
		final int indValue1 = 1000;
		assertNotSame("ind1", layer.getIndicators().get(0).getId());
		assertEquals("ind0", layer.getAttrIdByDBId("ind1"));
		assertEquals(false, layer.getIndicators().get(0).getIsMain());
		assertEquals("#2AAA2E", layer.getIndicators().get(0).getStyle());
		assertEquals(indValue1, altay.getValueForIndicator(layer.getIndicators().get(0))
				.doubleValue(), 0);
		final int indValue2 = 10;
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getIndicators().get(1).getId());
		assertEquals(GeoMapLayer.MAIN_IND_NAME, layer.getAttrIdByDBId("ind2"));
		assertEquals(layer.getMainIndicator(), layer.getIndicators().get(1));
		assertEquals(true, layer.getIndicators().get(1).getIsMain());
		assertEquals(indValue2, altay.getValueForIndicator(layer.getIndicators().get(1))
				.doubleValue(), 0);
		layer = data.getLayerById("l1");
		GeoMapFeature novgorod = layer.getObjectById("2532");
		assertEquals(String.format("%s - %s (%s) (%s - %s)", layer.getName(), novgorod.getName(),
				novgorod.getId(), novgorod.getLat(), novgorod.getLon()), novgorod.getTooltip());
		assertEquals("TestStyleClass", novgorod.getStyleClass());
	}

	@Test
	public void testApplyAutoSizeValuesOnClient() {
		final int height = 200;
		final int width = 100;
		GeoMap map = new GeoMap();
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(map);

		assertTrue(map.getJsDynamicData().indexOf("\"width\":" + GeoMapData.AUTOSIZE_CONSTANT) > -1);
		assertTrue(map.getJsDynamicData().indexOf("\"height\":" + GeoMapData.AUTOSIZE_CONSTANT) > -1);
		map.applyAutoSizeValuesOnClient(width, height);
		assertTrue(map.getJsDynamicData().indexOf("\"width\":" + width) > -1);
		assertTrue(map.getJsDynamicData().indexOf("\"height\":" + height) > -1);
	}

	/**
	 * Проверка автоматической установки проекции слоя.
	 */
	@Test
	public void testLayerProjection() {
		GeoMapLayer layer = new GeoMapLayer(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.POLYGON);
		assertNull(layer.getProjection());
		layer = new GeoMapLayer(GeoMapFeatureType.MULTIPOLYGON);
		assertNull(layer.getProjection());
		layer.setType(GeoMapFeatureType.POINT);
		assertEquals(GeoMapLayer.DEF_POINT_PROJECTION, layer.getProjection());
	}

}
