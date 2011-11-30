package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Все тесты модели.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		UtilTests.class, EventAndActionTests.class, RunTimeTests.class, DataPanelTests.class,
		ActionTabFinderTests.class, NavigatorTests.class, MainPageTests.class,
		HTMLVariablesTests.class, GridTests.class, ChartTests.class, GeoMapTests.class,
		WebTextTests.class, XFormTests.class })
public class ModelTests {

}
