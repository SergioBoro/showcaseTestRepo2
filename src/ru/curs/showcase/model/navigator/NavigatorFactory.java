package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.model.event.ActionFactory;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorFactory extends SAXTagHandler {
	private static final String SELECT_ON_LOAD_TAG = "selectOnLoad";
	private static final String XML_ERROR_MES = "описание навигатора";
	private static final String GRP_ICONS_DIR_PARAM_NAME = "navigator.icons.dir.name";
	private static final String GRP_DEF_ICON_PARAM_NAME = "navigator.def.icon.name";

	/**
	 * Стартовые тэги, которые будут обработаны.
	 */
	private static final String[] START_TAGS = { NAVIGATOR_TAG, GROUP_TAG };

	/**
	 * Конечные тэги, которые будут обработаны.
	 */
	private static final String[] END_TAGS = { EL_ON_LEVEL_NODE_NAME };
	/**
	 * Путь к каталогу для иконок.
	 */
	private final String groupIconsDir;
	/**
	 * Имя иконки по умолчанию.
	 */
	private final String groupDefIcon;

	/**
	 * Конструируемый навигатор.
	 */
	private Navigator result;
	/**
	 * Текущая группа навигатора.
	 */
	private NavigatorGroup currentGroup;
	/**
	 * Фабрика событий.
	 */
	private final ActionFactory actionFactory = new ActionFactory();
	/**
	 * Стек текущих элементов навигатора.
	 */
	private final LinkedList<NavigatorElement> currentElStack = new LinkedList<NavigatorElement>();

	public NavigatorFactory() {
		super();
		groupIconsDir = AppProps.getRequiredValueByName(GRP_ICONS_DIR_PARAM_NAME);
		groupDefIcon = AppProps.getRequiredValueByName(GRP_DEF_ICON_PARAM_NAME);
	}

	public void levelSTARTTAGHandler(final Attributes attrs) {
		NavigatorElement el = new NavigatorElement();
		setupBaseProps(el, attrs);
		if (attrs.getIndex(SELECT_ON_LOAD_TAG) > -1) {
			if (attrs.getValue(SELECT_ON_LOAD_TAG).equalsIgnoreCase("true")) {
				result.setAutoSelectElement(el);
			}
		}
		if (currentElStack.isEmpty()) {
			currentGroup.getElements().add(el);
		} else {
			currentElStack.getLast().getElements().add(el);
		}
		currentElStack.add(el);
	}

	public void groupSTARTTAGHandler(final Attributes attrs) {
		currentGroup = new NavigatorGroup();
		setupBaseProps(currentGroup, attrs);
		if (attrs.getIndex(IMAGE_ATTR_NAME) > -1) {
			setupImageId(attrs.getValue(IMAGE_ATTR_NAME));
		} else {
			setupImageId(groupDefIcon);
		}
		result.getGroups().add(currentGroup);
	}

	private void setupImageId(final String imageFile) {
		currentGroup.setImageId(String.format("%s/%s", groupIconsDir, imageFile));
	}

	public Object navigatorSTARTTAGHandler(final Attributes attrs) {
		if (result == null) {
			result = new Navigator();
			if (attrs.getIndex(HIDE_ON_LOAD_TAG) > -1) {
				result.setHideOnLoad(Boolean.parseBoolean(attrs.getValue(HIDE_ON_LOAD_TAG)));
			}
			if (attrs.getIndex(WIDTH_TAG) > -1) {
				result.setWidth(attrs.getValue(WIDTH_TAG));
			}
			return result;
		}
		return null;
	}

	/**
	 * Функция построения навигатора из потока, содержащего XML данные.
	 * 
	 * @param stream
	 *            - поток.
	 * @return - навигатор.
	 */
	public Navigator fromStream(final InputStream stream) {
		InputStream streamForParse = XMLUtils.xsdValidateAppDataSafe(stream, "navigator.xsd");

		SAXParser parser = XMLUtils.createSAXParser();

		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				handleStartTag(namespaceURI, lname, qname, attrs);
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				handleEndTag(namespaceURI, lname, qname);
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				handleCharacters(arg0, arg1, arg2);
			}
		};

		try {
			parser.parse(streamForParse, myHandler);
		} catch (Exception e) {
			XMLUtils.stdSAXErrorHandler(e, XML_ERROR_MES);
		}
		return result;
	}

	@Override
	protected String[] getStartTags() {
		return START_TAGS;
	}

	@Override
	protected String[] getEndTrags() {
		return END_TAGS;
	}

	@Override
	public Object handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		if (canHandleStartTag(qname)) {
			if (super.handleStartTag(namespaceURI, lname, qname, attrs) != null) {
				return null;
			}
		}
		if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
			levelSTARTTAGHandler(attrs);
			return null;
		}
		if (actionFactory.canHandleStartTag(qname)) {
			Action action = actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
			NavigatorElement cur = currentElStack.getLast();
			cur.setAction(action);
		}

		return null;
	}

	@Override
	public Object handleEndTag(final String namespaceURI, final String lname, final String qname) {
		if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
			currentElStack.removeLast();
		}

		if (actionFactory.canHandleEndTag(qname)) {
			Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
			NavigatorElement cur = currentElStack.getLast();
			cur.setAction(action);
		}

		return null;
	}

	@Override
	public void handleCharacters(final char[] arg0, final int arg1, final int arg2) {
		actionFactory.handleCharacters(arg0, arg1, arg2);
	}
}
