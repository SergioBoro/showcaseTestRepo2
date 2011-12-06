package ru.curs.showcase.model.grid;

import java.util.*;

import org.slf4j.*;
import org.xml.sax.Attributes;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.event.CompBasedElementFactory;
import ru.curs.showcase.model.sp.*;
import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;

/**
 * Базовый класс построителя грида. Содержит функции считывания динамических и
 * статических настроек грида, а также устанавливает настройки по умолчанию.
 * 
 * @author den
 * 
 */
public abstract class AbstractGridFactory extends CompBasedElementFactory {
	private static final String AUTO_SELECT_RELATIVE_RECORD_DISABLED =
		"Опция AutoSelectRelativeRecord отключена из-за недостаточного размера страницы грида";

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractGridFactory.class);

	private static final String DEF_COL_HOR_ALIGN = "def.column.hor.align";
	private static final String DEF_COL_VALUE_DISPLAY_MODE = "def.column.value.display.mode";
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

	private static final String RELATIVE_NUMBER_TOO_BIG_ERROR =
		"относительный autoSelectRecordId = %d выходит за пределы страницы (%d записей)";
	private static final String XML_ERROR_MES = "настройки грида";
	private static final String COL_SETTINGS_TAG = "col";

	private static final String AUTO_SELECT_REC_TAG = "autoSelectRecordId";
	private static final String AUTO_SELECT_COL_TAG = "autoSelectColumnId";
	private static final String AUTO_SELECT_RELATIVE = "autoSelectRelativeRecord";
	private static final String PRECISION_TAG = "precision";
	private static final String PROFILE_TAG = "profile";

	private ProfileReader gridProps = null;

	/**
	 * Результат работы фабрики.
	 */
	private Grid result;

	private final GridServerState serverState;

	private Integer autoSelectRecordId = null;

	@Override
	public Grid getResult() {
		return result;
	}

	@Override
	protected void correctSettingsAndData() {
		super.correctSettingsAndData();

		setupAutoSelecting();
		calcPageCount();

		updateServerState();
	}

	private void updateServerState() {
		serverState().setDefaultAction(getResult().getDefaultAction());
		serverState().setColumnSet(getResult().getDataSet().getColumnSet());
	}

	private void calcPageCount() {
		result.getUISettings().setPagesButtonCount(
				Math.min(result.getUISettings().getPagesButtonCount(), result.getDataSet()
						.getRecordSet().getPagesTotal()));

	}

	@Override
	public Grid build() throws Exception {
		return (Grid) super.build();
	}

	@Override
	public Grid buildStepOne() {
		return (Grid) super.buildStepOne();
	}

	@Override
	public Grid buildStepTwo() throws Exception {
		return (Grid) super.buildStepTwo();
	}

	@Override
	public GridContext getCallContext() {
		return (GridContext) super.getCallContext();
	}

	public AbstractGridFactory(final ElementRawData aSource, final GridServerState aState) {
		super(aSource);
		serverState = aState;
	}

	/**
	 * Функция заполнения данных о столбцах.
	 * 
	 */
	protected abstract void fillColumns();

	/**
	 * Функция заполнения данных о записях.
	 * 
	 */
	protected abstract void fillRecordsAndEvents();

	/**
	 * Настройка страничного отображения в гриде.
	 * 
	 */
	private void initPages() {
		getRecordSet().setPageInfo(getCallContext().getPageInfo());
	}

	@Override
	protected void initResult() {
		result = new Grid();
		initColumns();
		initRecords();
		initPages();
	}

	@Override
	protected void fillResultByData() {
		fillColumns();
		fillRecordsAndEvents();
	}

	private RecordSet initRecords() {
		RecordSet rs = new RecordSet();
		rs.setRecords(new ArrayList<Record>());
		result.getDataSet().setRecordSet(rs);
		return rs;
	}

	private ColumnSet initColumns() {
		ColumnSet cs = new ColumnSet();
		cs.setColumns(new ArrayList<Column>());
		result.getDataSet().setColumnSet(cs);
		return cs;
	}

	/**
	 * Считывает из файла настроек и устанавливает стандартные свойства записи
	 * грида.
	 * 
	 * @param curRecord
	 *            - запись.
	 */
	protected void setupStdRecordProps(final Record curRecord) {
		String value;
		boolean boolValue;
		value = gridProps.getStringValue(DEF_VAL_FONT_COLOR);
		if (value != null) {
			curRecord.setTextColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_BG_COLOR);
		if (value != null) {
			curRecord.setBackgroundColor(value);
		}
		value = gridProps.getStringValue(DEF_VAL_FONT_SIZE);
		if (value != null) {
			curRecord.setFontSize(value);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_BOLD);
		if (boolValue) {
			curRecord.addFontModifier(FontModifier.BOLD);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_IT);
		if (boolValue) {
			curRecord.addFontModifier(FontModifier.ITALIC);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_UL);
		if (boolValue) {
			curRecord.addFontModifier(FontModifier.UNDERLINE);
		}
		boolValue = gridProps.isTrueValue(DEF_VAL_FONT_ST);
		if (boolValue) {
			curRecord.addFontModifier(FontModifier.STRIKETHROUGH);
		}
	}

	/**
	 * Функция считывания и установки стандартных свойств столбца.
	 * 
	 * @param column
	 *            - столбец.
	 */
	protected void setupStdColumnProps(final Column column) {
		String val;
		column.setVisible(true);
		val = gridProps.getStringValue(DEF_COL_VALUE_DISPLAY_MODE);
		if (val != null) {
			column.setDisplayMode(ColumnValueDisplayMode.valueOf(val));
		}

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
		val = gridProps.getStringValue(DEF_COL_WIDTH);
		if ((val != null) && (column.getWidth() == null)) {
			column.setWidth(val);
		}
	}

	public GridServerState serverState() {
		return serverState;
	}

	/**
	 * Класс считывателя настроек грида.
	 * 
	 * @author den
	 * 
	 */
	private class GridDynamicSettingsReader extends StartTagSAXHandler {
		/**
		 * Стартовые тэги, которые будут обработаны данным обработчиком.
		 */
		private final String[] startTags = { COL_SETTINGS_TAG, PROPS_TAG };

		@Override
		public Object handleStartTag(final String namespaceURI, final String lname,
				final String qname, final Attributes attrs) {
			if (qname.equalsIgnoreCase(COL_SETTINGS_TAG)) {
				return colSTARTTAGHandler(attrs);
			}
			if (qname.equalsIgnoreCase(PROPS_TAG)) {
				return propertiesSTARTTAGHandler(attrs);
			}
			return null;
		}

		private Object propertiesSTARTTAGHandler(final Attributes attrs) {
			String value;
			Integer intValue;
			if (attrs.getIndex(AUTO_SELECT_RELATIVE) > -1) {
				value = attrs.getValue(AUTO_SELECT_RELATIVE);
				serverState().setAutoSelectRelativeRecord(Boolean.valueOf(value));
			}
			if (attrs.getIndex(AUTO_SELECT_REC_TAG) > -1) {
				value = attrs.getValue(AUTO_SELECT_REC_TAG);
				serverState().setAutoSelectRecordId(Integer.parseInt(value));
			}
			if (attrs.getIndex(AUTO_SELECT_COL_TAG) > -1) {
				serverState().setAutoSelectColumnId(attrs.getValue(AUTO_SELECT_COL_TAG));
			}
			if (attrs.getIndex(PROFILE_TAG) > -1) {
				serverState().setProfile(attrs.getValue(PROFILE_TAG));
			}
			if (attrs.getIndex(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG) > -1) {
				getResult().getEventManager().setFireGeneralAndConcreteEvents(
						Boolean.valueOf(attrs.getValue(FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG)));
			}
			if (attrs.getIndex(PAGESIZE_TAG) > -1) {
				value = attrs.getValue(PAGESIZE_TAG);
				intValue = Integer.valueOf(value);
				getResult().getDataSet().getRecordSet().setPageSize(intValue);
			}
			if (!getElementInfo().loadByOneProc()) {
				value = attrs.getValue(TOTAL_COUNT_TAG);
				intValue = Integer.valueOf(value);
				serverState().setTotalCount(intValue);
			}
			return null;
		}

		private Object colSTARTTAGHandler(final Attributes attrs) {
			String colId = attrs.getValue(ID_TAG);
			Column col = getResult().getColumnById(colId);
			if (col == null) {
				col = createColumn(colId);
				getResult().getDataSet().getColumnSet().getColumns().add(col);
			}
			if (attrs.getIndex(WIDTH_TAG) > -1) {
				String width = attrs.getValue(WIDTH_TAG);
				col.setWidth(width);
			}
			if (attrs.getIndex(PRECISION_TAG) > -1) {
				String value = attrs.getValue(PRECISION_TAG);
				col.setFormat(value);
			}
			if (attrs.getIndex(TYPE_TAG) > -1) {
				String value = attrs.getValue(TYPE_TAG);
				col.setValueType(GridValueType.valueOf(value));
			}
			if (attrs.getIndex(LINK_ID_TAG) > -1) {
				String value = attrs.getValue(LINK_ID_TAG);
				col.setLinkId(value);
			}
			return null;
		}

		@Override
		protected String[] getStartTags() {
			return startTags;
		}

	}

	@Override
	protected void setupDynamicSettings() {
		if (getCallContext().isFirstLoad()) {
			super.setupDynamicSettings();
			loadStaticSettings();
		} else {
			loadStaticSettings();
			loadStoredState();
		}
		calcAutoSelectRecordId();
	}

	private void loadStoredState() {
		if (serverState.getColumnSet() != null) {
			getResult().getDataSet().setColumnSet(serverState.getColumnSet());
		}
		getResult().setDefaultAction(serverState.getDefaultAction());
	}

	private void loadStaticSettings() {
		gridProps =
			new ProfileReader(serverState().getProfile(), SettingsFileType.GRID_PROPERTIES);
		gridProps.init();
		ProfileBasedSettingsApplyStrategy strategy =
			new DefaultGridSettingsApplyStrategy(gridProps, getResult().getUISettings());
		strategy.apply();
	}

	protected void calcAutoSelectRecordId() {
		autoSelectRecordId = serverState().getAutoSelectRecordId();
		if (autoSelectRecordId == null) {
			return;
		}

		if (serverState().getAutoSelectRelativeRecord()) {
			recalcAutoSelectRecordId();
		} else {
			recalcPageNumber();
		}

	}

	private void recalcAutoSelectRecordId() {
		if (autoSelectRecordId >= getRecordSet().getPageSize()) {
			if (getCallContext().isFirstLoad()) {
				throw new InconsistentSettingsFromDBException(String.format(
						RELATIVE_NUMBER_TOO_BIG_ERROR, autoSelectRecordId, getRecordSet()
								.getPageSize()));
			} else {
				LOGGER.info(AUTO_SELECT_RELATIVE_RECORD_DISABLED);
				return;
			}
		}
		autoSelectRecordId =
			autoSelectRecordId + getRecordSet().getPageSize()
					* (getRecordSet().getPageNumber() - 1);
	}

	private void recalcPageNumber() {
		if (getCallContext().isFirstLoad()) {
			if (autoSelectRecordId >= getRecordSet().getPageSize()) {
				getRecordSet().setPageNumber(
						(int) Math.ceil(((float) autoSelectRecordId + 1)
								/ getRecordSet().getPageSize()));
			}
		}
	}

	protected RecordSet getRecordSet() {
		return getResult().getDataSet().getRecordSet();
	}

	/**
	 * Настройка автоматического выделения у грида на основе считанных из БД
	 * данных.
	 */
	protected void setupAutoSelecting() {
		if (autoSelectRecordId == null) {
			return;
		}

		for (Record current : getRecordSet().getRecords()) {
			if (current.getIndex().equals(autoSelectRecordId)) {
				getResult().setAutoSelectRecord(current);
			}
		}

		if (serverState().getAutoSelectColumnId() == null) {
			return;
		}
		for (Column current : getResult().getDataSet().getColumnSet().getColumns()) {
			if (current.getId().equals(serverState().getAutoSelectColumnId())) {
				getResult().setAutoSelectColumn(current);
			}
		}
	}

	@Override
	protected SAXTagHandler getConcreteHandler() {
		return new GridDynamicSettingsReader();
	}

	/**
	 * Стандартная функция создания столбца.
	 * 
	 * @param colId
	 *            - идентификатор столбца.
	 * @return - столбец.
	 */
	protected Column createColumn(final String colId) {
		Column res = new Column();
		res.setId(colId);
		res.setCaption(colId);
		res.setMinWidthPx(0);
		return res;
	}

	@Override
	protected String getSettingsErrorMes() {
		return XML_ERROR_MES;
	}

	protected ProfileReader getGridProps() {
		return gridProps;
	}

	protected void initRecAttrs(final Record record) {
		ru.curs.gwt.datagrid.model.Attributes recAttrs =
			new ru.curs.gwt.datagrid.model.Attributes();
		recAttrs.setValues(new HashMap<String, String>());
		record.setAttributes(recAttrs);
	}

}