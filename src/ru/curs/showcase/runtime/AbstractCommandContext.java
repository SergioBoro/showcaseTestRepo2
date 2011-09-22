package ru.curs.showcase.runtime;

/**
 * Абстрактный интерфейс для контекста команды.
 * 
 * @author den
 * 
 */
public interface AbstractCommandContext {

	String getRequestId();

	String getCommandName();

	String getUserName();

	String getUserdata();

}