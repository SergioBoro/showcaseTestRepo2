package ru.curs.showcase.security.logging;

import java.util.*;
import java.util.Map.Entry;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Событие.
 * 
 * @author bogatov
 * 
 */
public class Event {
	public static final String DATA_TAG = "data";

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
		LOGOUT,
		/**
		 * Session time out.
		 * 
		 */
		SESSSIONTIMEOUT
	}

	private final TypeEvent typeEvent;
	private CompositeContext context;
	private final Map<String, String> mapData = new HashMap<String, String>();

	public Event(final TypeEvent eTypeEvent) {
		super();
		this.typeEvent = eTypeEvent;
	}

	public Event(final TypeEvent eTypeEvent, final CompositeContext oContext) {
		super();
		this.typeEvent = eTypeEvent;
		this.context = oContext;
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
	 * Добавление атрибута.
	 * 
	 * @return sessionid
	 */
	public void add(final String name, final String value) {
		mapData.put(name, value);
	}

	/**
	 * Возвращает все параметры в виде xml строки.
	 * 
	 * @return string xml
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(DATA_TAG).append(">");
		for (Entry<String, String> entry : mapData.entrySet()) {
			sb.append("<").append(entry.getKey()).append(">");
			sb.append(entry.getValue() != null ? entry.getValue() : "");
			sb.append("</").append(entry.getKey()).append(">");
		}
		sb.append("</").append(DATA_TAG).append(">");
		return sb.toString();
	}

}
