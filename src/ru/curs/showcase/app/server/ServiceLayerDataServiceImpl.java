package ru.curs.showcase.app.server;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

import org.slf4j.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.chart.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.event.*;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.model.geomap.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

import com.google.gson.*;

/**
 * Реализация функций сервисного слоя приложения не зависимая от GWT Servlet.
 * Позволяет вызывать функции сервисного слоя не из GWT кода. Нельзя создавать
 * экземпляр данного класса, не указав при этом aSessionId.
 * 
 * @author den
 * 
 */
public final class ServiceLayerDataServiceImpl implements DataService, DataServiceExt {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceLayerDataServiceImpl.class);

	public static final String HEADER_SOURCE = "header.source";

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId;

	public ServiceLayerDataServiceImpl(final String aSessionId) {
		super();
		sessionId = aSessionId;
	}

	@Override
	public Navigator getNavigator(final CompositeContext context) throws GeneralException {
		InputStream xml;
		Navigator nav = null;

		try {
			NavigatorGateway gw = new NavigatorDBGateway();
			prepareContext(context);
			try {
				xml = gw.getRawData(context);
				NavigatorFactory factory = new NavigatorFactory();
				nav = factory.fromStream(xml);
			} finally {
				gw.releaseResources();
			}
			outputDebugInfo(nav);
			return nav;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralException {
		DataPanel panel = null;

		try {
			prepareContext(action);
			DataPanelGateway gateway = new DataPanelXMLGateway();
			DataFile<InputStream> file =
				gateway.getRawData(action.getDataPanelLink().getDataPanelId());
			DataPanelFactory factory = new DataPanelFactory();
			panel = factory.fromStream(file);
			outputDebugInfo(panel);
			return panel;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		try {
			WebTextGateway wtgateway = new WebTextDBGateway();
			prepareContext(context);
			HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
			WebTextFactory builder = new WebTextFactory(rawWT);
			WebText webtext = builder.build();
			outputDebugInfo(webtext);
			return webtext;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public Grid getGrid(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final GridRequestedSettings aSettings) throws GeneralException {
		try {
			GridDBGateway gateway = new GridDBGateway();
			GridDBFactory factory = null;
			ElementRawData raw = null;
			Grid grid = null;
			ElementSettingsDBGateway sgateway = null;
			GridRequestedSettings settings = aSettings;
			GridServerState state = null;

			if (settings == null) {
				settings = GridRequestedSettings.createFirstLoadDefault();
			}
			prepareContext(context);

			if (settings.isFirstLoad()) {
				state = new GridServerState();
				AppInfoSingleton.getAppInfo().storeElementState(sessionId, elementInfo, context,
						state);
			} else {
				state =
					(GridServerState) AppInfoSingleton.getAppInfo().getElementState(sessionId,
							elementInfo, context);
			}

			if (elementInfo.loadByOneProc()) {
				raw = gateway.getRawDataAndSettings(context, elementInfo, settings);
				factory = new GridDBFactory(raw, settings, state);
				grid = factory.build();
			} else {
				if (settings.isFirstLoad()) {
					sgateway = new ElementSettingsDBGateway();
					raw = sgateway.getRawData(context, elementInfo);
					factory = new GridDBFactory(raw, settings, state);
					factory.buildStepOne();
					settings = factory.getRequestSettings();
					gateway.setConn(sgateway.getConn());
				} else {
					factory = new GridDBFactory(settings, state);
					factory.buildStepOneFast();
				}
				raw = gateway.getRawData(context, elementInfo, settings);
				factory.setSource(raw);
				grid = factory.buildStepTwo();
			}

			outputDebugInfo(grid);
			return grid;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		try {
			ChartGateway gateway = new ChartDBGateway();
			prepareContext(context);
			ElementRawData raw = gateway.getRawData(context, element);
			ChartDBFactory factory = new ChartDBFactory(raw);
			Chart chart = factory.build();
			outputDebugInfo(chart);
			AdapterForJS adapter = new AdapterForJS();
			adapter.adapt(chart);
			return chart;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public ExcelFile generateExcelFromGrid(final GridToExcelExportType exportType,
			final CompositeContext context, final DataPanelElementInfo element,
			final GridRequestedSettings settings, final ColumnSet cs) throws GeneralException {
		ByteArrayOutputStream result = null;
		Grid grid = null;
		try {
			if (exportType == GridToExcelExportType.ALL) {
				settings.resetForReturnAllRecords();
			}
			settings.setApplyLocalFormatting(false);
			prepareContext(context);
			grid = getGrid(context, element, settings);
			GridXMLBuilder builder = new GridXMLBuilder(grid);
			Document xml = builder.build(cs);
			result = XMLUtils.xsltTransformForGrid(xml);
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
		return new ExcelFile(result);
	}

	@Override
	public void saveColumnSet(final ColumnSet aCs) throws GeneralException {
		// fake метод для корректной сериализации
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		try {
			GeoMapGateway gateway = new GeoMapDBGateway();
			prepareContext(context);
			ElementRawData raw = gateway.getRawData(context, element);
			GeoMapDBFactory factory = new GeoMapDBFactory(raw);
			GeoMap map = factory.build();
			outputDebugInfo(map);
			AdapterForJS adapter = new AdapterForJS();
			adapter.adapt(map);
			return map;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public XForms getXForms(final CompositeContext context, final DataPanelElementInfo element,
			final String currentData) throws GeneralException {
		try {
			XFormsGateway gateway = new XFormsDBGateway();
			prepareContext(context);
			HTMLBasedElementRawData raw = gateway.getRawData(context, element);
			if (currentData != null) {
				raw.setData(currentData);
			}
			XFormsFactory factory = new XFormsFactory(raw);
			XForms xforms = factory.build();
			outputDebugInfo(xforms);
			return xforms;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public void saveXForms(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) throws GeneralException {
		try {
			prepareContext(context);
			LOGGER.info("Идет сохранение данных XForms: " + data);

			UserXMLTransformer transformer =
				new UserXMLTransformer(data, elementInfo.getSaveProc());
			transformer.checkAndTransform();
			XFormsGateway gateway = new XFormsDBGateway();
			gateway.saveData(context, elementInfo, transformer.getStringResult());
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	private void prepareContext(final CompositeContext context)
			throws UnsupportedEncodingException {
		if (context.getSession() != null) {
			return;
		}
		String sessionContext =
			SessionContextGenerator.generate(sessionId, context.getSessionParamsMap());

		LOGGER.info("Session context: " + sessionContext);
		context.setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		context.setSessionParamsMap(null);
	}

	private void prepareContext(final Action action) throws UnsupportedEncodingException {
		if (action.getContext() == null) {
			return;
		}
		if (action.getContext().getSession() != null) {
			return;
		}

		CompositeContext context = action.getContext();
		String sessionContext =
			SessionContextGenerator.generate(sessionId, context.getSessionParamsMap());
		LOGGER.info("Session context: " + sessionContext);
		action.setSessionContext(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		action.setSessionContext((Map<String, List<String>>) null);
	}

	@Override
	public String handleSQLSubmission(final String procName, final String content,
			final String userDataId) throws GeneralException {
		try {
			setUserData(userDataId);
			XFormsGateway gateway = new XFormsDBGateway();
			String decodedContent = XMLUtils.xmlServiceSymbolsToNormal(content);

			String res = gateway.handleSubmission(procName, decodedContent);

			LOGGER.info(String.format(
					"Submission '%s' c данными %s успешно выполнен c результатом: %s", procName,
					decodedContent, res));
			return res;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	private void setUserData(final String userDataId) {
		AppInfoSingleton.getAppInfo().setCurUserDataId(
				(userDataId == null) ? ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT : userDataId);
	}

	@Override
	public String handleXSLTSubmission(final String xsltFile, final String content,
			final String userDataId) throws GeneralException {
		try {
			setUserData(userDataId);
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));
			Document doc = XMLUtils.createBuilder().parse(is);
			String res = XMLUtils.xsltTransform(doc, xsltFile);
			LOGGER.info("XFormsTransformationServlet успешно выполнен: " + res);
			return res;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public ServerCurrentState getServerCurrentState(final CompositeContext context)
			throws GeneralException {
		try {
			prepareContext(context);
			ServerCurrentState res = ServerCurrentStateBuilder.build(sessionId);
			LOGGER.info(res.toString());
			return res;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public DataFile<ByteArrayOutputStream> getDownloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data)
			throws GeneralException {
		try {
			LOGGER.info("Данные формы при выгрузке файла:" + data);
			prepareContext(context);

			XFormsGateway gateway = new XFormsDBGateway();
			DataFile<ByteArrayOutputStream> file =
				gateway.downloadFile(context, elementInfo, linkId, data);

			UserXMLTransformer transformer =
				new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
			transformer.checkAndTransform();
			file = transformer.getOutputStreamResult();

			LOGGER.info(String.format("Размер скачиваемого файла: %d байт", file.getData().size()));
			return file;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public void uploadFile(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final String data, final DataFile<ByteArrayOutputStream> file)
			throws GeneralException {
		try {
			LOGGER.info("Данные формы при загрузке файла:" + data);
			LOGGER.info("Получен файл '" + file.getName() + "' размером " + file.getData().size()
					+ " байт");

			prepareContext(context);
			UserXMLTransformer transformer =
				new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
			transformer.checkAndTransform();
			XFormsGateway gateway = new XFormsDBGateway();
			gateway.uploadFile(context, elementInfo, linkId, data,
					transformer.getInputStreamResult());
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public void execServerAction(final Action action) throws GeneralException {
		try {
			prepareContext(action);
			ActivityGateway gateway = new ActivityDBGateway();

			for (Activity act : action.getServerActivities()) {
				gateway.exec(act);
				LOGGER.info("Выполнено действие на сервере: " + act.toString());
			}
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public MainPage getMainPage(final CompositeContext context) throws GeneralException {
		try {
			prepareContext(context);
			MainPage mp = new MainPage();
			String value = AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP);
			if (value != null) {
				mp.setHeaderHeight(value);
			} else {
				mp.setHeaderHeight(AppProps.DEF_HEADER_HEIGTH);
			}
			value = AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP);
			if (value != null) {
				mp.setFooterHeight(value);
			} else {
				mp.setFooterHeight(AppProps.DEF_FOOTER_HEIGTH);
			}
			MainPageFrameFactory factory = new MainPageFrameFactory(false);

			String html = getRawMainPageFrame(context, MainPageFrameType.HEADER);
			html = factory.build(html);
			mp.setHeader(html);
			html = getRawMainPageFrame(context, MainPageFrameType.FOOTER);
			html = factory.build(html);
			mp.setFooter(html);
			html = getRawMainPageFrame(context, MainPageFrameType.WELCOME);
			html = factory.build(html);
			mp.setWelcome(html);

			outputDebugInfo(mp);
			return mp;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	@Override
	public String getMainPageFrame(final CompositeContext context, final MainPageFrameType type)
			throws GeneralException {
		try {
			prepareContext(context);
			String result = getRawMainPageFrame(context, type);
			MainPageFrameFactory factory = new MainPageFrameFactory(true);
			result = factory.build(result);
			LOGGER.info(String.format("Возвращен фрейм типа %s c кодом: %s", type.toString(),
					result));
			return result;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	private String
			getRawMainPageFrame(final CompositeContext context, final MainPageFrameType type) {
		MainPageFrameSelector selector = new MainPageFrameSelector(type);
		String result = selector.getGateway().getRawData(context, selector.getSourceName());
		return result;
	}

	private void outputDebugInfo(final Object obj) {
		if (LOGGER.isInfoEnabled()) {
			ExclusionStrategy es = new ExclusionStrategy() {
				@Override
				public boolean shouldSkipClass(final Class<?> aClass) {
					return false;
				}

				@Override
				public boolean shouldSkipField(final FieldAttributes fa) {
					return fa.getAnnotation(ExcludeFromSerialization.class) != null;
				}
			};
			Gson gson =
				new GsonBuilder().setPrettyPrinting().setExclusionStrategies(es).serializeNulls()
						.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC).create();
			LOGGER.info(gson.toJson(obj));
		}
	}
}
