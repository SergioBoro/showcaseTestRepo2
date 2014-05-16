package ru.curs.showcase.security.logging;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Событие.
 * 
 * @author bogatov
 * 
 */
public class Event {
	/**
	 * Тип события.
	 * 
	 */
	public enum TypeEvent {
		/**
		 * LOGIN.
		 * 
		 */
		LOGIN,
		/**
		 * LOGOUT.
		 * 
		 */
		LOGOUT
	}

	private final TypeEvent typeEvent;
	private CompositeContext context;
	private String sessionid;
	private String ip;

	public Event(final TypeEvent eTypeEvent) {
		super();
		this.typeEvent = eTypeEvent;
	}

	public Event(final TypeEvent eTypeEvent, final CompositeContext oContext,
			final String sSessionid, final String sIp) {
		super();
		this.typeEvent = eTypeEvent;
		this.context = oContext;
		this.ip = sIp;
		this.sessionid = sSessionid;
	}

	/**
	 * Функция-getter для переменной typeEvent.
	 * 
	 * @return typeEvent
	 */
	public TypeEvent getTypeEvent() {
		return typeEvent;
	}

	/**
	 * Функция-getter для переменной context.
	 * 
	 * @return context
	 */
	public CompositeContext getContext() {
		return context;
	}

	/**
	 * Функция-setter для переменной context.
	 * 
	 * @param oContext
	 *            - переменная типа CompositeContext
	 */
	public void setContext(final CompositeContext oContext) {
		this.context = oContext;
	}

	/**
	 * Функция-getter для переменной sessionid.
	 * 
	 * @return sessionid
	 */
	public String getSessionid() {
		return sessionid;
	}

	/**
	 * Функция-setter для переменной sessionid.
	 * 
	 * @param sSessionid
	 *            - строковая переменная
	 */
	public void setSessionid(final String sSessionid) {
		this.sessionid = sSessionid;
	}

	/**
	 * Функция-getter для переменной ip.
	 * 
	 * @return ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Функция-setter для переменной ip.
	 * 
	 * @param sIp
	 *            - строковая переменная
	 */
	public void setIp(final String sIp) {
		this.ip = sIp;
	}

}
