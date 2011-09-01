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

	private final UUID requestId = UUID.randomUUID();

	public ServiceLayerDataServiceImpl(final String aSessionId) {
		super();
		sessionId = aSessionId;
	}

	@Override
	public Navigator getNavigator(final CompositeContext context) throws GeneralException {
		InputStream xml;
		Navigator nav = null;

		try {
			prepareContext(context);
			NavigatorSelector selector = new NavigatorSelector();
			NavigatorGateway gw = selector.getGateway();
			try {
				xml = gw.getRawData(context);
				NavigatorFactory factory = new NavigatorFactory(context);
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

			DataPanelSelector selector = new DataPanelSelector(action.getDataPanelLink());
			DataPanelGateway gateway = selector.getGateway();
			try {
				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				panel = factory.fromStream(file);
			} finally {
				gateway.releaseResources();
			}
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
	public Grid getGrid(final GridContext context, final DataPanelElementInfo elementInfo)
			throws GeneralException {
		return getGridExt(context, elementInfo, true);
	}

	private Grid getGridExt(final GridContext context, final DataPanelElementInfo elementInfo,
			final Boolean applyLocalFormatting) throws GeneralException {
		try {
			GridDBGateway gateway = new GridDBGateway();
			GridDBFactory factory = null;
			ElementRawData raw = null;
			Grid grid = null;
			ElementSettingsDBGateway sgateway = null;
			GridServerState state = null;

			prepareContext(context);

			state = getGridState(context, elementInfo);

			if (elementInfo.loadByOneProc()) {
				raw = gateway.getRawDataAndSettings(context, elementInfo);
				factory = new GridDBFactory(raw, state);
				factory.setApplyLocalFormatting(applyLocalFormatting);
				grid = factory.build();
			} else {
				if (context.isFirstLoad()) {
					sgateway = new ElementSettingsDBGateway();
					raw = sgateway.getRawData(context, elementInfo);
					factory = new GridDBFactory(raw, state);
					factory.buildStepOne();
					gateway.setConn(sgateway.getConn());
				} else {
					factory = new GridDBFactory(context, state);
					factory.buildStepOneFast();
				}
				raw = gateway.getRawData(context, elementInfo);
				factory.setSource(raw);
				factory.setApplyLocalFormatting(applyLocalFormatting);
				grid = factory.buildStepTwo();
			}

			outputDebugInfo(grid);
			return grid;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	private GridServerState getGridState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state;
		if (context.isFirstLoad()) {
			state = saveGridServerState(context, elementInfo);
		} else {
			state =
				(GridServerState) AppInfoSingleton.getAppInfo().getElementState(sessionId,
						elementInfo, context);
			if (state == null) {
				// состояние по каким-либо причинам не сохранено
				context.setIsFirstLoad(false);
				state = saveGridServerState(context, elementInfo);
			}
		}
		return state;
	}

	protected GridServerState saveGridServerState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(sessionId, elementInfo, context, state);
		return state;
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
			final GridContext context, final DataPanelElementInfo element, final ColumnSet cs)
			throws GeneralException {
		ByteArrayOutputStream result = null;
		Grid grid = null;
		try {
			if (exportType == GridToExcelExportType.ALL) {
				context.resetForReturnAllRecords();
			}

			prepareContext(context);
			grid = getGridExt(context, element, false);
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
	public XForms getXForms(final XFormsContext context, final DataPanelElementInfo element)
			throws GeneralException {
		try {
			XFormsGateway gateway = new XFormsDBGateway();
			prepareContext(context);
			HTMLBasedElementRawData raw = gateway.getRawData(context, element);
			if (context.getFormData() != null) {
				raw.setData(context.getFormData());
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
	public void saveXForms(final XFormsContext context, final DataPanelElementInfo elementInfo)
			throws GeneralException {
		try {
			prepareContext(context);
			LOGGER.info("Идет сохранение данных XForms: " + context.getFormData());

			UserXMLTransformer transformer =
				new UserXMLTransformer(context.getFormData(), elementInfo.getSaveProc());
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
		String sessionContext = SessionContextGenerator.generate(sessionId, context);

		context.setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		context.getSessionParamsMap().clear();
	}

	private void prepareContext(final Action action) throws UnsupportedEncodingException {
		if (action.getContext() == null) {
			return;
		}
		if (action.getContext().getSession() != null) {
			return;
		}

		CompositeContext context = action.getContext();
		String sessionContext = SessionContextGenerator.generate(sessionId, context);
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
	public DataFile<ByteArrayOutputStream> getDownloadFile(final XFormsContext context,
			final DataPanelElementInfo elementInfo, final String linkId) throws GeneralException {
		try {
			LOGGER.info("Данные формы при выгрузке файла:" + context.getFormData());
			prepareContext(context);

			XFormsGateway gateway = new XFormsDBGateway();
			DataFile<ByteArrayOutputStream> file =
				gateway.downloadFile(context, elementInfo, linkId);

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
	public void uploadFile(final XFormsContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final DataFile<ByteArrayOutputStream> file)
			throws GeneralException {
		try {
			LOGGER.info("Данные формы при загрузке файла:" + context.getFormData());
			LOGGER.info("Получен файл '" + file.getName() + "' размером " + file.getData().size()
					+ " байт");

			prepareContext(context);
			UserXMLTransformer transformer =
				new UserXMLTransformer(file, elementInfo.getProcs().get(linkId));
			transformer.checkAndTransform();
			XFormsGateway gateway = new XFormsDBGateway();
			gateway.uploadFile(context, elementInfo, linkId, transformer.getInputStreamResult());
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
		String result = selector.getGateway().getRawData(context);
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
				new GsonBuilder().disableHtmlEscaping().setExclusionStrategies(es)
						.serializeNulls()
						.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC).create();

			LOGGER.info(String.format("SL output <br> %s <br> %s", requestId, gson.toJson(obj)));
		}
	}
}
