package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Тесты для графиков.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ChartGatewayTest.class, ChartFactoryTest.class, ChartSLTest.class })
public class ChartTests {

}
