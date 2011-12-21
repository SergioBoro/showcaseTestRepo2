package ru.curs.showcase.model.external;

/**
 * Шлюз для выполнения внешней команды (пришедшей посредством вызова
 * веб-сервисов или специальных сервлетов).
 * 
 * @author den
 * 
 */
public interface ExternalCommandGateway {

	String handle(final String aRequest, final String aSource);

}