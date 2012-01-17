package ru.curs.showcase.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import ru.curs.showcase.test.*;

/**
 * Тесты модулей времени выполнения.
 * 
 * @author den
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ AppPropsTest.class, RuntimeTest.class, PoolsTest.class })
public class RunTimeTests {

}
