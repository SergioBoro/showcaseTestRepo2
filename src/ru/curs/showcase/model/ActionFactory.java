package ru.curs.showcase.model;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Фабрика для создания действий (Action) из XML. Не вызывается самостоятельно,
 * а только из других фабрик при работе SAX парсера.
 * 
 * @author den
 * 
 */
public class ActionFactory extends SAXTagHandler {
	/**
	 * Префикс, который обязательно должен быть у всех внутренних тэгов
	 * main_context и add_context.
	 */
	private static final String SOL_TAG_PREFIX = "context";
	static final String SHOW_CLOSE_BOTTOM_BUTTON_TAG = "show_close_bottom_button";
	static final String KEEP_USER_SETTINGS_TAG = "keep_user_settings";
	static final String SKIP_REFRESH_CONTEXT_ONLY_TAG = "skip_refresh_context_only";
	static final String SHOW_IN_MODE_TAG = "show_in";
	static final String REFRESH_CONTEXT_ONLY_TAG = "refresh_context_only";

	/**
	 * Текущее действие.
	 */
	private Action curAction;

	/**
	 * Текущий DataPanelElementLink.
	 */
	private DataPanelElementLink curDataPanelElementLink = null;

	/**
	 * Считываемое сейчас серверное действие.
	 */
	private Activity curActivity = null;
	/**
	 * Признак того, что считывается элемент main_context.
	 */
	private boolean readingMainContext = false;

	/**
	 * Признак того, что считывается элемент add_context.
	 */
	private boolean readingAddContext = false;
	/**
	 * characters.
	 */
	private String characters = null;
	/**
	 * Признак чтения секции server.
	 */
	private boolean readingServerPart = false;

	/**
	 * Стартовые тэги, которые будут обработаны данным обработчиком.
	 */
	private static final String[] START_TAGS = {
			ACTION_TAG, DP_TAG, NAVIGATOR_TAG, ELEMENT_TAG, MODAL_WINDOW_TAG,
			MAIN_CONTEXT_ATTR_NAME, ADD_CONTEXT_ATTR_NAME, SERVER_TAG, ACTIVITY_TAG };

	@Override
	protected String[] getStartTags() {
		return START_TAGS;
	}

	/**
	 * Конечные тэги, которые будут обработаны.
	 */
	private static final String[] END_TAGS =
		{ MAIN_CONTEXT_ATTR_NAME, ADD_CONTEXT_ATTR_NAME, ELEMENT_TAG, ACTIVITY_TAG, SERVER_TAG, };

	@Override
	protected String[] getEndTrags() {
		return END_TAGS;
	}

	@Override
	public boolean canHandleStartTag(final String tagName) {
		boolean res = super.canHandleStartTag(tagName);
		return res ? res : ((tagName != null) && (tagName.startsWith(SOL_TAG_PREFIX)));
	}

	@Override
	public boolean canHandleEndTag(final String tagName) {
		boolean res = super.canHandleEndTag(tagName);
		return res ? res : ((tagName != null) && (tagName.startsWith(SOL_TAG_PREFIX)));
	}

	@Override
	public Action handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		if (readingMainContext || readingAddContext) {
			characters = characters + XMLUtils.saxTagWithAttrsToString(qname, attrs);
			return curAction;
		}

		standartHandler(qname, attrs, SaxEventType.STARTTAG);
		return curAction;
	}

	/**
	 * Обработчик тэга server.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void serverSTARTTAGHandler(final Attributes attrs) {
		readingServerPart = true;
	}

	/**
	 * Обработчик тэга add_context.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void addcontextSTARTTAGHandler(final Attributes attrs) {
		readingAddContext = true;
		characters = "";
	}

	/**
	 * Обработчик тэга main_context.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void maincontextSTARTTAGHandler(final Attributes attrs) {
		readingMainContext = true;
		characters = "";
	}

	/**
	 * Обработчик тэга activity.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void activitySTARTTAGHandler(final Attributes attrs) {
		curActivity = new Activity();
		setupBaseProps(curActivity, attrs);
		if (readingServerPart) {
			String value = attrs.getValue(TYPE_TAG);
			curActivity.setType(ActivityType.valueOf(value));
		} else {
			curActivity.setType(ActivityType.BrowserJS);
		}

		CompositeContext context = createContextFromGeneral();
		curActivity.setContext(context);
	}

	/**
	 * Обработчик тэга element.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void elementSTARTTAGHandler(final Attributes attrs) {
		String value;
		curDataPanelElementLink = new DataPanelElementLink();

		curDataPanelElementLink.setId(attrs.getValue(ID_TAG));

		if (attrs.getIndex(REFRESH_CONTEXT_ONLY_TAG) > -1) {
			value = attrs.getValue(REFRESH_CONTEXT_ONLY_TAG);
			curDataPanelElementLink.setRefreshContextOnly(Boolean.parseBoolean(value));
		}
		if (attrs.getIndex(SKIP_REFRESH_CONTEXT_ONLY_TAG) > -1) {
			value = attrs.getValue(SKIP_REFRESH_CONTEXT_ONLY_TAG);
			curDataPanelElementLink.setSkipRefreshContextOnly(Boolean.parseBoolean(value));
		}
		if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
			value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
			curDataPanelElementLink.setKeepUserSettings(Boolean.parseBoolean(value));
		}

		CompositeContext context = createContextFromGeneral();
		curDataPanelElementLink.setContext(context);
	}

	/**
	 * Обработчик тэга modalwindow.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void modalwindowSTARTTAGHandler(final Attributes attrs) {
		String value;
		ModalWindowInfo mwi = new ModalWindowInfo();
		if (attrs.getIndex(CAPTION_TAG) > -1) {
			mwi.setCaption(attrs.getValue(CAPTION_TAG));
		}
		if (attrs.getIndex(WIDTH_TAG) > -1) {
			value = attrs.getValue(WIDTH_TAG);
			mwi.setWidth(Integer.valueOf(value));
		}
		if (attrs.getIndex(HEIGHT_TAG) > -1) {
			value = attrs.getValue(HEIGHT_TAG);
			mwi.setHeight(Integer.valueOf(value));
		}
		if (attrs.getIndex(SHOW_CLOSE_BOTTOM_BUTTON_TAG) > -1) {
			value = attrs.getValue(SHOW_CLOSE_BOTTOM_BUTTON_TAG);
			mwi.setShowCloseBottomButton(Boolean.parseBoolean(value));
		}
		curAction.setModalWindowInfo(mwi);
	}

	/**
	 * Обработчик тэга navigator.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void navigatorSTARTTAGHandler(final Attributes attrs) {
		NavigatorElementLink link = new NavigatorElementLink();
		if (attrs.getIndex(ELEMENT_TAG) > -1) {
			link.setId(attrs.getValue(ELEMENT_TAG));
		}
		if (attrs.getIndex(REFRESH_TAG) > -1) {
			String value = attrs.getValue(REFRESH_TAG);
			link.setRefresh(Boolean.parseBoolean(value));
		}
		curAction.setNavigatorElementLink(link);
	}

	/**
	 * Обработчик тэга datapanel.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void datapanelSTARTTAGHandler(final Attributes attrs) {
		DataPanelLink curDataPanelLink = new DataPanelLink();
		curDataPanelLink.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));

		ActionTabFinder finder = AppRegistry.getActionTabFinder();
		curDataPanelLink.setTabId(finder.findTabForAction(curDataPanelLink,
				attrs.getValue(TAB_TAG)));
		curAction.setDataPanelLink(curDataPanelLink);
	}

	/**
	 * Обработчик тэга action.
	 * 
	 * @param attrs
	 *            - атрибуты тэга.
	 */
	public void actionSTARTTAGHandler(final Attributes attrs) {
		Action action = new Action();
		action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
		if (attrs.getIndex(SHOW_IN_MODE_TAG) > -1) {
			action.setShowInMode(ShowInMode.valueOf(attrs.getValue(SHOW_IN_MODE_TAG)));
		}
		if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
			String value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
			action.setKeepUserSettings(Boolean.parseBoolean(value));
		}
		curAction = action;
	}

	@Override
	public Action
			handleEndTag(final String aNamespaceURI, final String aLname, final String qname) {
		if (qname.equalsIgnoreCase(MAIN_CONTEXT_ATTR_NAME)) {
			CompositeContext context = new CompositeContext();
			context.setMain(characters);
			curAction.setContext(context);
			readingMainContext = false;
			characters = null;
			return curAction;
		}

		if (qname.equalsIgnoreCase(ADD_CONTEXT_ATTR_NAME)) {
			if (curActivity != null) {
				curActivity.getContext().setAdditional(characters);
			} else {
				curDataPanelElementLink.getContext().setAdditional(characters);
			}
			readingAddContext = false;
			characters = null;
			return curAction;
		}

		if (qname.equalsIgnoreCase(ELEMENT_TAG)) {
			curAction.getDataPanelLink().getElementLinks().add(curDataPanelElementLink);
			curDataPanelElementLink = null;
			return curAction;
		}

		if (qname.equalsIgnoreCase(ACTIVITY_TAG)) {
			if (readingServerPart) {
				curAction.getServerActivities().add(curActivity);

			} else {
				curAction.getClientActivities().add(curActivity);
			}
			curActivity = null;

			return curAction;
		}

		if (qname.equalsIgnoreCase(SERVER_TAG)) {
			readingServerPart = false;
		}

		if (readingMainContext || readingAddContext) {
			characters = characters + "</" + qname + ">";
		}

		return curAction;
	}

	private CompositeContext createContextFromGeneral() {
		CompositeContext context = new CompositeContext();
		context.assignNullValues(curAction.getContext());
		return context;
	}

	@Override
	public void handleCharacters(final char[] aArg0, final int aArg1, final int aArg2) {
		if (readingMainContext || readingAddContext) {
			String s = new String(aArg0, aArg1, aArg2).trim();
			characters = characters + s;
		}
	}

}
