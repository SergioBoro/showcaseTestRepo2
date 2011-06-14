package ru.curs.showcase.app.server;

import java.lang.management.ManagementFactory;

import javax.management.*;

/**
 * Регистрация локальных JMX bean.
 * 
 * @author den
 * 
 */
public final class JMXAgent {

	/**
	 * Сервер JMX Bean.
	 */
	private static MBeanServer mbs = null;

	/**
	 * Функция регистрации JMX bean.
	 */
	public static void register() {
		// Получить экземпляр MBeanServer
		mbs = ManagementFactory.getPlatformMBeanServer();

		// Создаем наш MBean
		JMXMonitorBean monBean = new JMXMonitorBeanImpl();
		ObjectName beanName = null;

		try {
			// И регистрируем его на платформе MBeanServer
			beanName = new ObjectName("Showcase:name=Showcase.Monitor");
			mbs.registerMBean(monBean, beanName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JMXAgent() {
		throw new UnsupportedOperationException();
	}
}
