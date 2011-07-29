package ru.curs.showcase.app.api.geomap;

import java.util.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.element.Size;

/**
 * Динамические данные для карты.
 * 
 * @author den
 * 
 */
public class GeoMapData extends Size implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3552200592580335858L;

	/**
	 * Коллекция слоев на карте.
	 */
	private List<GeoMapLayer> layers = new ArrayList<GeoMapLayer>();

	public final List<GeoMapLayer> getLayers() {
		return layers;
	}

	public final void setLayers(final List<GeoMapLayer> aLayers) {
		layers = aLayers;
	}

	/**
	 * Добавляет слой с определенным типом объектов.
	 * 
	 * @param aObjectType
	 *            - тип объекта.
	 * @return - добавленный слой.
	 */
	public GeoMapLayer addLayer(final GeoMapFeatureType aObjectType) {
		GeoMapLayer res = new GeoMapLayer(aObjectType);
		layers.add(res);
		return res;
	}

	/**
	 * Возвращает слой по его ID.
	 * 
	 * @param aLayerId
	 *            - ID слоя.
	 * @return - слой.
	 */
	public GeoMapLayer getLayerById(final String aLayerId) {
		if (aLayerId == null) {
			return null;
		}
		Iterator<GeoMapLayer> iterator = layers.iterator();
		while (iterator.hasNext()) {
			GeoMapLayer cur = iterator.next();
			if (aLayerId.equals(cur.getId())) {
				return cur;
			}
		}
		return null;
	}

	/**
	 * Возвращает слой по идентификатору добавленного в него объекта.
	 * 
	 * @param aObjectId
	 *            - идентификатор объекта.
	 * @return - слой.
	 */
	public GeoMapLayer getLayerByObjectId(final String aObjectId) {
		if (aObjectId == null) {
			return null;
		}
		Iterator<GeoMapLayer> iterator = layers.iterator();
		while (iterator.hasNext()) {
			GeoMapLayer curLayer = iterator.next();
			Iterator<GeoMapFeature> oiterator = curLayer.getFeatures().iterator();
			while (oiterator.hasNext()) {
				GeoMapFeature curObj = oiterator.next();
				if (aObjectId.equals(curObj.getId())) {
					return curLayer;
				}
			}
		}
		return null;
	}
}
