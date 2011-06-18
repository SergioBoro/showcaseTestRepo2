package ru.curs.showcase.app.server;

import java.lang.management.ManagementFactory;

import javax.management.*;

/**
 * Регистрация локальных JMX bean.
 * 
 * @author den
 * 
 */
public final class JMXMBeanRegistrator {

	/**
	 * Сервер JMX Bean.
	 */
	private static MBeanServer mbs = null;

	/**
	 * Функция регистрации JMX bean.
	 */
	public static void register() {
		mbs = ManagementFactory.getPlatformMBeanServer();

		JMXMonitorBean monBean = new JMXMonitorBeanImpl();
		ObjectName beanName = null;
		try {
			beanName = new ObjectName("Showcase:name=Showcase.Monitor");
			if (mbs.isRegistered(beanName)) {
				mbs.unregisterMBean(beanName);
			}
			mbs.registerMBean(monBean, beanName);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO заменить
		}
	}

	private JMXMBeanRegistrator() {
		throw new UnsupportedOperationException();
	}
}
