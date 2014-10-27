package ru.curs.showcase.core.event;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания действий (Action) из XML. Не вызывается самостоятельно,
 * а только из других фабрик при работе SAX парсера.
 * 
 * @author den
 * 
 */
public class ActionFactory extends SAXTagHandler {
	private static final String SHOW_CLOSE_BOTTOM_BUTTON_TAG = "show_close_bottom_button";
	private static final String KEEP_USER_SETTINGS_TAG = "keep_user_settings";
	private static final String PARTIAL_UPDATE_TAG = "partial_update";
	private static final String SHOW_IN_MODE_TAG = "show_in";
	private static final String GROUP_TAG = "group";

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

	private CompositeContext callContext = null;

	/**
	 * Стартовые тэги, которые будут обработаны данным обработчиком.
	 */
	private static final String[] START_TAGS = {
			ACTION_TAG, DP_TAG, NAVIGATOR_TAG, ELEMENT_TAG, MODAL_WINDOW_TAG, MAIN_CONTEXT_TAG,
			ADD_CONTEXT_TAG, SERVER_TAG, ACTIVITY_TAG };

	/**
	 * Передавать контекст нужно для ActionTabFinder.
	 * 
	 * @param aCallContext
	 *            - контекст с session.
	 */
	public ActionFactory(final CompositeContext aCallContext) {
		super();
		callContext = aCallContext;
	}

	@Override
	protected String[] getStartTags() {
		return START_TAGS;
	}

	/**
	 * Конечные тэги, которые будут обработаны.
	 */
	private static final String[] END_TAGS = {
			MAIN_CONTEXT_TAG, ADD_CONTEXT_TAG, ELEMENT_TAG, ACTIVITY_TAG, SERVER_TAG, };

	@Override
	protected String[] getEndTrags() {
		return END_TAGS;
	}

	@Override
	public boolean canHandleStartTag(final String tagName) {
		boolean res = super.canHandleStartTag(tagName);
		return res ? res : (readingMainContext || readingAddContext);
	}

	@Override
	public boolean canHandleEndTag(final String tagName) {
		boolean res = super.canHandleEndTag(tagName);
		return res ? res : (readingMainContext || readingAddContext);
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

	public void serverSTARTTAGHandler(final Attributes attrs) {
		readingServerPart = true;
	}

	public void addcontextSTARTTAGHandler(final Attributes attrs) {
		readingAddContext = true;
		characters = "";
	}

	public void maincontextSTARTTAGHandler(final Attributes attrs) {
		readingMainContext = true;
		characters = "";
	}

	public void activitySTARTTAGHandler(final Attributes attrs) {
		curActivity = new Activity();
		setupBaseProps(curActivity, attrs);
		curActivity.setOnServerSide(readingServerPart);

		CompositeContext context = createContextFromGeneral();
		curActivity.setContext(context);
	}

	public void elementSTARTTAGHandler(final Attributes attrs) {
		String value;
		curDataPanelElementLink = new DataPanelElementLink();

		if (attrs.getIndex(ID_TAG) > -1) {
			value = attrs.getValue(ID_TAG);
			curDataPanelElementLink.setId(attrs.getValue(ID_TAG));
		}

		if (attrs.getIndex(GROUP_TAG) > -1) {
			value = attrs.getValue(GROUP_TAG);
			curDataPanelElementLink.setGroup(attrs.getValue(GROUP_TAG));
		}

		if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
			value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
			curDataPanelElementLink.setKeepUserSettings(Boolean.parseBoolean(value));
		}

		if (attrs.getIndex(PARTIAL_UPDATE_TAG) > -1) {
			value = attrs.getValue(PARTIAL_UPDATE_TAG);
			curDataPanelElementLink.setPartialUpdate(Boolean.parseBoolean(value));
		}

		CompositeContext context = createContextFromGeneral();
		curDataPanelElementLink.setContext(context);
	}

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

	// public void datapanelSTARTTAGHandler(final Attributes attrs) {
	// DataPanelLink curDataPanelLink = new DataPanelLink();
	// curDataPanelLink.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));
	//
	// CompositeContext context = curAction.getContext().gwtClone();
	// context.setSession(callContext.getSession());
	//
	// curDataPanelLink.setTabId(attrs.getValue(TAB_TAG));
	//
	// curAction.setDataPanelLink(curDataPanelLink);
	// }

	public void datapanelSTARTTAGHandler(final Attributes attrs) {

		DataPanelLink curDataPanelLink = new DataPanelLink();
		curDataPanelLink.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));

		ActionTabFinder finder = AppRegistry.getActionTabFinder();
		CompositeContext context = curAction.getContext().gwtClone();
		context.setSession(callContext.getSession());
		curDataPanelLink.setTabId(finder.findTabForAction(context, curDataPanelLink,
				attrs.getValue(TAB_TAG)));
		curAction.setDataPanelLink(curDataPanelLink);

	}

	public void actionSTARTTAGHandler(final Attributes attrs) {
		Action action = new Action();
		if (attrs.getIndex(SHOW_IN_MODE_TAG) > -1) {
			action.setShowInMode(ShowInMode.valueOf(attrs.getValue(SHOW_IN_MODE_TAG)));
		}

		if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
			String value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
			action.setKeepUserSettings(Boolean.parseBoolean(value));
		}

		if (attrs.getIndex(PARTIAL_UPDATE_TAG) > -1) {
			String value = attrs.getValue(PARTIAL_UPDATE_TAG);
			action.setPartialUpdate(Boolean.parseBoolean(value));
		}

		curAction = action;
	}

	@Override
	public Action
			handleEndTag(final String aNamespaceURI, final String aLname, final String qname) {
		if (qname.equalsIgnoreCase(MAIN_CONTEXT_TAG)) {
			CompositeContext context = new CompositeContext();
			context.setMain(characters);
			curAction.setContext(context);
			readingMainContext = false;
			characters = null;
			return curAction;
		}

		if (qname.equalsIgnoreCase(ADD_CONTEXT_TAG)) {
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
