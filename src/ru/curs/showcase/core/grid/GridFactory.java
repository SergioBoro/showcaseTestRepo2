package ru.curs.showcase.core.grid;

import java.io.*;
import java.sql.Types;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.stream.*;

import org.joda.time.DateTime;
import org.slf4j.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.ProfileBasedSettingsApplyStrategy;
import ru.curs.showcase.core.event.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Фабрика для создания гридов. Содержит функции считывания динамических и
 * статических настроек грида, устанавливает настройки по умолчанию, строит грид
 * на основе данных из XmlDS.
 * 
 * @author den
 * 
 */
public class GridFactory extends CompBasedElementFactory {
	private static final String UNIQUE_CHECK_ERROR =
		"В отображаемом наборе присутствуют записи с неуникальным id";

	private static final String AUTO_SELECT_RELATIVE_RECORD_DISABLED =
		"Опция AutoSelectRelativeRecord отключена из-за недостаточного размера страницы грида";

	private static final Logger LOGGER = LoggerFactory.getLogger(GridFactory.class);

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
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

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
	 * Не локальная Locale по умолчанию :) Используется для передачи данных в
	 * приложение, которые плохо обрабатывают текущую Locale.
	 */
	private static final Locale DEF_NON_LOCAL_LOCALE = Locale.US;
	/**
	 * Тэг столбца события в гриде.
	 */
	private static final String EVENT_COLUMN_TAG = "column";
	/**
	 * Префикс имени события, определяющий событие в ячейке.
	 */
	private static final String CELL_PREFIX = "cell";

	/**
	 * Признак того, что нужно применять форматирование для дат и чисел при
	 * формировании грида. По умолчанию - нужно. Отключать эту опцию необходимо
	 * при экспорте в Excel.
	 */
	private Boolean applyLocalFormatting = true;

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

	public GridFactory(final RecordSetElementRawData aSource, final GridServerState aState) {
		super(aSource);
		serverState = aState;
	}

	public GridFactory(final GridContext context, final DataPanelElementInfo aElementInfo,
			final GridServerState aState) {
		this(new RecordSetElementRawData(aElementInfo, context), aState);
	}

	public GridFactory(final RecordSetElementRawData aRaw) {
		this(aRaw, new GridServerState());
	}

	/**
	 * Настройка страничного отображения в гриде.
	 * 
	 */
	private void initPages() {
		getRecordSet().setPageInfo(getCallContext().getPageInfo());
		getResult().setLiveInfo(getCallContext().getLiveInfo());
	}

	@Override
	protected void initResult() {
		result = new Grid(getElementInfo());
		initColumns();
		initRecords();
		initPages();
	}

	@Override
	protected void prepareSettings() {
		super.prepareSettings();
		setXmlDS(getSource().getXmlDS());
	}

	@Override
	protected void prepareData() {
		if (getXmlDS() == null) {
			setXmlDS(getSource().getXmlDS());
		}
	}

	@Override
	protected void fillResultByData() {
		fillColumnsAndRecordsAndEventsByXmlDS();
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
	private void setupStdRecordProps(final Record curRecord) {
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
	private void setupStdColumnProps(final Column column) {
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

	private void calcAutoSelectRecordId() {
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

	private RecordSet getRecordSet() {
		return getResult().getDataSet().getRecordSet();
	}

	/**
	 * Настройка автоматического выделения у грида на основе считанных из БД
	 * данных.
	 */
	private void setupAutoSelecting() {
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
	private Column createColumn(final String colId) {
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

	private ProfileReader getGridProps() {
		return gridProps;
	}

	private void initRecAttrs(final Record record) {
		ru.curs.gwt.datagrid.model.Attributes recAttrs =
			new ru.curs.gwt.datagrid.model.Attributes();
		recAttrs.setValues(new HashMap<String, String>());
		record.setAttributes(recAttrs);
	}

	public void checkRecordIdUniqueness() {
		List<String> ids = new ArrayList<>();
		for (Record rec : getRecordSet().getRecords()) {
			if (ids.indexOf(rec.getId()) > -1) {
				throw new ResultSetHandleException(UNIQUE_CHECK_ERROR);
			}
			ids.add(rec.getId());
		}
	}

	// -------------------------------------------------

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
		if (getXmlDS() == null) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}

	/**
	 * Функция для замены служебных символов XML (только XML, не HTML!) в
	 * описании ссылки в гриде.
	 * 
	 * @param value
	 *            - текст ссылки.
	 * @return - исправленный текст ссылки.
	 */
	public static String makeSafeXMLAttrValues(final String value) {
		String res = value.trim();

		Pattern pattern = Pattern.compile("(\\&(?!quot;)(?!lt;)(?!gt;)(?!amp;)(?!apos;))");
		Matcher matcher = pattern.matcher(res);
		res = matcher.replaceAll("&amp;");

		pattern =
			Pattern.compile("(?<!=)(\")(?!\\s*openInNewTab)(?!\\s*text)(?!\\s*href)(?!\\s*image)(?!\\s*/\\>)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&quot;");

		pattern = Pattern.compile("(?<!^)(\\<)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&lt;");

		pattern = Pattern.compile("(\\>)(?!$)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&gt;");

		res = res.replace("'", "&apos;");

		return res;
	}

	/**
	 * Считывает события для записи и ее ячеек, а также доп. параметры записи.
	 * Один из таких параметров - стиль CSS. Есть возможность задать несколько
	 * стилей для каждой записи.
	 */
	private void readEvents(final Record record, final String data) {
		EventFactory<GridEvent> factory =
			new EventFactory<GridEvent>(GridEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX, getElementInfo().getType()
				.getPropsSchemaName());
		SAXTagHandler recPropHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				String value;
				if (record.getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG) == null) {
					initRecAttrs(record);
					value = attrs.getValue(NAME_TAG);
				} else {
					value =
						record.getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG) + " "
								+ attrs.getValue(NAME_TAG);
				}

				record.getAttributes().setValue(GeneralConstants.STYLE_CLASS_TAG, value);
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { GeneralConstants.STYLE_CLASS_TAG };
				return tags;
			}

		};
		factory.addHandler(recPropHandler);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(new ID(record.getId()), data));
	}

	private String getStringValueOfNumber(final Double value, final Column col) {
		NumberFormat nf;
		if (applyLocalFormatting) {
			nf = NumberFormat.getNumberInstance();
		} else {
			nf = NumberFormat.getNumberInstance(DEF_NON_LOCAL_LOCALE);
		}
		if (col.getFormat() != null) {
			nf.setMinimumFractionDigits(Integer.parseInt(col.getFormat()));
			nf.setMaximumFractionDigits(Integer.parseInt(col.getFormat()));
		}
		return nf.format(value);
	}

	private String getStringValueOfDate(final java.util.Date date, final Column col) {
		DateFormat df = null;
		String value = getGridProps().getStringValue(DEF_DATE_VALUES_FORMAT);
		Integer style = DateFormat.DEFAULT;
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}
		if (col.getValueType() == GridValueType.DATE) {
			if (applyLocalFormatting) {
				df = DateFormat.getDateInstance(style);
			} else {
				df = DateFormat.getDateInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.TIME) {
			if (applyLocalFormatting) {
				df = DateFormat.getTimeInstance(style);
			} else {
				df = DateFormat.getTimeInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.DATETIME) {
			if (applyLocalFormatting) {
				df = DateFormat.getDateTimeInstance(style, style);
			} else {
				df = DateFormat.getDateTimeInstance(style, style, DEF_NON_LOCAL_LOCALE);
			}
		}
		return df.format(date);
	}

	private void calcRecordsCount() {
		getRecordSet().setPagesTotal(
				(int) Math.ceil((float) serverState().getTotalCount()
						/ getRecordSet().getPageSize()));

		if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
			getResult().getLiveInfo().setTotalCount(serverState().getTotalCount());
		}
	}

	private void determineValueType(final Column column, final int sqlType) {
		if (column.getValueType() != null) {
			return; // тип задан явно
		}
		if (SQLUtils.isStringType(sqlType)) {
			column.setValueType(GridValueType.STRING);
		} else if (SQLUtils.isIntType(sqlType)) {
			column.setValueType(GridValueType.INT);
		} else if (SQLUtils.isFloatType(sqlType)) {
			column.setValueType(GridValueType.FLOAT);
		} else if (SQLUtils.isDateType(sqlType)) {
			column.setValueType(GridValueType.DATE);
		} else if (SQLUtils.isTimeType(sqlType)) {
			column.setValueType(GridValueType.TIME);
		} else if (SQLUtils.isDateTimeType(sqlType)) {
			column.setValueType(GridValueType.DATETIME);
		} else {
			column.setValueType(GridValueType.STRING);
		}
	}

	public Boolean getApplyLocalFormatting() {
		return applyLocalFormatting;
	}

	public void setApplyLocalFormatting(final Boolean aApplyLocalFormatting) {
		applyLocalFormatting = aApplyLocalFormatting;
	}

	private static final String SAX_ERROR_MES = "XML-датасет грида";

	private void fillColumnsAndRecordsAndEventsByXmlDS() {

		XmlDSHandler handler = new XmlDSHandler();
		SimpleSAX sax = new SimpleSAX(getXmlDS(), handler, SAX_ERROR_MES);
		sax.parse();

		try {
			getXmlDS().close();
			setXmlDS(null);
			getSource().setXmlDS(null);
		} catch (IOException e) {
			throw new SAXError(e);
		}

		postProcessingByXmlDS();
		checkRecordIdUniqueness();
		if (getElementInfo().loadByOneProc()) {
			serverState().setTotalCount(handler.getCounterRecord());
		}
		calcRecordsCount();

	}

	/**
	 * Формирует грид на основе XML-датасета.
	 */
	private class XmlDSHandler extends DefaultHandler {

		private boolean processRecord = false;
		private boolean processValue = false;

		private int firstNumber;
		private int lastNumber;
		private int counterRecord = 0;
		private int counterColumn = 1;

		private Column curColumn = null;
		private Record curRecord = null;
		private ByteArrayOutputStream osValue = null;
		private XMLStreamWriter writerValue = null;

		public XmlDSHandler() {
			super();

			if (getElementInfo().loadByOneProc()) {
				if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
					firstNumber = getResult().getLiveInfo().getFirstRecord();
					lastNumber = firstNumber + getResult().getLiveInfo().getLimit();
				} else {
					firstNumber = getRecordSet().getPageInfo().getFirstRecord();
					lastNumber = firstNumber + getRecordSet().getPageSize();
				}
			} else {
				firstNumber = 1;
				if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
					lastNumber = firstNumber + getResult().getLiveInfo().getLimit();
				} else {
					lastNumber = firstNumber + getRecordSet().getPageSize();
				}
			}
		}

		private int getRecordIndex() {
			int index = counterRecord;
			if (!getElementInfo().loadByOneProc()) {
				if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
					index = index + getResult().getLiveInfo().getFirstRecord() - 1;
				} else {
					index = index + getRecordSet().getPageInfo().getFirstRecord() - 1;
				}
			}
			return index;
		}

		private int getCounterRecord() {
			return counterRecord;
		}

		private boolean isRequiredPage() {
			return (firstNumber <= counterRecord) && (counterRecord < lastNumber);
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				counterRecord++;
				if (isRequiredPage()) {
					processRecord = true;
					curRecord = new Record();
					return;
				}
			}

			if (!processRecord || !isRequiredPage()) {
				return;
			}

			if (processValue) {
				try {
					writerValue.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerValue.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			} else {
				String colId = XMLUtils.unEscapeTagXml(localName);
				curColumn = getResult().getColumnById(colId);

				if (counterRecord == firstNumber) {
					if (curColumn == null) {
						curColumn = createColumn(colId);
						getResult().getDataSet().getColumnSet().getColumns().add(curColumn);
					}
					curColumn.setIndex(counterColumn - 1);
					int sqltype;
					if (atts.getValue(SQLTYPE_ATTR) == null) {
						sqltype = Types.VARCHAR;
					} else {
						sqltype = Integer.valueOf(atts.getValue("sqltype"));
					}
					determineValueType(curColumn, sqltype);
					setupStdColumnProps(curColumn);
					curColumn.setSorting(getCallContext().getSortingForColumn(curColumn));
					counterColumn++;
				}

				processValue = true;
				osValue = new ByteArrayOutputStream();
				try {
					writerValue =
						XMLOutputFactory.newInstance().createXMLStreamWriter(osValue,
								TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (!isRequiredPage()) {
				return;
			}

			if (processValue) {
				try {
					writerValue.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (!isRequiredPage()) {
				return;
			}

			if (RECORD_TAG.equals(localName)) {
				curRecord.setIndex(getRecordIndex());
				setupStdRecordProps(curRecord);
				getRecordSet().getRecords().add(curRecord);
				processRecord = false;
				return;
			}

			if (processValue) {
				String colId = XMLUtils.unEscapeTagXml(localName);
				try {
					if (curColumn == getResult().getColumnById(colId)) {
						String value = osValue.toString(TextUtils.DEF_ENCODING);
						if (curColumn.getValueType().isGeneralizedString()) {
							value = XMLUtils.unEscapeValueXml(value);
						}
						curRecord.setValue(curColumn.getId(), value);
						writerValue.close();
						processValue = false;
					} else {
						writerValue.writeEndElement();
					}
				} catch (XMLStreamException | UnsupportedEncodingException e) {
					throw new SAXError(e);
				}
			}
		}
	}

	private void postProcessingByXmlDS() {
		Column idColumn = getResult().getColumnById(ID_TAG);
		Column propsColumn = getResult().getColumnById(PROPS_TAG);
		for (Record rec : getRecordSet().getRecords()) {
			if (idColumn != null) {
				rec.setId(rec.getValue(idColumn));
			} else {
				rec.setId(String.valueOf(rec.getIndex()));
			}
			if (propsColumn != null) {
				readEvents(rec, "<" + PROPS_TAG + ">" + rec.getValue(propsColumn) + "</"
						+ PROPS_TAG + ">");
			}
		}
		if (idColumn != null) {
			getResult().getDataSet().getColumnSet().getColumns().remove(idColumn);
		}
		if (propsColumn != null) {
			getResult().getDataSet().getColumnSet().getColumns().remove(propsColumn);
		}

		for (Record rec : getRecordSet().getRecords()) {
			for (Column col : getResult().getDataSet().getColumnSet().getColumns()) {
				rec.setValue(col.getId(), getCellValueForXmlDS(rec.getValue(col), col));
			}
		}
	}

	private String getCellValueForXmlDS(final String aValue, final Column col) {
		String value = aValue;
		if (value == null) {
			value = "";
		}
		if (value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
			value = "";
			return value;
		}
		if (col.getValueType() == GridValueType.IMAGE) {
			value =
				String.format("%s/%s",
						UserdataUtils.getRequiredProp(UserdataUtils.IMAGES_IN_GRID_DIR), value);
		} else if (col.getValueType() == GridValueType.LINK) {
			value = UserdataUtils.replaceVariables(value);
			value = normalizeLink(value);
			value = makeSafeXMLAttrValues(value);
		} else if (col.getValueType() == GridValueType.DOWNLOAD) {
			value = UserdataUtils.replaceVariables(value);
		} else if (col.getValueType().isDate()) {
			DateTime dt = new DateTime(value);
			java.util.Date date = dt.toDate();
			value = getStringValueOfDate(date, col);
		} else if (col.getValueType().isNumber()) {
			value = getStringValueOfNumber(Double.valueOf(value), col);
		}
		return value;
	}

	private static String normalizeLink(final String aValue) {
		String value = aValue.trim();
		value = value.replace("></" + GridValueType.LINK.toString().toLowerCase() + ">", "/>");

		return value;
	}

}