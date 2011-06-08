package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.event.Action;

/**
 * Класс информационной панели. Панель содержит набор вкладок, каждая из которых
 * содержит набор элементов.
 * 
 * @author den
 * 
 */
public class DataPanel implements SerializableElement {

	public static final int DEF_TIMER_INTERVAL = 600;

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1182249077845843177L;

	/**
	 * Идентификатор панели. В случае хранения панели в файле - имя файла без
	 * расширения.
	 */
	private String id;

	/**
	 * Набор вкладок панели.
	 */
	private List<DataPanelTab> tabs = new ArrayList<DataPanelTab>();

	/**
	 * Признак того, что нужно сохранять данные уже открытых вкладок панели и не
	 * обращаться к серверу повторно при смене вкладки. На выполнение действий
	 * данная опция также влияет, т.к. по сути смена вкладки из UI при клике ее
	 * заголовку - это тоже действие. В режиме cacheData = true должна быть
	 * возможность принудительного обновления вкладки.
	 */
	private Boolean cacheData = false;

	/**
	 * Признак того, нужно ли обновлять элементы панели по таймеру. При
	 * обновлении по таймеру обновляется только содержимое текущий вкладки. При
	 * этом время отсчитывается от последнего из 3 событий: 1) последней
	 * загрузки данных вкладки, инициированной из UI, 2) выполнения действия,
	 * обновившего текущую вкладку, 3) последнего обновления по таймеру.
	 */
	private Boolean refreshByTimer = false;
	/**
	 * Интервал обновления панели в секундах. Используется только если
	 * refreshByTimer=true.
	 */
	private Integer refreshInterval = DEF_TIMER_INTERVAL;

	public DataPanel(final String aId) {
		super();
		id = aId;
	}

	public DataPanel() {
		super();
	}

	/**
	 * Возвращает активную вкладку для переданного объекта действия. Если в
	 * действии не задана вкладка панели - открывается первая вкладка.
	 * 
	 * @param action
	 *            - действие.
	 * @return - вкладка.
	 */
	public DataPanelTab getActiveTabForAction(final Action action) {
		if (action.getDataPanelLink() != null) {
			return getTabById(action.getDataPanelLink().getTabId());
		} else {
			return tabs.iterator().next();
		}
	}

	/**
	 * Возвращает вкладку по id.
	 * 
	 * @param aTabId
	 *            - id.
	 * @return вкладку.
	 */
	public DataPanelTab getTabById(final String aTabId) {
		Iterator<DataPanelTab> iterator = tabs.iterator();
		while (iterator.hasNext()) {
			DataPanelTab current = iterator.next();
			if (current.getId().equals(aTabId)) {
				return current;
			}
		}
		return null;
	}

	public final List<DataPanelTab> getTabs() {
		return tabs;
	}

	public final void setTabs(final List<DataPanelTab> aTabs) {
		this.tabs = aTabs;
	}

	/**
	 * Добавляет и инициализирует вкладку к панели.
	 * 
	 * @param tabId
	 *            - id вкладки.
	 * @param tabNamr
	 *            - наименование вкладки.
	 * @return - вкладка.
	 */
	public DataPanelTab add(final String tabId, final String tabNamr) {
		DataPanelTab res = new DataPanelTab();
		res.setId(tabId);
		res.setName(tabNamr);
		res.setDataPanel(this);
		res.setPosition(tabs.size());
		tabs.add(res);
		return res;
	}

	public Integer getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(final Integer aRefreshInterval) {
		refreshInterval = aRefreshInterval;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public Boolean getRefreshByTimer() {
		return refreshByTimer;
	}

	public void setRefreshByTimer(final Boolean aRefreshByTimer) {
		refreshByTimer = aRefreshByTimer;
	}

	public Boolean getCacheData() {
		return cacheData;
	}

	public void setCacheData(final Boolean aCacheData) {
		cacheData = aCacheData;
	}

}
