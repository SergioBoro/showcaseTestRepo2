package ru.curs.showcase.core.grid;

import java.text.*;
import java.util.*;

import org.json.simple.*;

import ru.curs.celesta.CelestaException;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Фабрика для создания данных лирагридов.
 * 
 */
public class LyraGridDataFactory {

	private static final String DEF_NUM_COL_DECIMAL_SEPARATOR = "def.num.column.decimal.separator";
	private static final String DEF_NUM_COL_GROUPING_SEPARATOR =
		"def.num.column.grouping.separator";
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

	// private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final int COLUMN_DEFAULT_PRECISION = 2;
	private static final String GRID_DEFAULT_PROFILE = "default.properties";

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private final BasicGridForm basicGridForm;

	private GridData result;

	private LyraGridServerState state = null;

	private final String profile = GRID_DEFAULT_PROFILE;
	private ProfileReader gridProps = null;

	public LyraGridDataFactory(final LyraGridContext aContext, final DataPanelElementInfo aElInfo,
			final BasicGridForm aBasicGridForm) {
		context = aContext;
		elInfo = aElInfo;
		basicGridForm = aBasicGridForm;
	}

	/**
	 * Построение данных лирагрида.
	 * 
	 * @return - GridData.
	 * @throws CelestaException
	 */
	public GridData buildData() throws CelestaException {
		initResult();

		fillResultByData();

		return result;
	}

	private void initResult() {
		result = new GridData();
	}

	@SuppressWarnings("unchecked")
	private void fillResultByData() throws CelestaException {

		List<LyraFormData> records =
			basicGridForm.getRows(context.getLiveInfo().getOffset(), context.getLiveInfo()
					.getOffset() - context.getOldPosition());

		JSONArray data = new JSONArray();
		for (LyraFormData rec : records) {
			JSONObject obj = new JSONObject();
			for (LyraFieldValue lyraFieldValue : rec.getFields()) {
				obj.put(lyraFieldValue.getName(),
						getCellValue(lyraFieldValue.getValue(), lyraFieldValue.getFieldType(),
								COLUMN_DEFAULT_PRECISION));
			}
			obj.put("recversion", String.valueOf(rec.getRecversion()));
			data.add(obj);
		}
		result.setData(data.toJSONString());

		context.getLiveInfo().setTotalCount(basicGridForm.getApproxTotalCount());

	}

	private String getCellValue(final Object value, final LyraFieldType lyraFieldType,
			final Integer precision) {
		if (value == null) {
			return "";
		}
		switch (lyraFieldType) {
		case BLOB:
			return value.toString();
		case BIT:
			return value.toString();
		case DATETIME:
			return getStringValueOfDate((Date) value);
		case REAL:
			return getStringValueOfNumber((Double) value, precision);
		case INT:
			return value.toString();
		case VARCHAR:
			return value.toString();
		default:
			return value.toString();
		}
	}

	private String getStringValueOfNumber(final Double value, final Integer precision) {
		NumberFormat nf;

		nf = NumberFormat.getNumberInstance();

		if (precision != null) {
			nf.setMinimumFractionDigits(precision);
			nf.setMaximumFractionDigits(precision);
		} else {
			final int maximumFractionDigits = 20;
			nf.setMaximumFractionDigits(maximumFractionDigits);
		}

		ensureLyraGridServerState();
		String decimalSeparator = state.getDecimalSeparator();
		String groupingSeparator = state.getGroupingSeparator();
		if ((decimalSeparator != null) || (groupingSeparator != null)) {
			DecimalFormat df = (DecimalFormat) nf;
			DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
			if (decimalSeparator != null) {
				dfs.setDecimalSeparator(decimalSeparator.charAt(0));
			}
			if (groupingSeparator != null) {
				if (groupingSeparator.isEmpty()) {
					nf.setGroupingUsed(false);
				} else {
					dfs.setGroupingSeparator(groupingSeparator.charAt(0));
				}
			}
			df.setDecimalFormatSymbols(dfs);
		}

		return nf.format(value);
	}

	private String getStringValueOfDate(final java.util.Date date) {
		DateFormat df = null;

		Integer style = DateFormat.DEFAULT;
		ensureLyraGridServerState();
		String value = state.getDateValuesFormat();
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}

		df = DateFormat.getDateTimeInstance(style, style);

		return df.format(date);
	}

	private void ensureLyraGridServerState() {
		if (state != null) {
			return;
		}

		state =
			(LyraGridServerState) AppInfoSingleton.getAppInfo().getLyraGridCacheState(
					SessionUtils.getCurrentSessionId(), elInfo, context);

		if (state == null) {
			state = new LyraGridServerState();

			gridProps = new ProfileReader(profile, SettingsFileType.GRID_PROPERTIES);
			gridProps.init();

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

			AppInfoSingleton.getAppInfo().storeLyraGridCacheState(
					SessionUtils.getCurrentSessionId(), elInfo, context, state);
		}
	}

}