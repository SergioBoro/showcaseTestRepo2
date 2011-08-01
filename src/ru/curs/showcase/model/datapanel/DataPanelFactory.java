package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

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
	public static final String DATAPANEL_XSD = "datapanel.xsd";
	private static final String NEVER_SHOW_IN_PANEL_TAG = "neverShowInPanel";
	private static final String PROC_TAG = "proc";
	private static final String REFRESH_INTERVAL_TAG = "refreshInterval";
	private static final String REFRESH_BY_TIMER_TAG = "refreshByTimer";

	/**
	 * Создаваемая панель.
	 */
	private DataPanel result;

	/**
	 * Файл с исходными данными.
	 */
	private DataFile<InputStream> file;

	/**
	 * Текущая вкладка.
	 */
	private DataPanelTab currentTab = null;

	/**
	 * Число элементов.
	 */
	private int elCounter = 0;

	/**
	 * Стартовые тэги, которые будут обработаны данным обработчиком.
	 */
	private final String[] startTags = { DP_TAG, TAB_TAG, ELEMENT_TAG, PROC_TAG };

	@Override
	protected String[] getStartTags() {
		return startTags;
	}

	/**
	 * Обработчик тэга tab.
	 * 
	 * @param attrs
	 *            - атрибуты.
	 */
	public void tabSTARTTAGHandler(final Attributes attrs) {
		currentTab = result.add(attrs.getValue(ID_TAG), attrs.getValue(NAME_TAG));
	}

	/**
	 * Обработчик тэга datapanel.
	 * 
	 * @param attrs
	 *            - атрибуты.
	 */
	public void datapanelSTARTTAGHandler(final Attributes attrs) {
		result = new DataPanel(file.getId());
	}

	/**
	 * Обработчик тэга element.
	 * 
	 * @param attrs
	 *            - атрибуты.
	 */
	public void elementSTARTTAGHandler(final Attributes attrs) {
		String value;
		DataPanelElementInfo el = new DataPanelElementInfo(elCounter++, currentTab);
		el.setId(attrs.getValue(ID_TAG));
		el.setType(DataPanelElementType.valueOf(attrs.getValue(TYPE_TAG).toUpperCase()));
		if (attrs.getIndex(STYLE_CLASS_TAG) > -1) {
			el.setStyleClass(attrs.getValue(STYLE_CLASS_TAG));
		}
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
		if (attrs.getIndex(REFRESH_BY_TIMER_TAG) > -1) {
			value = attrs.getValue(REFRESH_BY_TIMER_TAG);
			el.setRefreshByTimer(Boolean.valueOf(value));
		}
		if (attrs.getIndex(REFRESH_INTERVAL_TAG) > -1) {
			value = attrs.getValue(REFRESH_INTERVAL_TAG);
			el.setRefreshInterval(Integer.valueOf(value));
		}
		currentTab.getElements().add(el);
	}

	/**
	 * Обработчик тэга proc.
	 * 
	 * @param attrs
	 *            - атрибуты.
	 */
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
		currentTab.getElements().get(currentTab.getElements().size() - 1).getProcs()
				.put(proc.getId(), proc);
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
		} catch (Exception e) {
			XMLUtils.stdSAXErrorHandler(e, file.getName());
		}
		return result;
	}
}
