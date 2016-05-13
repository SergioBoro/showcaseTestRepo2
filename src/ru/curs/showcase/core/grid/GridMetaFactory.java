package ru.curs.showcase.core.grid;

import java.io.IOException;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.event.CompBasedElementFactory;
import ru.curs.showcase.core.html.plugin.PluginFactory;
import ru.curs.showcase.core.sp.RecordSetElementRawData;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания метаданных гридов. Содержит функции считывания
 * динамических и статических настроек грида, устанавливает настройки по
 * умолчанию.
 * 
 */
public class GridMetaFactory extends CompBasedElementFactory {

	private static final String DEF_COL_HOR_ALIGN = "def.column.hor.align";
	private static final String DEF_COL_WIDTH = "def.column.width";
	private static final String DEF_VAL_FONT_COLOR = "def.value.font.color";
	private static final String DEF_VAL_BG_COLOR = "def.value.bg.color";
	private static final String DEF_VAL_FONT_SIZE = "def.value.font.size";
	private static final String DEF_VAL_FONT_BOLD = "def.value.font.bold";
	private static final String DEF_VAL_FONT_IT = "def.value.font.italic";
	private static final String DEF_VAL_FONT_UL = "def.value.font.underline";
	private static final String DEF_VAL_FONT_ST = "def.value.font.strikethrough";
	private static final String DEF_STR_COL_HOR_ALIGN = "def.str.column.hor.align";
	private static final String DEF_NUM_COL_HOR_ALIGN = "def.num.column.hor.align";
	private static final String DEF_DATE_COL_HOR_ALIGN = "def.date.column.hor.align";
	private static final String DEF_IMAGE_COL_HOR_ALIGN = "def.image.column.hor.align";
	private static final String DEF_LINK_COL_HOR_ALIGN = "def.link.column.hor.align";
	private static final String DEF_NUM_COL_DECIMAL_SEPARATOR = "def.num.column.decimal.separator";
	private static final String DEF_NUM_COL_GROUPING_SEPARATOR =
		"def.num.column.grouping.separator";
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

	private static final String XML_ERROR_MES = "настройки грида";

	private static final String COL_SETTINGS_TAG = "col";
	private static final String COLUMN_SET_SETTINGS_TAG = "columnset";
	private static final String COLUMN_HEADER_SETTINGS_TAG = "columnheader";

	private static final String FILTER_MULTISELECTOR_TAG = "multiselector";

	private static final String SORT_TAG = "sort";
	private static final String SORT_COLUMN_TAG = "column";
	private static final String SORT_DIRECTION_TAG = "direction";

	private static final String FILTER_MULTISELECTOR_WINDOWCAPTION_TAG = "windowCaption";
	private static final String FILTER_MULTISELECTOR_DATAWIDTH_TAG = "dataWidth";
	private static final String FILTER_MULTISELECTOR_DATAHEIGHT_TAG = "dataHeight";
	private static final String FILTER_MULTISELECTOR_SELECTEDDATAWIDTH_TAG = "selectedDataWidth";
	private static final String FILTER_MULTISELECTOR_VISIBLERECORDCOUNT_TAG = "visibleRecordCount";
	private static final String FILTER_MULTISELECTOR_PROCCOUNT_TAG = "procCount";
	private static final String FILTER_MULTISELECTOR_PROCLIST_TAG = "procList";
	private static final String FILTER_MULTISELECTOR_PROCLISTANDCOUNT_TAG = "procListAndCount";
	private static final String FILTER_MULTISELECTOR_CURRENTVALUE_TAG = "currentValue";
	private static final String FILTER_MULTISELECTOR_MANUALSEARCH_TAG = "manualSearch";
	private static final String FILTER_MULTISELECTOR_STARTWITH_TAG = "startWith";
	private static final String FILTER_MULTISELECTOR_HIDESTARTSWITH_TAG = "hideStartsWith";
	private static final String FILTER_MULTISELECTOR_NEEDINITSELECTION_TAG = "needInitSelection";

	private static final String AUTO_SELECT_REC_TAG = "autoSelectRecordId";
	private static final String AUTO_SELECT_OFFSET_TAG = "autoSelectOffset";
	private static final String AUTO_SELECT_COL_TAG = "autoSelectColumnId";
	private static final String GRID_WIDTH_TAG = "gridWidth";
	private static final String GRID_HEIGHT_TAG = "gridHeight";
	private static final String FORCE_LOAD_SETTINGS = "forceLoadSettings";
	private static final String PRECISION_TAG = "precision";
	private static final String PROFILE_TAG = "profile";

	private ProfileReader gridProps = null;

	private static final String GRID_WIDTH_DEF_VALUE = "95%";
	private static final int GRID_HEIGHT_DEF_VALUE = 400;

	private static final String GRID_DEFAULT_PROFILE = "default.properties";

	private String profile = GRID_DEFAULT_PROFILE;

	/**
	 * Результат работы фабрики.
	 */
	private GridMetadata result;

	private GridServerState state = null;

	public GridMetaFactory(final RecordSetElementRawData aRaw, final GridServerState aState) {
		super(aRaw);
		state = aState;
	}

	@Override
	public GridContext getCallContext() {
		return (GridContext) super.getCallContext();
	}

	@Override
	public GridMetadata getResult() {
		return result;
	}

	@Override
	protected void prepareData() {
	}

	@Override
	protected void fillResultByData() {
	}

	/**
	 * Построение метаданных грида (загрузка настроек).
	 * 
	 * @return - GridMetadata.
	 */
	public GridMetadata buildMetadata() {
		initResult();
		prepareSettings();

		setupDynamicSettings();
		setupStaticSettings();
		correctSettingsAndData();

		setupPluginSettings();

		return result;
	}

	@Override
	protected void initResult() {
		result = new GridMetadata(getElementInfo());
	}

	@Override
	protected void prepareSettings() {
		super.prepareSettings();
		setXmlDS(getSource().getXmlDS());
	}

	@Override
	protected void setupDynamicSettings() {
		super.setupDynamicSettings();

		setupAutoSelectRecordId();
	}

	@Override
	protected String getSettingsErrorMes() {
		return XML_ERROR_MES;
	}

	@Override
	protected SAXTagHandler getConcreteHandler() {
		return new GridDynamicSettingsReader();
	}

	/**
	 * Класс считывателя настроек грида.
	 * 
	 */
	private class GridDynamicSettingsReader extends StartTagSAXHandler {
		/**
		 * Стартовые тэги, которые будут обработаны данным обработчиком.
		 */
		private final String[] startTags = {
				PROPS_TAG, COL_SETTINGS_TAG, COLUMN_SET_SETTINGS_TAG, COLUMN_HEADER_SETTINGS_TAG,
				FILTER_MULTISELECTOR_TAG, SORT_TAG };

		/**
		 * Конечные тэги, которые будут обработаны данным обработчиком.
		 */
		private final String[] endTags = { COLUMN_SET_SETTINGS_TAG, COLUMN_HEADER_SETTINGS_TAG };

		private List<String> currentIds = null;

		private GridColumnConfig column = null;

		private int colNum = 0;

		@Override
		public Object handleStartTag(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			if (qname.equalsIgnoreCase(COL_SETTINGS_TAG)) {
				return colSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				return propertiesSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(COLUMN_SET_SETTINGS_TAG)) {
				return columnsetSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(COLUMN_HEADER_SETTINGS_TAG)) {
				return columnheaderSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(FILTER_MULTISELECTOR_TAG)) {
				return filtermultiselectorSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(SORT_TAG)) {
				return sortSTARTTAGHandler(attrs);
			}
			return null;
		}

		@Override
		public Object handleEndTag(final String aNamespaceURI, final String lname,
				final String qname) {
			if (qname.equalsIgnoreCase(COLUMN_SET_SETTINGS_TAG)) {
				return columnsetENDTAGHandler();
			}
			if (qname.equalsIgnoreCase(COLUMN_HEADER_SETTINGS_TAG)) {
				return columnheaderENDTAGHandler();
			}
			return null;
		}

		private Object propertiesSTARTTAGHandler(final Attributes attrs) {
			String value;
			Integer intValue;

			if (attrs.getIndex(AUTO_SELECT_REC_TAG) > -1) {
				value = attrs.getValue(AUTO_SELECT_REC_TAG);
				result.setAutoSelectRecordId(value);
			}

			if (attrs.getIndex(AUTO_SELECT_OFFSET_TAG) > -1) {
				value = attrs.getValue(AUTO_SELECT_OFFSET_TAG);
				result.getLiveInfo().setOffset(Integer.parseInt(value));
			}

			if (attrs.getIndex(AUTO_SELECT_COL_TAG) > -1) {
				result.setAutoSelectColumnId(attrs.getValue(AUTO_SELECT_COL_TAG));
			}

			result.getUISettings().setGridWidth(GRID_WIDTH_DEF_VALUE);
			if (attrs.getIndex(GRID_WIDTH_TAG) > -1) {
				result.getUISettings().setGridWidth(attrs.getValue(GRID_WIDTH_TAG));
			}
			result.getUISettings().setGridHeight(GRID_HEIGHT_DEF_VALUE);
			if (attrs.getIndex(GRID_HEIGHT_TAG) > -1) {
				value = attrs.getValue(GRID_HEIGHT_TAG);
				result.getUISettings().setGridHeight(Integer.parseInt(value));
			}
			if (attrs.getIndex(FORCE_LOAD_SETTINGS) > -1) {
				value = attrs.getValue(FORCE_LOAD_SETTINGS);
				state.setForceLoadSettings(Boolean.valueOf(value));
			}
			if (attrs.getIndex(PROFILE_TAG) > -1) {
				profile = attrs.getValue(PROFILE_TAG);
			}

			if (attrs.getIndex(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG) > -1) {
				result.getEventManager().setFireGeneralAndConcreteEvents(
						Boolean.valueOf(attrs.getValue(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG)));
			}

			if (attrs.getIndex(PAGESIZE_TAG) > -1) {
				value = attrs.getValue(PAGESIZE_TAG);
				intValue = Integer.valueOf(value);
				result.getLiveInfo().setLimit(intValue);
			}

			try {
				value = attrs.getValue(TOTAL_COUNT_TAG);
				intValue = Integer.valueOf(value);
			} catch (Exception e) {
				intValue = 0;
			}
			result.getLiveInfo().setTotalCount(intValue);
			state.setTotalCount(intValue);

			return null;
		}

		/**
		 * Стандартная функция создания столбца.
		 * 
		 * @param colId
		 *            - идентификатор столбца.
		 * @return - столбец.
		 */
		private GridColumnConfig createColumn(final String colId) {
			GridColumnConfig res = new GridColumnConfig();
			colNum++;
			res.setId("col" + String.valueOf(colNum));
			res.setCaption(colId);
			return res;
		}

		private Object colSTARTTAGHandler(final Attributes attrs) {
			String colId = attrs.getValue(ID_TAG);

			column = createColumn(colId);

			column.setParentId(getParentId());
			if (column.getParentId() == null) {
				if (result.getVirtualColumns() == null) {
					createVirtualColumns();
				}

				VirtualColumn vc = new VirtualColumn();
				vc.setId(column.getId());
				vc.setVirtualColumnType(VirtualColumnType.COLUMN_REAL);
				result.getVirtualColumns().add(vc);
			}

			result.getColumns().add(column);

			if (attrs.getIndex(WIDTH_TAG) > -1) {
				String width = attrs.getValue(WIDTH_TAG);
				column.setWidth(TextUtils.getIntSizeValue(width));
			}
			if (attrs.getIndex(VISIBLE_TAG) > -1) {
				String value = attrs.getValue(VISIBLE_TAG);
				column.setVisible(Boolean.valueOf(value));
			}
			if (attrs.getIndex(PRECISION_TAG) > -1) {
				String value = attrs.getValue(PRECISION_TAG);
				column.setFormat(value);
			}
			if (attrs.getIndex(TYPE_TAG) > -1) {
				String value = attrs.getValue(TYPE_TAG);
				column.setValueType(GridValueType.valueOf(value));
			}
			if (attrs.getIndex(GeneralConstants.READONLY_TAG) > -1) {
				String value = attrs.getValue(GeneralConstants.READONLY_TAG);
				column.setReadonly(Boolean.valueOf(value));
			}
			if (attrs.getIndex(GeneralConstants.EDITOR_TAG) > -1) {
				String value = attrs.getValue(GeneralConstants.EDITOR_TAG);
				column.setEditor(value);
			}
			if (attrs.getIndex(LINK_ID_TAG) > -1) {
				String value = attrs.getValue(LINK_ID_TAG);
				column.setLinkId(value);
			}
			return null;
		}

		private Object sortSTARTTAGHandler(final Attributes attrs) {

			result.setGridSorting(new GridSorting());

			if (attrs.getIndex(SORT_COLUMN_TAG) > -1) {
				result.getGridSorting().setSortColId(attrs.getValue(SORT_COLUMN_TAG));

			}
			if (attrs.getIndex(SORT_DIRECTION_TAG) > -1) {
				String value = attrs.getValue(SORT_DIRECTION_TAG);
				result.getGridSorting().setSortColDirection(Sorting.valueOf(value));
			}

			return null;
		}

		@Override
		protected String[] getStartTags() {
			return startTags;
		}

		@Override
		protected String[] getEndTrags() {
			return endTags;
		}

		private void createVirtualColumns() {
			result.setVirtualColumns(new ArrayList<VirtualColumn>());

			currentIds = new ArrayList<String>();
		}

		private String getParentId() {
			String parentId = null;
			if ((currentIds != null) && (currentIds.size() > 0)) {
				parentId = currentIds.get(currentIds.size() - 1);
			}
			return parentId;
		}

		private void removeParentId() {
			if ((currentIds != null) && (currentIds.size() > 0)) {
				currentIds.remove(currentIds.size() - 1);
			}
		}

		private Object columnsetSTARTTAGHandler(final Attributes attrs) {
			return columnsetANDcolumnheaderSTARTTAGHandler(attrs, VirtualColumnType.COLUMN_SET);
		}

		private Object columnheaderSTARTTAGHandler(final Attributes attrs) {
			return columnsetANDcolumnheaderSTARTTAGHandler(attrs, VirtualColumnType.COLUMN_HEADER);
		}

		private Object columnsetANDcolumnheaderSTARTTAGHandler(final Attributes attrs,
				final VirtualColumnType virtualColumnType) {
			if (result.getVirtualColumns() == null) {
				createVirtualColumns();
			}

			VirtualColumn vc = new VirtualColumn();
			vc.setId(attrs.getValue(ID_TAG));
			vc.setParentId(getParentId());
			if (attrs.getIndex(WIDTH_TAG) > -1) {
				String width = attrs.getValue(WIDTH_TAG);
				vc.setWidth(width);
			}
			if (attrs.getIndex(GeneralConstants.STYLE_TAG) > -1) {
				String style = attrs.getValue(GeneralConstants.STYLE_TAG);
				vc.setStyle(style);
			}
			vc.setVirtualColumnType(virtualColumnType);
			result.getVirtualColumns().add(vc);

			currentIds.add(vc.getId());

			return null;
		}

		private Object columnsetENDTAGHandler() {
			removeParentId();

			return null;
		}

		private Object columnheaderENDTAGHandler() {
			removeParentId();

			return null;
		}

		// CHECKSTYLE:OFF
		private Object filtermultiselectorSTARTTAGHandler(final Attributes attrs) {
			FilterMultiselector fms = new FilterMultiselector();
			result.getJSInfo().setFilterMultiselector(fms);
			String value;

			if (attrs.getIndex(FILTER_MULTISELECTOR_WINDOWCAPTION_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_WINDOWCAPTION_TAG);
				fms.setWindowCaption(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_DATAWIDTH_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_DATAWIDTH_TAG);
				fms.setDataWidth(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_DATAHEIGHT_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_DATAHEIGHT_TAG);
				fms.setDataHeight(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_SELECTEDDATAWIDTH_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_SELECTEDDATAWIDTH_TAG);
				fms.setSelectedDataWidth(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_VISIBLERECORDCOUNT_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_VISIBLERECORDCOUNT_TAG);
				fms.setVisibleRecordCount(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_PROCCOUNT_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_PROCCOUNT_TAG);
				fms.setProcCount(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_PROCLIST_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_PROCLIST_TAG);
				fms.setProcList(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_PROCLISTANDCOUNT_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_PROCLISTANDCOUNT_TAG);
				fms.setProcListAndCount(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_CURRENTVALUE_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_CURRENTVALUE_TAG);
				fms.setCurrentValue(value);
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_MANUALSEARCH_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_MANUALSEARCH_TAG);
				fms.setManualSearch(Boolean.valueOf(value));
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_STARTWITH_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_STARTWITH_TAG);
				fms.setStartWith(Boolean.valueOf(value));
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_HIDESTARTSWITH_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_HIDESTARTSWITH_TAG);
				fms.setHideStartsWith(Boolean.valueOf(value));
			}
			if (attrs.getIndex(FILTER_MULTISELECTOR_NEEDINITSELECTION_TAG) > -1) {
				value = attrs.getValue(FILTER_MULTISELECTOR_NEEDINITSELECTION_TAG);
				fms.setNeedInitSelection(Boolean.valueOf(value));
			}
			return null;
		}
		// CHECKSTYLE:ON

	}

	private void setupAutoSelectRecordId() {
		if (getCallContext().isFirstLoad()) {
			int pageNumber = 1;
			if (result.getLiveInfo().getOffset() != -1) {

				pageNumber =
					result.getLiveInfo().getOffset() / result.getLiveInfo().getLimit() + 1;
				result.getLiveInfo().setPageNumber(pageNumber);
			}
		}
	}

	private void setupStaticSettings() {
		gridProps = new ProfileReader(profile, SettingsFileType.GRID_PROPERTIES);
		gridProps.init();

		ProfileBasedSettingsApplyStrategy strategy =
			new DefaultGridSettingsApplyStrategy(gridProps, result.getUISettings());
		strategy.apply();

		setupFormatSettings();

		setupViewSettings();
	}

	private void setupFormatSettings() {
		String decimalSeparator = gridProps.getStringValue(DEF_NUM_COL_DECIMAL_SEPARATOR);
		if (decimalSeparator != null) {
			if (decimalSeparator.contains(" ")) {
				decimalSeparator = " ";
			}
			if (decimalSeparator.isEmpty()) {
				decimalSeparator = ".";
			}
		}
		state.setDecimalSeparator(decimalSeparator);

		String groupingSeparator = gridProps.getStringValue(DEF_NUM_COL_GROUPING_SEPARATOR);
		if (groupingSeparator != null) {
			if (groupingSeparator.contains(" ")) {
				groupingSeparator = " ";
			}
		}
		state.setGroupingSeparator(groupingSeparator);

		state.setDateValuesFormat(gridProps.getStringValue(DEF_DATE_VALUES_FORMAT));
	}

	/**
	 * Считывает из файла настроек и устанавливает стандартные свойства вида
	 * информации в гриде.
	 * 
	 */
	private void setupViewSettings() {
		String value;
		boolean boolValue;
		value = gridProps.getStringValue(DEF_VAL_FONT_COLOR);
		if (value != null) {
			result.setTextColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_BG_COLOR);
		if (value != null) {
			result.setBackgroundColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_FONT_SIZE);
		if (value != null) {
			result.setFontSize(value);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_BOLD);
		if (boolValue) {
			result.addFontModifier(FontModifier.BOLD);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_IT);
		if (boolValue) {
			result.addFontModifier(FontModifier.ITALIC);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_UL);
		if (boolValue) {
			result.addFontModifier(FontModifier.UNDERLINE);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_ST);
		if (boolValue) {
			result.addFontModifier(FontModifier.STRIKETHROUGH);
		}
	}

	@Override
	protected void correctSettingsAndData() {
		super.correctSettingsAndData();

		adjustColumns();

		adjustVirtualColumns();

		adjustGridServerState();

		adjustActions();
	}

	private void adjustColumns() {
		for (GridColumnConfig column : result.getColumns()) {
			if (column.getValueType() == null) {
				column.setValueType(GridValueType.STRING);
			}

			String val;
			if (column.getHorizontalAlignment() == null) {
				if (column.getValueType().isString()) {
					val = gridProps.getStringValue(DEF_STR_COL_HOR_ALIGN);
				} else if (column.getValueType().isNumber()) {
					val = gridProps.getStringValue(DEF_NUM_COL_HOR_ALIGN);
				} else if (column.getValueType().isDate()) {
					val = gridProps.getStringValue(DEF_DATE_COL_HOR_ALIGN);
				} else if (column.getValueType() == GridValueType.IMAGE) {
					val = gridProps.getStringValue(DEF_IMAGE_COL_HOR_ALIGN);
				} else if (column.getValueType() == GridValueType.LINK) {
					val = gridProps.getStringValue(DEF_LINK_COL_HOR_ALIGN);
				} else {
					val = gridProps.getStringValue(DEF_COL_HOR_ALIGN);
				}
				if (val != null) {
					column.setHorizontalAlignment(HorizontalAlignment.valueOf(val));
				}
			}

			if (column.getWidth() == null) {
				val = gridProps.getStringValue(DEF_COL_WIDTH);
				if (val != null) {
					column.setWidth(TextUtils.getIntSizeValue(val));
				}
			}
		}
	}

	private void adjustVirtualColumns() {
		if (result.getVirtualColumns() == null) {
			return;
		}

		for (VirtualColumn vc : result.getVirtualColumns()) {
			if (vc.getVirtualColumnType() != VirtualColumnType.COLUMN_REAL) {
				return;
			}
		}
		result.setVirtualColumns(null);
	}

	private void adjustGridServerState() {
		HashMap<String, GridServerColumnConfig> hm =
			new HashMap<String, GridServerColumnConfig>(getResult().getColumns().size());
		for (final GridColumnConfig gcc : getResult().getColumns()) {
			GridServerColumnConfig serv =
				new GridServerColumnConfig(gcc.getId(), gcc.getValueType(), gcc.getFormat());
			hm.put(gcc.getCaption(), serv);
		}
		state.setColumns(hm);
	}

	private void adjustActions() {
		if (result.getDefaultAction() != null) {
			result.getDefaultAction().actualizeBy(getCallContext());
		}

		Action wrong = result.checkActions();
		if (wrong != null) {
			throw new IncorrectElementException(CHECK_ACTION_ERROR, wrong);
		}
	}

	private void setupPluginSettings() {
		String plugin = ((PluginInfo) getElementInfo()).getPlugin();

		result.getJSInfo().setCreateProc("create" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRefreshProc("refresh" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setAddRecordProc("addRecord" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setSaveProc("save" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRevertProc("revert" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setClipboardProc("clipboard" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setPartialUpdate("partialUpdate" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setCurrentLevelUpdate(
				"currentLevelUpdate" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setChildLevelUpdate(
				"childLevelUpdate" + TextUtils.capitalizeWord(plugin));

		result.getJSInfo().getRequiredJS()
				.add(getAdapterForWebServer(getPluginDir(), plugin + ".js"));
	}

	private String getAdapterForWebServer(final String dir, final String adapterFile) {
		String adapter = String.format("%s/%s", dir, adapterFile);
		String adapterOnTomcat =
			String.format("%s/%s/%s", UserDataUtils.SOLUTIONS_DIR, "general", adapter);
		return adapterOnTomcat;
	}

	private String getPluginDir() {
		return String.format("%s/%s", PluginFactory.PLUGINS_DIR,
				((PluginInfo) getElementInfo()).getPlugin());
	}

	public int buildTotalCount() {
		prepareSettings();

		TotalCountReader handler = new TotalCountReader();
		SimpleSAX sax = new SimpleSAX(getSource().getSettings(), handler, getSettingsErrorMes());
		sax.parse();

		try {
			getSource().getSettings().close();
			getSource().setSettings(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

		return handler.getTotalCount();
	}

	/**
	 * Класс считывателя totalCount.
	 * 
	 */
	private class TotalCountReader extends DefaultHandler {
		private int totalCount = 0;

		public int getTotalCount() {
			return totalCount;
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (localName.equalsIgnoreCase(PROPS_TAG)) {
				try {
					String value = atts.getValue(TOTAL_COUNT_TAG);
					totalCount = Integer.valueOf(value);
				} catch (Exception e) {
					totalCount = 0;
				}
			}
		}
	}

}