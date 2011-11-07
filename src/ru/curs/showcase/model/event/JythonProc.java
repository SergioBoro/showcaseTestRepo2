package ru.curs.showcase.model.event;

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
	void execute(AbstractCompositeContext context);
}
