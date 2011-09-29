package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.JSONObject;

/**
 * Класс элемента панели управления с легендой. Легенда содержит информацию об
 * отображаемых в элементе графических данных. Класс используется для типов
 * элементов CHART и MAP.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataPanelElementWithLegend extends DataPanelCompBasedElement implements
		JSONObject {

	private static final long serialVersionUID = 652329350772736896L;

	/**
	 * Позиция легенды относительно графика.
	 */
	private LegendPosition legendPosition;

	/**
	 * Шаблон для графика. В текущей реализации представляет собой строку с JSON
	 * объектом. Параметр 2 в функции Володи.
	 */
	private String template;

	/**
	 * JSON объект с динамическими данными для использования в JS коде.
	 */
	private String jsDynamicData;

	public final String getJsDynamicData() {
		return jsDynamicData;
	}

	@Override
	public final void setJsDynamicData(final String aJsDynamicData) {
		jsDynamicData = aJsDynamicData;
	}

	public final LegendPosition getLegendPosition() {
		return legendPosition;
	}

	public final void setLegendPosition(final LegendPosition aLegendPosition) {
		legendPosition = aLegendPosition;
	}

	public final String getTemplate() {
		return template;
	}

	public final void setTemplate(final String aTemplate) {
		template = aTemplate;
	}
}
