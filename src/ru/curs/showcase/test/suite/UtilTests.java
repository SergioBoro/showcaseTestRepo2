package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.util.*;

/**
 * Тесты для utils.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		BaseObjectsTest.class, DBConnectionsTest.class, TextUtilsTest.class, XMLUtilsTest.class,
		ExceptionsTest.class, SVGUtilsTest.class, WSTest.class })
public class UtilTests {

}
