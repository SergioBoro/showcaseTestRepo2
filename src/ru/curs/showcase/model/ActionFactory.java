package ru.curs.showcase.model;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.event.*;

/**
 * Фабрика для создания действий (Action) из XML. Не вызывается самостоятельно,
 * а только из других фабрик при работе SAX парсера.
 * 
 * @author den
 * 
 */
public class ActionFactory extends GeneralXMLHelper implements SAXTagHandler {
	static final String SHOW_CLOSE_BOTTOM_BUTTON_TAG = "show_close_bottom_button";
	static final String KEEP_USER_SETTINGS_TAG = "keep_user_settings";
	static final String SKIP_REFRESH_CONTEXT_ONLY_TAG = "skip_refresh_context_only";
	static final String SHOW_IN_MODE_TAG = "show_in";
	static final String REFRESH_CONTEXT_ONLY_TAG = "refresh_context_only";

	/**
	 * Текущее действие.
	 */
	private Action current;

	/**
	 * Текущий DataPanelLink.
	 */
	private DataPanelLink linkDataPanelLink = null;
	/**
	 * Текущий DataPanelElementLink.
	 */
	private DataPanelElementLink linkDataPanelElementLink = null;
	/**
	 * readingMainContext.
	 */
	private boolean readingMainContext = false;
	/**
	 * readingAddContext.
	 */
	private boolean readingAddContext = false;
	/**
	 * characters.
	 */
	private String characters = null;

	@Override
	public boolean canHandleStartTag(final String tagName, final SaxEventType saxEventType) {
		return ACTION_TAG.equalsIgnoreCase(tagName) || DP_TAG.equalsIgnoreCase(tagName)
				|| NAVIGATOR_TAG.equalsIgnoreCase(tagName)
				|| ELEMENT_TAG.equalsIgnoreCase(tagName)
				|| MODAL_WINDOW_TAG.equalsIgnoreCase(tagName)
				|| MAIN_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| ADD_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName);
	}

	@Override
	public boolean canHandleEndTag(final String tagName, final SaxEventType saxEventType) {
		return MAIN_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName)
				|| ADD_CONTEXT_ATTR_NAME.equalsIgnoreCase(tagName);
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
			current = action;
		}
		if (qname.equalsIgnoreCase(DP_TAG)) {
			linkDataPanelLink.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));

			ActionTabFinder finder = AppRegistry.getActionTabFinder();
			linkDataPanelLink.setTabId(finder.findTabForAction(linkDataPanelLink,
					attrs.getValue(TAB_TAG)));
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
			current.setNavigatorElementLink(link);
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
			current.setModalWindowInfo(mwi);
		}
		if (qname.equalsIgnoreCase(ELEMENT_TAG)) {
			linkDataPanelElementLink = new DataPanelElementLink();

			linkDataPanelElementLink.setId(attrs.getValue(ID_TAG));

			if (attrs.getIndex(REFRESH_CONTEXT_ONLY_TAG) > -1) {
				value = attrs.getValue(REFRESH_CONTEXT_ONLY_TAG);
				linkDataPanelElementLink.setRefreshContextOnly(Boolean.parseBoolean(value));
			}
			if (attrs.getIndex(SKIP_REFRESH_CONTEXT_ONLY_TAG) > -1) {
				value = attrs.getValue(SKIP_REFRESH_CONTEXT_ONLY_TAG);
				linkDataPanelElementLink.setSkipRefreshContextOnly(Boolean.parseBoolean(value));
			}
			if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
				value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
				linkDataPanelElementLink.setKeepUserSettings(Boolean.parseBoolean(value));
			}
		}

		if (qname.equalsIgnoreCase(MAIN_CONTEXT_ATTR_NAME)) {
			linkDataPanelLink = new DataPanelLink();

			readingMainContext = true;
		}
		if (qname.equalsIgnoreCase(ADD_CONTEXT_ATTR_NAME)) {
			readingAddContext = true;
		}

		return current;
	}

	@Override
	public Action
			handleEndTag(final String aNamespaceURI, final String aLname, final String aQname) {
		if (aQname.equalsIgnoreCase(MAIN_CONTEXT_ATTR_NAME)) {
			// System.out.println("handleCharacters, main_context = " +
			// characters);
			CompositeContext context = new CompositeContext();
			context.setMain(characters);
			linkDataPanelLink.setContext(context);
			current.setDataPanelLink(linkDataPanelLink);

			readingMainContext = false;
		}

		if (aQname.equalsIgnoreCase(ADD_CONTEXT_ATTR_NAME)) {
			// System.out.println("handleCharacters, add_context = " +
			// characters);
			CompositeContext context = new CompositeContext();
			context.assignNullValues(current.getDataPanelLink().getContext());
			context.setAdditional(characters);
			linkDataPanelElementLink.setContext(context);
			current.getDataPanelLink().getElementLinks().add(linkDataPanelElementLink);

			readingAddContext = false;
		}

		characters = null;

		return current;
	}

	@Override
	public void handleCharacters(final char[] aArg0, final int aArg1, final int aArg2) {
		if (readingMainContext || readingAddContext) {
			String s = new String(aArg0, aArg1, aArg2).trim();
			if (characters == null) {
				characters = s;
			} else {
				characters = characters + s;
			}
		}
	}

}
