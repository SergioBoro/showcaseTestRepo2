package ru.curs.showcase.core.grid;

import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import ru.curs.celesta.CelestaException;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.event.ActionFactory;
import ru.curs.showcase.core.html.plugin.PluginFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.SimpleSAX;

/**
 * Фабрика для создания метаданных лирагридов.
 * 
 */
public class LyraGridMetaFactory {
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

	private static final String DEFAULT_ACTION_XML_ERROR = "настройки грида";
	private static final String CHECK_DEFAULT_ACTION_ERROR =
		"Некорректное описание действия в элементе инф. панели: ";

	private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final String GRID_WIDTH_DEF_VALUE = "95%";
	private static final int GRID_HEIGHT_DEF_VALUE = 400;
	private static final int COLUMN_WIDTH_DEF_VALUE = 100;

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private final BasicGridForm basicGridForm;

	private GridMetadata result;

	private String profile = null;
	private ProfileReader gridProps = null;

	public LyraGridMetaFactory(final LyraGridContext aContext, final DataPanelElementInfo aElInfo,
			final BasicGridForm aBasicGridForm) {
		context = aContext;
		elInfo = aElInfo;
		basicGridForm = aBasicGridForm;
	}

	/**
	 * Построение метаданных лирагрида.
	 * 
	 * @return - GridMetadata.
	 * @throws CelestaException
	 */
	public GridMetadata buildMetadata() throws CelestaException {
		initResult();

		setupDynamicSettings();
		setupDefaultAction();
		setupStaticSettings();

		setupColumns();

		setupPluginSettings();

		setupUnused();

		return result;
	}

	private void initResult() {
		result = new GridMetadata(elInfo);
	}

	private void setupDynamicSettings() throws CelestaException {

		if (basicGridForm.getFormProperties().getGridwidth() == null) {
			result.getUISettings().setGridWidth(GRID_WIDTH_DEF_VALUE);
		} else {
			result.getUISettings().setGridWidth(basicGridForm.getFormProperties().getGridwidth());
		}

		if (basicGridForm.getFormProperties().getGridheight() == null) {
			result.getUISettings().setGridHeight(GRID_HEIGHT_DEF_VALUE);
		} else {
			result.getUISettings().setGridHeight(
					TextUtils.getIntSizeValue(basicGridForm.getFormProperties().getGridheight()));
		}

		if (basicGridForm.getFormProperties().getHeader() != null) {
			result.setHeader(basicGridForm.getFormProperties().getHeader());
		}
		if (basicGridForm.getFormProperties().getFooter() != null) {
			result.setFooter(basicGridForm.getFormProperties().getFooter());
		}

		if (basicGridForm.rec().getOrderBy() != null) {
			result.setGridSorting(new GridSorting());

			String s = basicGridForm.rec().getOrderBy();
			int pos = s.indexOf(",");
			if (pos > -1) {
				s = s.substring(0, pos);
			}
			s = s.trim();

			result.getGridSorting().setSortColId(s.substring(1, s.lastIndexOf("\"")));

			if (s.toLowerCase().contains(" desc")) {
				result.getGridSorting().setSortColDirection(Sorting.DESC);
			}
		}

		result.getLiveInfo().setOffset(0);
		result.getLiveInfo().setLimit(basicGridForm.getGridHeight());
		result.getLiveInfo().setTotalCount(basicGridForm.getApproxTotalCount());

	}

	private void setupDefaultAction() {
		if (basicGridForm.getFormProperties().getDefaultaction() == null) {
			return;
		}

		DefaultHandler myHandler = new DefaultHandler() {

			private final ActionFactory actionFactory = new ActionFactory(context);

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final org.xml.sax.Attributes attrs) {
				if (actionFactory.canHandleStartTag(qname)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					result.setDefaultAction(action);
					return;
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (actionFactory.canHandleEndTag(qname)) {
					Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
					result.setDefaultAction(action);
					return;
				}
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				actionFactory.handleCharacters(arg0, arg1, arg2);
			}
		};

		SimpleSAX sax =
			new SimpleSAX(TextUtils.stringToStream(basicGridForm.getFormProperties()
					.getDefaultaction()), myHandler, DEFAULT_ACTION_XML_ERROR);
		sax.parse();

		if (result.getDefaultAction() != null) {
			result.getDefaultAction().actualizeBy(context);
		}

		Action wrong = result.checkActions();
		if (wrong != null) {
			throw new IncorrectElementException(CHECK_DEFAULT_ACTION_ERROR, wrong);
		}

	}

	private void setupStaticSettings() {

		if (basicGridForm.getFormProperties().getProfile() == null) {
			profile = GRID_DEFAULT_PROFILE;
		} else {
			profile = basicGridForm.getFormProperties().getProfile();
		}

		gridProps = new ProfileReader(profile, SettingsFileType.GRID_PROPERTIES);
		gridProps.init();

		ProfileBasedSettingsApplyStrategy strategy =
			new DefaultGridSettingsApplyStrategy(gridProps, result.getUISettings());
		strategy.apply();

		setupViewSettings();
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

	private void setupColumns() {
		Map<String, LyraFormField> lyraFields = basicGridForm.getFieldsMeta();

		for (LyraFormField field : lyraFields.values()) {

			if ("_properties_".equalsIgnoreCase(field.getName())) {
				continue;
			}

			GridColumnConfig column = new GridColumnConfig();

			column.setId(field.getName());
			column.setCaption(field.getCaption());

			column.setVisible(field.isVisible());
			column.setReadonly(!field.isEditable());

			column.setValueType(GridUtils.getGridValueTypeByLyraFieldType(field.getType(),
					field.getSubtype()));
			column.setLinkId(field.getLinkId());

			String val;

			int colWidthDefValue;
			val = gridProps.getStringValue(DEF_COL_WIDTH);
			if (val != null) {
				colWidthDefValue = TextUtils.getIntSizeValue(val);
			} else {
				colWidthDefValue = COLUMN_WIDTH_DEF_VALUE;
			}

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

			if (field.getWidth() < 0) {
				column.setWidth(colWidthDefValue);
			} else {
				column.setWidth(field.getWidth());
			}

			column.setFormat(null);
			column.setParentId(null);
			column.setEditor(null);

			result.getColumns().add(column);
		}
	}

	private void setupPluginSettings() {
		String plugin = ((PluginInfo) elInfo).getPlugin();

		result.getJSInfo().setCreateProc("create" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRefreshProc("refresh" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setAddRecordProc("addRecord" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setSaveProc("save" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setRevertProc("revert" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setClipboardProc("clipboard" + TextUtils.capitalizeWord(plugin));
		result.getJSInfo().setPartialUpdate("partialUpdate" + TextUtils.capitalizeWord(plugin));

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
		return String
				.format("%s/%s", PluginFactory.PLUGINS_DIR, ((PluginInfo) elInfo).getPlugin());
	}

	private void setupUnused() {
		result.setVirtualColumns(null);

		result.setAutoSelectRecordId(null);
		// result.setAutoSelectRecordId("63028000006005400");
		// похоже не влияет
		// result.getEventManager().setFireGeneralAndConcreteEvents(true);
		result.setAutoSelectColumnId(null);

	}

}