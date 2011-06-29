package ru.curs.showcase.app.api.event;

import java.util.*;

import ru.curs.showcase.app.api.*;

/**
 * Класс действия, выполняемого при активации визуального элемента UI (например,
 * при щелчке по элементу навигатора, или при щелчке по строке грида).
 * 
 * @author den
 * 
 */
public class Action implements SerializableElement, GWTClonable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5014034913652092038L;

	/**
	 * Тэг верхнего уровня для фильтрующего контекста, созданного на основе
	 * выделенных записей грида.
	 */
	public static final String FILTER_TAG = "filter";
	/**
	 * Тэг для записи в фильтрующем контексте, содержащей информацию о контексте
	 * выделенной строки грида.
	 */
	public static final String CONTEXT_TAG = "context";

	/**
	 * Тип действия, которое нужно осуществить с панелью.
	 */
	private DataPanelActionType dataPanelActionType = DataPanelActionType.DO_NOTHING;

	/**
	 * Ссылка на информационную панель, которая должна быть открыта при
	 * выполнении действия.
	 */
	private DataPanelLink dataPanelLink;

	/**
	 * Ссылка на элемент навигатора, которая должна быть открыта при выполнении
	 * действия.
	 */
	private NavigatorElementLink navigatorElementLink;

	/**
	 * Режим отображения элементов панели.
	 */
	private ShowInMode showInMode = ShowInMode.PANEL;

	/**
	 * Признак того, что нужно сохранять пользовательские настройки всех
	 * элементов панели, затрагиваемых данным действием. Если элемент
	 * отображается в первый раз - то признак игнорируется. Пользовательские
	 * настройки имеют не все типы элементов.
	 */
	private Boolean keepUserSettings;

	/**
	 * Информация об отображении модального окна, связанного с действием.
	 */
	private ModalWindowInfo modalWindowInfo;

	/**
	 * Список действий на сервере, содержащихся в данном действии.
	 */
	private List<ServerActivity> serverActivities = new ArrayList<ServerActivity>();

	public final DataPanelLink getDataPanelLink() {
		return dataPanelLink;
	}

	public final void setDataPanelLink(final DataPanelLink aDataPanelLinkForOpen) {
		this.dataPanelLink = aDataPanelLinkForOpen;
	}

	public final NavigatorElementLink getNavigatorElementLink() {
		return navigatorElementLink;
	}

	public final void setNavigatorElementLink(
			final NavigatorElementLink aNavigatorElementLinkForOpen) {
		this.navigatorElementLink = aNavigatorElementLinkForOpen;
	}

	public final DataPanelActionType getDataPanelActionType() {
		return dataPanelActionType;
	}

	public final void setDataPanelActionType(final DataPanelActionType aDataPanelActionType) {
		this.dataPanelActionType = aDataPanelActionType;
	}

	/**
	 * Возвращает тип действия для навигатора в зависимости от состояния
	 * действия.
	 * 
	 */
	public final NavigatorActionType getNavigatorActionType() {
		if (navigatorElementLink == null) {
			return NavigatorActionType.DO_NOTHING;
		}
		if (dataPanelLink == null) {
			return NavigatorActionType.CHANGE_NODE_AND_DO_ACTION;
		} else {
			return NavigatorActionType.CHANGE_NODE;
		}
	}

	/**
	 * Функция "самоопределения" состояния действия по его свойствам. Для
	 * каждого действия должны быть вызвана один и только один раз!
	 * 
	 */
	public void determineState() {
		determineDPActionType();
		determineKeepUserSettingsState();
	}

	private void determineKeepUserSettingsState() {
		boolean actionParamDefined = true;
		if (keepUserSettings == null) {
			keepUserSettings = true;
			actionParamDefined = false;
		}
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
			while (iterator.hasNext()) {
				DataPanelElementLink elink = iterator.next();
				if (elink.getKeepUserSettings() == null) {
					if (actionParamDefined) {
						elink.setKeepUserSettings(getKeepUserSettings());
					} else {
						elink.setKeepUserSettings(false);
					}
				}

			}
		}
	}

	/**
	 * TODO: Алгоритм хотя и довольно простой, но не отражен в модели. Подумать
	 * о доработке модели.
	 * 
	 * @param aCurrentElementInfo
	 *            - информация о текущем элементе на панели. Используется в
	 *            алгоритме определения типа.
	 */
	private void determineDPActionType() {
		if (dataPanelLink == null) {
			dataPanelActionType = DataPanelActionType.DO_NOTHING;
			return;
		}
		if (dataPanelLink.isCurrentPanel()) {
			if (dataPanelLink.isCurrentTab()) {
				if (dataPanelLink.getContext().mainIsCurrent()) {
					dataPanelActionType = DataPanelActionType.RELOAD_ELEMENTS;
				} else {
					dataPanelActionType = DataPanelActionType.REFRESH_TAB;
				}
			} else {
				dataPanelActionType = DataPanelActionType.REFRESH_TAB;
			}
		} else {
			dataPanelActionType = DataPanelActionType.RELOAD_PANEL;
		}
	}

	/**
	 * Актуализирует состояние действия по переданному контексту.
	 * 
	 * @param callContext
	 *            - контекст.
	 * @return - себя.
	 */
	public Action actualizeBy(final CompositeContext callContext) {
		determineState();
		if (getDataPanelActionType() == DataPanelActionType.DO_NOTHING) {
			return this;
		}

		CompositeContext context = getDataPanelLink().getContext();
		context.actualizeBy(callContext);

		Iterator<DataPanelElementLink> eterator = getDataPanelLink().getElementLinks().iterator();
		while (eterator.hasNext()) {
			CompositeContext elContext = eterator.next().getContext();
			elContext.actualizeBy(callContext);
		}

		return this;
	}

	/**
	 * Обновление действия. При этом значения типа current не заменяют реальное
	 * значение.
	 * 
	 * @param prevAction
	 *            - новый Action.
	 * @return - себя.
	 */
	public Action actualizeBy(final Action prevAction) {
		if (dataPanelLink != null) {
			if (dataPanelLink.isCurrentPanel()) {
				dataPanelLink.setDataPanelId(prevAction.dataPanelLink.getDataPanelId());
			}
			if (dataPanelLink.isCurrentTab()) {
				dataPanelLink.setTabId(prevAction.dataPanelLink.getTabId());
			}
			if (dataPanelLink.getFirstOrCurrentTab()
					&& dataPanelLink.getDataPanelId().equals(
							prevAction.dataPanelLink.getDataPanelId())) {
				dataPanelLink.setTabId(prevAction.dataPanelLink.getTabId());
			}

			dataPanelLink.getContext().actualizeBy(prevAction.dataPanelLink.getContext());

			Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
			while (iterator.hasNext()) {
				DataPanelElementLink link = iterator.next();
				link.getContext().actualizeBy(dataPanelLink.getContext());
			}
		}
		return this;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public Action gwtClone() {
		Action res = new Action();
		res.dataPanelActionType = dataPanelActionType;
		if (dataPanelLink != null) {
			res.dataPanelLink = dataPanelLink.gwtClone();
		}
		if (navigatorElementLink != null) {
			res.navigatorElementLink = navigatorElementLink.gwtClone();
		}
		res.keepUserSettings = keepUserSettings;
		res.showInMode = showInMode;
		if (modalWindowInfo != null) {
			res.modalWindowInfo = modalWindowInfo.gwtClone();
		}
		res.serverActivities.clear();
		res.serverActivities.addAll(serverActivities);
		return res;
	}

	public Action() {
		super();
	}

	public Action(final DataPanelActionType aDataPanelActionType) {
		super();
		dataPanelActionType = aDataPanelActionType;

		if (dataPanelActionType == DataPanelActionType.RELOAD_ELEMENTS) {
			dataPanelLink = DataPanelLink.createCurrent();
		} else {
			dataPanelLink = new DataPanelLink();
		}
	}

	/**
	 * Обновляет дополнительный контекст у всех зависимых элементов.
	 * 
	 * @param context
	 *            - правильный контекст.
	 * @return - себя.
	 */
	public Action updateAddContext(final CompositeContext context) {
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			Iterator<DataPanelElementLink> eliterator = dataPanelLink.getElementLinks().iterator();
			while (eliterator.hasNext()) {
				DataPanelElementLink link = eliterator.next();
				if (!link.getSkipRefreshContextOnly()) {
					link.getContext().setAdditional(context.getAdditional());
				}
			}
		}
		return this;
	}

	/**
	 * Функция добавления фильтра ко всем контекстам в действии.
	 * 
	 * @param data
	 *            - данные фильтра (как правило MainInstance XForms).
	 */
	public void filterBy(final String data) {
		if (getDataPanelActionType() == DataPanelActionType.DO_NOTHING) {
			return;
		}
		dataPanelLink.getContext().setFilter(data);
		Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
		while (iterator.hasNext()) {
			DataPanelElementLink link = iterator.next();
			link.getContext().setFilter(data);
		}
	}

	public ShowInMode getShowInMode() {
		return showInMode;
	}

	public void setShowInMode(final ShowInMode aShowInMode) {
		showInMode = aShowInMode;
	}

	/**
	 * Проверка того, что действие включает в себя фильтр. Данный факт
	 * определяется по заполненному фильтрующему контексту у основного
	 * составного контекста действия.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isFiltered() {
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			return (dataPanelLink.getContext().getFilter() != null);
		}
		return false;
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}

	/**
	 * Устанавливает значение aKeepUserSettings как у действия в целом, так и у
	 * его отдельных элементов.
	 * 
	 * @param aKeepUserSettings
	 *            - новое значение признака.
	 */
	public void setKeepUserSettingsForAll(final boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
			while (iterator.hasNext()) {
				DataPanelElementLink elink = iterator.next();
				elink.setKeepUserSettings(aKeepUserSettings);
			}
		}
	}

	public ModalWindowInfo getModalWindowInfo() {
		return modalWindowInfo;
	}

	public void setModalWindowInfo(final ModalWindowInfo aModalWindowInfo) {
		modalWindowInfo = aModalWindowInfo;
	}

	/**
	 * Устанавливает контекст сессии (в виде карты) для всех составных
	 * контекстов действия.
	 * 
	 * @param data
	 *            - новое значение контекста.
	 */
	public void setSessionContext(final Map<String, List<String>> data) {
		if (getDataPanelActionType() == DataPanelActionType.DO_NOTHING) {
			return;
		}
		dataPanelLink.getContext().addSessionParams(data);
		Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
		while (iterator.hasNext()) {
			DataPanelElementLink link = iterator.next();
			link.getContext().addSessionParams(data);
		}

	}

	/**
	 * Устанавливает контекст сессии (в виде строки) для всех составных
	 * контекстов действия.
	 * 
	 * @param data
	 *            - новое значение контекста.
	 */
	public void setSessionContext(final String data) {
		if (getDataPanelActionType() == DataPanelActionType.DO_NOTHING) {
			return;
		}
		dataPanelLink.getContext().setSession(data);
		Iterator<DataPanelElementLink> iterator = dataPanelLink.getElementLinks().iterator();
		while (iterator.hasNext()) {
			DataPanelElementLink elLink = iterator.next();
			elLink.getContext().setSession(data);
		}

	}

	/**
	 * Функция, определяющая требует ли действие выполнение каких-либо операций
	 * на сервера (например, вызов процедуры в БД).
	 * 
	 */
	public boolean containServerActivity() {
		return serverActivities.size() > 0;
	}

	public List<ServerActivity> getServerActivities() {
		return serverActivities;
	}

	public void setServerActivities(final List<ServerActivity> aServerActivities) {
		serverActivities = aServerActivities;
	}
}
