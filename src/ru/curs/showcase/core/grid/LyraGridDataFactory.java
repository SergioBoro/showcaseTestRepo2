package ru.curs.showcase.core.grid;

import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.json.simple.*;
import org.xml.sax.*;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.celesta.CelestaException;
import ru.curs.celesta.dbutils.Cursor;
import ru.curs.lyra.*;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Фабрика для создания данных лирагридов.
 * 
 */
public class LyraGridDataFactory {

	private static final String DEF_NUM_COL_DECIMAL_SEPARATOR = "def.num.column.decimal.separator";
	private static final String DEF_NUM_COL_GROUPING_SEPARATOR =
		"def.num.column.grouping.separator";
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";

	private static final String GRID_DEFAULT_PROFILE = "default.properties";
	private static final int COLUMN_DEFAULT_PRECISION = 2;

	private final LyraGridContext context;
	private final DataPanelElementInfo elInfo;
	private final BasicGridForm basicGridForm;

	private GridData result;

	private LyraGridServerState state = null;

	private String profile = null;
	private ProfileReader gridProps = null;

	@SuppressWarnings("unused")
	private static final String ID_TAG = "id";
	private static final String KEYVALUES_SEPARATOR = "_D13k82F9g7";
	private static final String EVENT_COLUMN_TAG = "column";
	private static final String CELL_PREFIX = "cell";
	private static final String ROWSTYLE = "rowstyle";
	private static final String CHECK_ACTION_ERROR =
		"Некорректное описание действия в элементе инф. панели: ";

	private final List<GridEvent> events = new ArrayList<GridEvent>();

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

		LyraGridAddInfo lyraGridAddInfo =
			((LyraGridScrollBack) basicGridForm.getChangeNotifier()).getLyraGridAddInfo();
		int lyraApproxTotalCount = basicGridForm.getApproxTotalCount();
		boolean smallStep = false;
		int position;
		int dgridDelta = context.getLiveInfo().getOffset() - context.getDgridOldPosition();

		System.out.println("ddddddddddddd1");
		System.out.println("lyraNewPosition: " + basicGridForm.getTopVisiblePosition());
		System.out.println("lyraOldPosition: " + lyraGridAddInfo.getLyraOldPosition());
		System.out.println("getApproxTotalCount: " + lyraApproxTotalCount);

		if (lyraApproxTotalCount <= LyraGridScrollBack.DGRID_MAX_TOTALCOUNT) {

			context.getLiveInfo().setTotalCount(lyraApproxTotalCount);

			position = context.getLiveInfo().getOffset();

			if (Math.abs(dgridDelta) < LyraGridScrollBack.DGRID_SMALLSTEP) {
				smallStep = true;
			}

		} else {

			context.getLiveInfo().setTotalCount(LyraGridScrollBack.DGRID_MAX_TOTALCOUNT);

			if (context.getLiveInfo().getOffset() == 0) {
				position = 0;
			} else {

				if (Math.abs(dgridDelta) < LyraGridScrollBack.DGRID_SMALLSTEP) {

					position = basicGridForm.getTopVisiblePosition() + dgridDelta;

					smallStep = true;

				} else {

					if (Math.abs(context.getLiveInfo().getOffset()
							- LyraGridScrollBack.DGRID_MAX_TOTALCOUNT) < LyraGridScrollBack.DGRID_SMALLSTEP) {

						position = lyraApproxTotalCount - context.getLiveInfo().getLimit();

						// position =
						// lyraApproxTotalCount
						// - (LyraGridScrollBack.DGRID_MAX_TOTALCOUNT - context
						// .getLiveInfo().getOffset());

					} else {
						double d = lyraApproxTotalCount;
						d = d / LyraGridScrollBack.DGRID_MAX_TOTALCOUNT;
						d = d * context.getLiveInfo().getOffset();
						position = (int) d;
					}

				}

			}

		}

		// int lyraDelta = position - lyraGridAddInfo.getLyraOldPosition();
		int lyraDelta = position - basicGridForm.getTopVisiblePosition();
		lyraGridAddInfo.setLyraOldPosition(position);
		lyraGridAddInfo.setDgridOldTotalCount(context.getLiveInfo().getTotalCount());

		System.out.println("position: " + position);
		System.out.println("lyraDelta: " + lyraDelta);

		// --------------------------------------------------------

		// if (position > 0) {
		// List<LyraFormData> records2 = basicGridForm.getRows(position,
		// lyraDelta);
		//
		// position = basicGridForm.getTopVisiblePosition() + 20;
		// lyraDelta = 20;
		// List<LyraFormData> records3 = basicGridForm.getRows(position,
		// lyraDelta);
		//
		// position = basicGridForm.getTopVisiblePosition() - 30;
		// lyraDelta = -30;
		// records3 = basicGridForm.getRows(position, lyraDelta);
		//
		// int i = records3.size();
		// }

		// --------------------------------------------------------

		List<LyraFormData> records;

		if (context.getRefreshId() == null) {

			if (smallStep && (lyraGridAddInfo.getLastKeyValues() != null) && (dgridDelta > 0)) {
				// records =
				// basicGridForm.setPosition(lyraGridAddInfo.getLastKeyValues());

				// records = basicGridForm.getRows(position, lyraDelta);
				records = basicGridForm.getRows(position);
			} else {
				// records = basicGridForm.getRows(position, lyraDelta);
				records = basicGridForm.getRows(position);
			}

		} else {

			records = basicGridForm.setPosition(getKeyValuesById(context.getRefreshId()));

		}

		JSONArray data = new JSONArray();

		int length = Math.min(records.size(), context.getLiveInfo().getLimit());
		for (int i = 0; i < length; i++) {
			LyraFormData rec = records.get(i);

			String properties = null;
			JSONObject obj = new JSONObject();
			for (LyraFieldValue lyraFieldValue : rec.getFields()) {

				int colPrecision;
				if (lyraFieldValue.getScale() == 0) {
					colPrecision = COLUMN_DEFAULT_PRECISION;
				} else {
					colPrecision = lyraFieldValue.getScale();
				}

				if ("_properties_".equalsIgnoreCase(lyraFieldValue.getName())) {
					properties = lyraFieldValue.getValue().toString();
				} else {
					obj.put(lyraFieldValue.getName(), getCellValue(lyraFieldValue, colPrecision));
				}

			}

			String recId = getIdByKeyValues(rec.getKeyValues());

			if ((properties != null) && (!properties.trim().isEmpty())) {
				// String recId = obj.get(ID_TAG).toString();

				if (recId != null) {
					String rowstyle = readEvents(recId, properties);
					if (rowstyle != null) {
						obj.put(ROWSTYLE, rowstyle);
					}
				}
			}

			obj.put("recversion", String.valueOf(rec.getRecversion()));
			obj.put("id", recId);
			data.add(obj);

			if (i == length - 1) {
				lyraGridAddInfo.setLastKeyValues(((Cursor) basicGridForm.rec())
						.getCurrentKeyValues());
			}
		}

		for (Event event : events) {
			Action action = event.getAction();
			action.actualizeBy(context);
		}

		for (Event event : events) {
			Action action = event.getAction();
			if (!action.isCorrect()) {
				throw new IncorrectElementException(CHECK_ACTION_ERROR, action);
			}
		}

		if ((data.size() > 0) && (events.size() > 0)) {
			try {
				String stringEvents =
					com.google.gwt.user.server.rpc.RPC.encodeResponseForSuccess(
							FakeService.class.getMethod("serializeEvents"), events);
				((JSONObject) data.get(0)).put("events", stringEvents);
			} catch (SerializationException | NoSuchMethodException e) {
				throw GeneralExceptionFactory.build(e);
			}
		}

		result.setData(data.toJSONString());

	}

	private Object[] getKeyValuesById(final String refreshId) {
		String[] keyValues = refreshId.split(KEYVALUES_SEPARATOR);
		return keyValues;
	}

	private String getIdByKeyValues(final Object[] keyValues) {
		String refreshId = "";
		for (int i = 0; i < keyValues.length; i++) {
			if (i > 0) {
				refreshId = refreshId + KEYVALUES_SEPARATOR;
			}
			refreshId = keyValues[i].toString();
		}
		return refreshId;
	}

	private String getCellValue(final LyraFieldValue lyraFieldValue, final Integer precision) {

		Object value = lyraFieldValue.getValue();
		if (value == null) {
			value = "";
		}

		String strValue = value.toString();

		if (strValue.trim().isEmpty() || "null".equalsIgnoreCase(strValue)) {
			return "";
		}

		switch (lyraFieldValue.getFieldType()) {
		case BLOB:
			return strValue;
		case BIT:
			return strValue;
		case DATETIME:
			return getStringValueOfDate((Date) value);
		case REAL:
			return getStringValueOfNumber((Double) value, precision);
		case INT:
			return strValue;
		case VARCHAR:
			strValue = XMLUtils.unEscapeValueXml(strValue);
			String subtype = lyraFieldValue.getSubtype();
			if (subtype != null) {
				switch (subtype.toUpperCase()) {
				case "DOWNLOAD":
					return UserDataUtils.replaceVariables(strValue);

				case "LINK":
					strValue = UserDataUtils.replaceVariables(strValue);
					strValue = normalizeLink(strValue);
					strValue = makeSafeXMLAttrValues(strValue);
					strValue = getLink(strValue);
					return strValue;

				case "IMAGE":
					return String.format("%s/%s",
							UserDataUtils.getRequiredProp(UserDataUtils.IMAGES_IN_GRID_DIR),
							strValue);

				default:
					break;
				}
			}

			return strValue;
		default:
			return strValue;
		}
	}

	private static String normalizeLink(final String aValue) {
		String value = aValue.trim();
		value = value.replace("></" + GridValueType.LINK.toString().toLowerCase() + ">", "/>");
		return value;
	}

	private static String makeSafeXMLAttrValues(final String value) {
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

	private static String getLink(final String value) {
		String result = null;

		try {
			org.w3c.dom.Element el =
				ru.curs.showcase.util.xml.XMLUtils.stringToDocument(value).getDocumentElement();

			String href = el.getAttribute("href");
			String text = el.getAttribute("text");
			if ((text == null) || text.isEmpty()) {
				text = href;
			}
			String image = el.getAttribute("image");
			String openInNewTab = el.getAttribute("openInNewTab");
			String target = null;
			if (Boolean.parseBoolean(openInNewTab)) {
				target = "_blank";
			}

			result = "<a class=\"gwt-Anchor\" href=\"" + href + "\" ";
			if (target != null) {
				result = result + "target=\"_blank\"";
			}
			result = result + ">";
			if ((image == null) || image.isEmpty()) {
				result = result + text;
			} else {
				String alt = text != null ? " alt=\"" + text + "\"" : "";
				result =
					result + "<img border=\"0\" src=\"" + XMLUtils.unEscapeTagXml(image) + "\""
							+ alt + "/>";
			}
			result = result + "</a>";

		} catch (SAXException | IOException e) {
			result = null;
		}

		return result;
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

			if (basicGridForm.getFormProperties().getProfile() == null) {
				profile = GRID_DEFAULT_PROFILE;
			} else {
				profile = basicGridForm.getFormProperties().getProfile();
			}

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

	private String readEvents(final String recId, final String data) {
		final List<String> rowstyle = new ArrayList<String>(1);
		rowstyle.add(null);
		EventFactory<GridEvent> factory = new EventFactory<GridEvent>(GridEvent.class, context);
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX, elInfo.getType()
				.getPropsSchemaName());
		SAXTagHandler recPropHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				if (aQname.equalsIgnoreCase(GeneralConstants.STYLE_CLASS_TAG)) {
					String newValue = attrs.getValue(NAME_TAG);
					if (rowstyle.get(0) == null) {
						rowstyle.set(0, newValue);
					} else {
						rowstyle.set(0, rowstyle.get(0) + " " + newValue);
					}
				}
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags =
					{ GeneralConstants.STYLE_CLASS_TAG, GeneralConstants.READONLY_TAG };
				return tags;
			}
		};
		factory.addHandler(recPropHandler);

		events.addAll(factory.getSubSetOfEvents(new ID(recId), data));

		return rowstyle.get(0);

	}

}