package ru.curs.showcase.model;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.AbstractCompositeContext;

/**
 * Интерфейс для серверной процедуры Jython. Серверная процедура - это
 * процедура, написанная на Jython, которая вызывается из серверного действия
 * типа Jython. Все серверные процедуры Jython обязаны реализовывать данный
 * интерфейс.
 * 
 * @author den
 * 
 */
public interface JythonProc {
	UserMessage execute(AbstractCompositeContext context);

	JythonDTO getRawData(AbstractCompositeContext context, String elementId);
}
