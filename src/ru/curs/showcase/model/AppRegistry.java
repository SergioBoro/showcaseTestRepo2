package ru.curs.showcase.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.curs.showcase.util.ObjectToLogSerializer;

/**
 * Реестр, хранящий различные глобальные объекты, настраиваемые с помощью Spring
 * IoC.
 * 
 * @author den
 * 
 */
public final class AppRegistry {

	private AppRegistry() {
		throw new UnsupportedOperationException();
	}

	/**
	 * String контекст с логикой Showcase.
	 */
	private static ApplicationContext context = new ClassPathXmlApplicationContext("logic.xml");

	/**
	 * Возвращает синглетон-реестр.
	 * 
	 * @return AppRegistry.
	 */
	public static ActionTabFinder getActionTabFinder() {
		return (ActionTabFinder) context.getBean("actionTabFinder");
	}

	public static ObjectToLogSerializer getObjectSerializer() {
		return (ObjectToLogSerializer) context.getBean("objectSerializer");
	}
}
