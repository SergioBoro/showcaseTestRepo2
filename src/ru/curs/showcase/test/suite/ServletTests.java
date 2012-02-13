package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.servlets.*;

/**
 * Сборка тестов сервлетов.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
		ControlMemoryServletTest.class, StateServletTest.class,
		XFormScriptTransformServletTest.class, XFormXSLTransformServletTest.class })
public class ServletTests {

}
