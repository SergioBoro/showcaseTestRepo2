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
public class ActionFactory extends GeneralXMLHelper implements SAXTagHandler {
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

	@Override
	public boolean canHandleStartTag(final String tagName, final SaxEventType saxEventType) {
		return ACTION_TAG.equalsIgnoreCase(tagName) || DP_TAG.equalsIgnoreCase(tagName)
				|| NAVIGATOR_TAG.equalsIgnoreCase(tagName)
				|| ELEMENT_TAG.equalsIgnoreCase(tagName)
				|| MODAL_WINDOW_TAG.equalsIgnoreCase(tagName)
				|| MAIN_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| ADD_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| SERVER_TAG.equalsIgnoreCase(tagName) || ACTIVITY_TAG.equalsIgnoreCase(tagName)
				|| ((tagName != null) && (tagName.startsWith(SOL_TAG_PREFIX)));
	}

	@Override
	public boolean canHandleEndTag(final String tagName, final SaxEventType saxEventType) {
		return MAIN_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| ADD_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| ELEMENT_TAG.equalsIgnoreCase(tagName) || ACTIVITY_TAG.equalsIgnoreCase(tagName)
				|| SERVER_TAG.equalsIgnoreCase(tagName)
				|| ((tagName != null) && (tagName.startsWith(SOL_TAG_PREFIX)));
	}

	@Override
	public Action handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		String value;
		if (qname.equalsIgnoreCase(ACTION_TAG)) {
			Action action = new Action();
			action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
			if (attrs.getIndex(SHOW_IN_MODE_TAG) > -1) {
				action.setShowInMode(ShowInMode.valueOf(attrs.getValue(SHOW_IN_MODE_TAG)));
			}
			if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
				value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
				action.setKeepUserSettings(Boolean.parseBoolean(value));
			}
			curAction = action;
			return curAction;
		}
		if (qname.equalsIgnoreCase(DP_TAG)) {
			DataPanelLink curDataPanelLink = new DataPanelLink();
			curDataPanelLink.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));

			ActionTabFinder finder = AppRegistry.getActionTabFinder();
			curDataPanelLink.setTabId(finder.findTabForAction(curDataPanelLink,
					attrs.getValue(TAB_TAG)));
			curAction.setDataPanelLink(curDataPanelLink);
			return curAction;
		}
		if (qname.equalsIgnoreCase(NAVIGATOR_TAG)) {
			NavigatorElementLink link = new NavigatorElementLink();
			if (attrs.getIndex(ELEMENT_TAG) > -1) {
				link.setId(attrs.getValue(ELEMENT_TAG));
			}
			if (attrs.getIndex(REFRESH_TAG) > -1) {
				value = attrs.getValue(REFRESH_TAG);
				link.setRefresh(Boolean.parseBoolean(value));
			}
			curAction.setNavigatorElementLink(link);
			return curAction;
		}
		if (qname.equalsIgnoreCase(MODAL_WINDOW_TAG)) {
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
			return curAction;
		}
		if (qname.equalsIgnoreCase(ELEMENT_TAG)) {
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
			return curAction;
		}
		if (qname.equalsIgnoreCase(SERVER_TAG)) {
			readingServerPart = true;
		}
		if (qname.equalsIgnoreCase(ACTIVITY_TAG)) {
			curActivity = new Activity();
			curActivity.setId(attrs.getValue(ID_TAG));
			curActivity.setName(attrs.getValue(NAME_TAG));
			if (readingServerPart) {
				value = attrs.getValue(TYPE_TAG);
				curActivity.setType(ActivityType.valueOf(value));
			} else {
				curActivity.setType(ActivityType.BrowserJS);
			}

			CompositeContext context = createContextFromGeneral();
			curActivity.setContext(context);
			return curAction;
		}
		if (qname.equalsIgnoreCase(MAIN_CONTEXT_ATTR_NAME)) {
			readingMainContext = true;
			characters = "";
			return curAction;
		}
		if (qname.equalsIgnoreCase(ADD_CONTEXT_ATTR_NAME)) {
			readingAddContext = true;
			characters = "";
			return curAction;
		}
		if (readingMainContext || readingAddContext) {
			characters = characters + XMLUtils.saxTagWithAttrsToString(qname, attrs);
			return curAction;
		}
		return curAction;
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
