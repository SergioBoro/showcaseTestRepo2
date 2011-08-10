package ru.curs.showcase.test;

import static org.junit.Assert.assertNotNull;

import java.util.*;

import org.junit.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextSLTest extends AbstractTest {

	/**
	 * Проверка выполнения действия на сервере.
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testServerActivityExec() throws GeneralException {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		sl.execServerAction(action);
		assertNotNull(action.getServerActivities().get(0).getContext().getSession());
	}

	/**
	 * Проверка выполнения действия на сервере, приводящего к ошибке.
	 * 
	 * @throws GeneralException
	 */
	@Test(expected = GeneralException.class)
	public void testServerActivityExecFail() throws GeneralException {
		final int actionNumber = 2;
		AppInfoSingleton.getAppInfo().setCurUserDataId("test1");
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		Map<String, ArrayList<String>> params = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> val = new ArrayList<String>();
		val.add("test1");
		params.put(ExchangeConstants.URL_PARAM_USERDATA, val);
		action.getContext().setSessionParamsMap(params);
		ServiceLayerDataServiceImpl sl = new ServiceLayerDataServiceImpl(TEST_SESSION);
		sl.execServerAction(action);
	}

}
