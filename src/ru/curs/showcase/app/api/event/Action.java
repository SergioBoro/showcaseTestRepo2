package ru.curs.showcase.app.api.event;

import java.util.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.GWTClonable;

/**
 * Класс действия, выполняемого при активации визуального элемента UI (например,
 * при щелчке по элементу навигатора, или при щелчке по строке грида).
 * 
 * @author den
 * 
 */
public class Action implements SerializableElement, GWTClonable, ContainingContext {
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
	private List<Activity> serverActivities = new ArrayList<Activity>();

	/**
	 * Список действий на сервере, содержащихся в данном действии.
	 */
	private List<Activity> clientActivities = new ArrayList<Activity>();

	/**
	 * Контекст, общий для всего действия. Играет роль контекста по умолчанию
	 * для элементов, не имеющих переопределенного контекста.
	 */
	private CompositeContext context;

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
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
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
				if (context.mainIsCurrent()) {
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

	private Iterable<ContainingContext> getContainingContextChilds() {
		final Iterator<? extends ContainingContext> caIterator = getClientActivities().iterator();
		final Iterator<? extends ContainingContext> saIterator = getServerActivities().iterator();
		final Iterator<? extends ContainingContext> dpeIterator =
			dataPanelActionType != DataPanelActionType.DO_NOTHING ? dataPanelLink
					.getElementLinks().iterator() : null;

		return new Iterable<ContainingContext>() {

			@Override
			public Iterator<ContainingContext> iterator() {
				return new Iterator<ContainingContext>() {

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public ContainingContext next() {
						if ((dpeIterator != null) && (dpeIterator.hasNext())) {
							return dpeIterator.next();
						}
						if (saIterator.hasNext()) {
							return saIterator.next();
						}
						if (caIterator.hasNext()) {
							return caIterator.next();
						}
						return null;
					}

					@Override
					public boolean hasNext() {
						if (dpeIterator != null) {
							return dpeIterator.hasNext() || saIterator.hasNext()
									|| caIterator.hasNext();
						} else {
							return saIterator.hasNext() || caIterator.hasNext();
						}
					}
				};
			}
		};
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

		if (needGeneralContext()) {
			context.actualizeBy(callContext);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().actualizeBy(callContext);
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
		if (prevAction == null) {
			return this;
		}

		if (needGeneralContext()) {
			context.actualizeBy(prevAction.context);
		}

		if (getDataPanelActionType() != DataPanelActionType.DO_NOTHING) {
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
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().actualizeBy(context);
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
		if (context != null) {
			res.context = context.gwtClone();
		}
		res.serverActivities.clear();
		for (Activity act : serverActivities) {
			res.serverActivities.add(act.gwtClone());
		}
		res.clientActivities.clear();
		for (Activity act : clientActivities) {
			res.clientActivities.add(act.gwtClone());
		}
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
			context = CompositeContext.createCurrent();
		} else {
			dataPanelLink = new DataPanelLink();
		}
	}

	/**
	 * Обновляет дополнительный контекст у всех зависимых элементов.
	 * 
	 * @param addContext
	 *            - правильный контекст.
	 * @return - себя.
	 */
	public Action refreshContextOnly(final CompositeContext addContext) {
		if (dataPanelActionType != DataPanelActionType.DO_NOTHING) {
			for (DataPanelElementLink link : dataPanelLink.getElementLinks()) {
				if (!link.getSkipRefreshContextOnly()) {
					link.getContext().setAdditional(addContext.getAdditional());
				}
			}
		}

		refreshContextOnlyForActivities(serverActivities, addContext);
		refreshContextOnlyForActivities(clientActivities, addContext);
		return this;
	}

	private void refreshContextOnlyForActivities(final List<Activity> aActivities,
			final CompositeContext addContext) {
		for (Activity act : aActivities) {
			act.getContext().setAdditional(addContext.getAdditional());
		}
	}

	/**
	 * Функция обновления add_context у всех дочерних контекстов в действии.
	 * 
	 * @param data
	 *            - данные фильтра (как правило MainInstance XForms).
	 */
	public void setAdditionalContext(final String data) {
		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setAdditional(data);
		}
	}

	/**
	 * Функция добавления фильтра ко всем контекстам в действии.
	 * 
	 * @param data
	 *            - данные фильтра (как правило MainInstance XForms).
	 */
	public void filterBy(final String data) {
		if (needGeneralContext()) {
			context.setFilter(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setFilter(data);
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
		return context.getFilter() != null;
	}

	/**
	 * Устанавливает признак того, что действие содержит фильтр.
	 * 
	 * @param filter
	 *            - строка фильтра.
	 */
	public void markFiltered(final String filter) {
		if (!isFiltered()) {
			context.setFilter(filter);
		}
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
			for (DataPanelElementLink elink : dataPanelLink.getElementLinks()) {
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
		if (needGeneralContext()) {
			context.addSessionParams(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().addSessionParams(data);
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
		if (needGeneralContext()) {
			context.setSession(data);
		}

		for (ContainingContext el : getContainingContextChilds()) {
			el.getContext().setSession(data);
		}
	}

	/**
	 * Функция, определяющая требует ли действие выполнение каких-либо операций
	 * на сервера (например, вызов процедуры в БД).
	 * 
	 */
	public boolean containsServerActivity() {
		return !serverActivities.isEmpty();
	}

	/**
	 * Функция, определяющая требует ли действие выполнение каких-либо операций
	 * на клиенте (например, вызов процедуры JS).
	 * 
	 */
	public boolean containsClientActivity() {
		return !clientActivities.isEmpty();
	}

	public List<Activity> getServerActivities() {
		return serverActivities;
	}

	@Override
	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	public void setServerActivities(final List<Activity> aServerActivities) {
		serverActivities = aServerActivities;
	}

	/**
	 * Признак того, что действие должно содержать базовый контекст.
	 * 
	 */
	public boolean needGeneralContext() {
		return (dataPanelActionType != DataPanelActionType.DO_NOTHING) || containsServerActivity()
				|| containsClientActivity();
	}

	public List<Activity> getClientActivities() {
		return clientActivities;
	}

	public void setClientActivities(final List<Activity> aClientActivities) {
		clientActivities = aClientActivities;
	}
}
