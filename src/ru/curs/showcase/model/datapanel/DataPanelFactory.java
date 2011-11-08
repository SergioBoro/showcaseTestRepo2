package ru.curs.showcase.model.datapanel;

import java.io.InputStream;
import java.util.regex.*;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.showcase.app.api.HTMLAttrs;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания информационных панелей.
 * 
 * @author den
 * 
 */
public final class DataPanelFactory extends StartTagSAXHandler {
	private static final String SHOW_LOADING_MESSAGE_TAG = "showLoadingMessage";
	private static final String LAYOUT_TAG = "layout";
	private static final String ROWSPAN_TAG = "rowspan";
	public static final String DATAPANEL_XSD = "datapanel.xsd";
	private static final String NEVER_SHOW_IN_PANEL_TAG = "neverShowInPanel";
	private static final String PROC_TAG = "proc";
	private static final String REFRESH_INTERVAL_TAG = "refreshInterval";
	private static final String REFRESH_BY_TIMER_TAG = "refreshByTimer";
	private static final String COLSPAN_TAG = "colspan";

	/**
	 * Создаваемая панель.
	 */
	private DataPanel result;

	/**
	 * Файл с исходными данными.
	 */
	private DataFile<InputStream> file;

	private DataPanelTab currentTab = null;

	private DataPanelTR currentTR = null;

	private DataPanelTD currentTD = null;

	/**
	 * Число элементов.
	 */
	private int elCounter = 0;

	/**
	 * Стартовые тэги, которые будут обработаны данным обработчиком.
	 */
	private final String[] startTags = {
			DP_TAG, TAB_TAG, ELEMENT_TAG, PROC_TAG, RELATED_TAG, GeneralConstants.TR_TAG,
			GeneralConstants.TD_TAG };

	@Override
	protected String[] getStartTags() {
		return startTags;
	}

	public void tabSTARTTAGHandler(final Attributes attrs) {
		currentTab = result.add(attrs.getValue(ID_TAG), attrs.getValue(NAME_TAG));
		handleHTMLAttrs(attrs, currentTab.getHtmlAttrs());
		if (attrs.getIndex(LAYOUT_TAG) > -1) {
			currentTab.setLayout(DataPanelTabLayout.valueOf(attrs.getValue(LAYOUT_TAG)));
		}
		currentTD = null;
		currentTR = null;
	}

	public void datapanelSTARTTAGHandler(final Attributes attrs) {
		result = new DataPanel(file.getId());
	}

	public void trSTARTTAGHandler(final Attributes attrs) {
		currentTR = currentTab.addTR();
		if (attrs.getIndex(ID_TAG) > -1) {
			currentTR.setId(attrs.getValue(ID_TAG));
		}
		if (attrs.getIndex(HEIGHT_TAG) > -1) {
			currentTR.setHeight(attrs.getValue(HEIGHT_TAG));
		}
		handleHTMLAttrs(attrs, currentTR.getHtmlAttrs());
	}

	public void tdSTARTTAGHandler(final Attributes attrs) {
		currentTD = currentTR.add();
		if (attrs.getIndex(ID_TAG) > -1) {
			currentTD.setId(attrs.getValue(ID_TAG));
		}
		if (attrs.getIndex(WIDTH_TAG) > -1) {
			currentTD.setWidth(attrs.getValue(WIDTH_TAG));
		}
		handleHTMLAttrs(attrs, currentTD.getHtmlAttrs());
		if (attrs.getIndex(ROWSPAN_TAG) > -1) {
			currentTD.setRowspan(Integer.valueOf(attrs.getValue(ROWSPAN_TAG)));
		}
		if (attrs.getIndex(COLSPAN_TAG) > -1) {
			currentTD.setColspan(Integer.valueOf(attrs.getValue(COLSPAN_TAG)));
		}
	}

	public void elementSTARTTAGHandler(final Attributes attrs) {
		String value;
		DataPanelElementInfo el = new DataPanelElementInfo(elCounter++, currentTab);
		el.setId(attrs.getValue(ID_TAG));
		el.setType(DataPanelElementType.valueOf(attrs.getValue(TYPE_TAG).toUpperCase()));
		handleHTMLAttrs(attrs, el.getHtmlAttrs());
		if (attrs.getIndex(PROC_ATTR_NAME) > -1) {
			el.setProcName(attrs.getValue(PROC_ATTR_NAME));
		}
		if (el.getType() == DataPanelElementType.WEBTEXT) {
			el.setTransformName(attrs.getValue(TRANSFORM_ATTR_NAME));
		}
		if (el.getType() == DataPanelElementType.XFORMS) {
			el.setTemplateName(attrs.getValue(TEMPLATE_TAG));
		}
		if (attrs.getIndex(HIDE_ON_LOAD_TAG) > -1) {
			value = attrs.getValue(HIDE_ON_LOAD_TAG);
			el.setHideOnLoad(Boolean.valueOf(value));
		}
		if (attrs.getIndex(NEVER_SHOW_IN_PANEL_TAG) > -1) {
			value = attrs.getValue(NEVER_SHOW_IN_PANEL_TAG);
			el.setNeverShowInPanel(Boolean.valueOf(value));
		}
		if (attrs.getIndex(CACHE_DATA_TAG) > -1) {
			value = attrs.getValue(CACHE_DATA_TAG);
			el.setCacheData(Boolean.valueOf(value));
		}
		if (attrs.getIndex(SHOW_LOADING_MESSAGE_TAG) > -1) {
			value = attrs.getValue(SHOW_LOADING_MESSAGE_TAG);
			el.setShowLoadingMessage(Boolean.valueOf(value));
		}
		if (attrs.getIndex(REFRESH_BY_TIMER_TAG) > -1) {
			value = attrs.getValue(REFRESH_BY_TIMER_TAG);
			el.setRefreshByTimer(Boolean.valueOf(value));
		}
		if (attrs.getIndex(REFRESH_INTERVAL_TAG) > -1) {
			value = attrs.getValue(REFRESH_INTERVAL_TAG);
			el.setRefreshInterval(Integer.valueOf(value));
		}
		if (currentTab.getLayout() == DataPanelTabLayout.VERTICAL) {
			currentTab.getElements().add(el);
		} else {
			currentTD.setElement(el);
		}
	}

	public void handleHTMLAttrs(final Attributes attrs, final HTMLAttrs aHtmlAttrs) {
		if (attrs.getIndex(GeneralConstants.STYLE_CLASS_TAG) > -1) {
			aHtmlAttrs.setStyleClass(attrs.getValue(GeneralConstants.STYLE_CLASS_TAG));
		}
		if (attrs.getIndex(GeneralConstants.STYLE_TAG) > -1) {
			aHtmlAttrs.setStyle(attrs.getValue(GeneralConstants.STYLE_TAG));
		}
	}

	public void procSTARTTAGHandler(final Attributes attrs) {
		DataPanelElementProc proc = new DataPanelElementProc();
		setupBaseProps(proc, attrs);
		proc.setType(DataPanelElementProcType.valueOf(attrs.getValue(TYPE_TAG)));
		if (attrs.getIndex(TRANSFORM_ATTR_NAME) > -1) {
			proc.setTransformName(attrs.getValue(TRANSFORM_ATTR_NAME));
		}
		if (attrs.getIndex(SCHEMA_TAG) > -1) {
			proc.setSchemaName(attrs.getValue(SCHEMA_TAG));
		}
		getLastElement().getProcs().put(proc.getId(), proc);
	}

	private DataPanelElementInfo getLastElement() {
		DataPanelElementInfo dpei = null;
		if (currentTD == null) {
			dpei = currentTab.getElements().get(currentTab.getElements().size() - 1);
		} else {
			dpei = currentTD.getElement();
		}
		return dpei;
	}

	public void relatedSTARTTAGHandler(final Attributes attrs) {
		getLastElement().getRelated().add(attrs.getValue(ID_TAG));
	}

	/**
	 * Функция построения панели из XML файла.
	 * 
	 * @param aFile
	 *            - файл с панелью.
	 * @return - информационная панель.
	 */
	public DataPanel fromStream(final DataFile<InputStream> aFile) {
		file = aFile;
		XMLUtils.xsdValidateAppDataSafe(file, DATAPANEL_XSD);
		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) throws SAXException {
				if (canHandleStartTag(qname)) {
					handleStartTag(namespaceURI, lname, qname, attrs);
				}
			}

		};

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(file.getData(), myHandler);
			adjustTableTabWidth();
		} catch (Exception e) {
			XMLUtils.stdSAXErrorHandler(e, file.getName());
		}
		return result;
	}

	private void adjustTableTabWidth() {
		final String stdWidth = "width: 100%";
		for (DataPanelTab tab : result.getTabs()) {
			if (tab.getLayout() == DataPanelTabLayout.TABLE) {
				if ((tab.getHtmlAttrs().getStyle() == null)
						|| (tab.getHtmlAttrs().getStyle().isEmpty())) {
					tab.getHtmlAttrs().setStyle(stdWidth);
				} else if (!consistsWidth(tab.getHtmlAttrs().getStyle())) {
					tab.getHtmlAttrs().setStyle(tab.getHtmlAttrs().getStyle() + "; " + stdWidth);
				}

			}
		}

	}

	private boolean consistsWidth(final String aStyle) {
		Pattern widthPattern = Pattern.compile("\\bwidth:");
		Matcher matcher = widthPattern.matcher(aStyle);
		return matcher.find();
	}
}
