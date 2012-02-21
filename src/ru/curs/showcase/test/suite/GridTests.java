package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.grid.*;

/**
 * Все тесты для грида.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		GridTest.class, GridGatewayTest.class, GridFactoryTest.class, GridSLTest.class,
		GridExportToExcelSLTest.class })
public class GridTests {

}
