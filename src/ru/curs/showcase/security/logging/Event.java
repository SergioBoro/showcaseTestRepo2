package ru.curs.showcase.security.logging;

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

	private TypeEvent typeEvent;
	private String sessionid;
	private String username;
	private String ip;

	public Event(final TypeEvent eTypeEvent) {
		super();
		this.typeEvent = eTypeEvent;
	}
	
	public Event(final TypeEvent eTypeEvent, final String sSessionid, final String sUsername, final String sIp) {
		super();
		this.typeEvent = eTypeEvent;
		this.username = sUsername;
		this.ip = sIp;
		this.sessionid = sSessionid;
	}

	public TypeEvent getTypeEvent() {
		return typeEvent;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(final String sSessionid) {
		this.sessionid = sSessionid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String sUsername) {
		this.username = sUsername;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(final String sIp) {
		this.ip = sIp;
	}
		
}
