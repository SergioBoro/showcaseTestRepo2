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
		LOGIN, LOGOUT
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

	public TypeEvent getTypeEvent() {
		return typeEvent;
	}

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext oContext) {
		this.context = oContext;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(final String sSessionid) {
		this.sessionid = sSessionid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(final String sIp) {
		this.ip = sIp;
	}

}
