package ru.curs.showcase.util;

/**
 * Интерфейс для сериализации объектов с целью вывода информации в вебконсоль.
 * 
 * @author den
 * 
 */
public interface ObjectToLogSerializer {
	String serialize(Object obj);
}
