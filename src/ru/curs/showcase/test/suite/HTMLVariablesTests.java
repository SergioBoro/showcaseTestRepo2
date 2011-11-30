package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.html.*;

/**
 * Все тесты для HTML переменных.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ HTMLVariablesFactoryTest.class, HTMLVariablesSLTest.class })
public class HTMLVariablesTests {

}
