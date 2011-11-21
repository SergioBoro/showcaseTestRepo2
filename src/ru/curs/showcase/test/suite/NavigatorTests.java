package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Все тесты для навигатора.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ NavigatorFactoryTest.class, NavigatorGatewayTest.class, NavigatorSLTest.class })
public class NavigatorTests {

}
