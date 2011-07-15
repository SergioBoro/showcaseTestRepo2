package ru.curs.showcase.model;

import ru.curs.showcase.app.api.CanBeCurrent;
import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.exception.IncorrectElementException;

/**
 * Интерфейс для поиска и установки вкладки инф. панели у действия при неполных
 * указанных данных.
 * 
 * @author den
 * 
 */
public abstract class ActionTabFinder {
	/**
	 * Часть сообщения об ошибке, передаваемая в случае неверного номера
	 * вкладки.
	 */
	private static final String ERROR_MES = "вкладка %s в панели %s не существует";
	/**
	 * Признак того, что нужно искать первую вкладку при первом открытии панели
	 * и оставаться на текущей при последующих.
	 */
	private static final String FIRST_OR_CURRENT_VALUE = "firstOrCurrent";

	/**
	 * Функция поиска вкладки для действия.
	 * 
	 * @param tabValue
	 *            - указатель на вкладку.
	 * @return - реальный идентификатор вкладки.
	 * @param link
	 *            - ссылка на панель.
	 */
	public String findTabForAction(final DataPanelLink link, final String tabValue) {
		if (tabValue != null) {
			if (!tabValue.equalsIgnoreCase(FIRST_OR_CURRENT_VALUE)) {
				if (!tabValue.equalsIgnoreCase(CanBeCurrent.CURRENT_ID)) {
					if (!link.getDataPanelId().equalsIgnoreCase(CanBeCurrent.CURRENT_ID)) {
						checkForExists(link, tabValue);
					}
				}
				return tabValue;
			} else {
				link.setFirstOrCurrentTab(true);
				return getFirstFromStorage(link);
			}
		} else {
			return getFirstFromStorage(link);
		}
	}

	private void checkForExists(final DataPanelLink link, final String tabValue) {
		if (!existsInStorage(link, tabValue)) {
			throw new IncorrectElementException(String.format(ERROR_MES, tabValue,
					link.getDataPanelId()));
		}
	}

	/**
	 * Получает первую вкладку панели.
	 * 
	 * @param link
	 *            - идентификатор панели.
	 * @return - идентификатор первой вкладки.
	 */
	public abstract String getFirstFromStorage(final DataPanelLink link);

	/**
	 * Проверяет - существует ли вкладка в панели.
	 * 
	 * @param link
	 *            - идентификатор панели.
	 * @param tabValue
	 *            - значение идентификатора вкладки.
	 */
	public abstract boolean existsInStorage(final DataPanelLink link, final String tabValue);
}
