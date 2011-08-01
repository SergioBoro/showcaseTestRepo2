package ru.curs.showcase.model.grid;

import ru.curs.gwt.datagrid.model.DataGridSettings;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Стиль по умолчанию.
 * 
 * @author den
 * 
 */
public final class DefaultGridUIStyle implements GridUIStyle {
	private static final String DEF_VISIBLE_PAGES_COUNT = "def.visible.pages.count";
	public static final String DEF_SELECT_WHOLE_RECORD = "def.select.whole.record";

	private static final String DEF_PAGES_BLOCK_DUPLICATE_LIMIT =
		"def.pages.block.duplicate.limit";
	private static final String DEF_VISIBLE_COLUMNS_CUSTOMIZER = "def.visible.columns.customizer";
	private static final String DEF_VISIBLE_COLUMNGROUPS_CUSTOMIZER =
		"def.visible.columngroups.customizer";
	private static final String DEF_VISIBLE_PAGER = "def.visible.pager";
	private static final String DEF_VISIBLE_EXPORTTOEXCEL_CURRENTPAGE =
		"def.visible.exporttoexcel.currentpage";
	private static final String DEF_VISIBLE_EXPORTTOEXCEL_ALL = "def.visible.exporttoexcel.all";
	private static final String DEF_VISIBLE_COPYTOCLIPBOARD = "def.visible.copytoclipboard";
	private static final String DEF_VISIBLE_RECORDS_SELECTOR = "def.visible.records.selector";
	private static final String DEF_VISIBLE_COLUMNS_HEADER = "def.visible.columns.header";
	private static final String SINGLE_CLICK_BEFORE_DOUBLE = "single.click.before.double";

	/**
	 * Настройки грида.
	 */
	private DataGridSettings settings;

	@Override
	public void apply(final GridProps gp, final DataGridSettings aSettings) {
		settings = aSettings;
		applyByDefault();
		applyFromPropertiesFile(gp);
	}

	private void applyByDefault() {
		settings.setHorizontalScrollable(true);

		settings.setRightClickEnabled(false);
		settings.setSingleClickBeforeDoubleClick(false);

		settings.setColumnGapHtml("");
		settings.setColumnGapWidth(AppInfoSingleton.getAppInfo().getGridColumnGapWidth());

		settings.setSelectOnDoubleClick(true);

		settings.setSelectRecordOnClick(true);
		settings.setUnselectRecordOnClick(false);
		settings.setUnselectCellOnClick(false);
	}

	private void applyFromPropertiesFile(final GridProps gp) {
		Integer intValue;
		intValue = gp.stdReadIntGridValue(DEF_PAGES_BLOCK_DUPLICATE_LIMIT);
		if (intValue != null) {
			settings.setPagerDuplicateRecords(intValue);
		}
		intValue = gp.stdReadIntGridValue(DEF_VISIBLE_PAGES_COUNT);
		if (intValue != null) {
			settings.setPagesButtonCount(intValue);
		}
		Boolean boolValue = gp.stdReadBoolGridValue(DEF_SELECT_WHOLE_RECORD);
		if (boolValue != null) {
			settings.setSelectOnlyRecords(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(SINGLE_CLICK_BEFORE_DOUBLE);
		if (boolValue != null) {
			settings.setSingleClickBeforeDoubleClick(boolValue);
		}
		applyVisibilitySettings(gp);
	}

	private void applyVisibilitySettings(final GridProps gp) {
		Boolean boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_COLUMNS_CUSTOMIZER);
		if (boolValue != null) {
			settings.setVisibleColumnsCustomizer(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_COLUMNGROUPS_CUSTOMIZER);
		if (boolValue != null) {
			settings.setVisibleColumnGroupsCustomizer(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_PAGER);
		if (boolValue != null) {
			settings.setVisiblePager(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_EXPORTTOEXCEL_CURRENTPAGE);
		if (boolValue != null) {
			settings.setVisibleExportToExcelCurrentPage(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_EXPORTTOEXCEL_ALL);
		if (boolValue != null) {
			settings.setVisibleExportToExcelAll(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_COPYTOCLIPBOARD);
		if (boolValue != null) {
			settings.setVisibleCopyToClipboard(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_RECORDS_SELECTOR);
		if (boolValue != null) {
			settings.setVisibleRecordsSelector(boolValue);
		}
		boolValue = gp.stdReadBoolGridValue(DEF_VISIBLE_COLUMNS_HEADER);
		if (boolValue != null) {
			settings.setVisibleColumnsHeader(boolValue);
		}
	}
}
