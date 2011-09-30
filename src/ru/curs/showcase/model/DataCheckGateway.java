package ru.curs.showcase.model;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.util.ObjectSerializer;
import ru.curs.showcase.util.xml.*;

/**
 * Абстрактный класс шлюза с проверкой данных для элементов панели управления.
 * 
 * @author den
 * 
 */
public abstract class DataCheckGateway extends GeneralXMLHelper {

	private static final String NO_ELEMENT_INFO_ERROR =
		"Не передано описание элемента в шлюз к данным";

	/**
	 * Функция проверки переданных в шлюз данных.
	 * 
	 * @param element
	 *            - информация о загружаемом элементе.
	 */
	public void check(final DataPanelElementInfo element) {
		if (element == null) {
			throw new IncorrectElementException(NO_ELEMENT_INFO_ERROR);
		}
		if ((element.getType() != getElementType()) || !element.isCorrect()) {
			ObjectSerializer serializer = new XMLObjectSerializer();
			throw new IncorrectElementException("Некорректное описание элемента: "
					+ serializer.serialize(element));
		}
	}

	/**
	 * Функция возвращает тип элемента, обрабатываемого шлюзом.
	 * 
	 * @return - тип шлюза.
	 */
	protected abstract DataPanelElementType getElementType();
}
