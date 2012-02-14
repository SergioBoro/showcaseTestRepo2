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
		BaseServletTest.class, FiltersTest.class, ControlMemoryServletTest.class,
		StateServletTest.class, XFormScriptTransformServletTest.class,
		XFormXSLTransformServletTest.class, FilesFrontControllerTest.class,
		MainPageFramesFrontControllerTest.class, ExternalServletTest.class })
public class ServletTests {

}
