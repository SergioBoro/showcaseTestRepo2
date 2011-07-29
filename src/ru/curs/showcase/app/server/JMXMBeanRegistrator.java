package ru.curs.showcase.app.server;

import java.lang.management.ManagementFactory;

import javax.management.*;

import org.slf4j.*;

/**
 * Регистрация локальных JMX bean.
 * 
 * @author den
 * 
 */
public final class JMXMBeanRegistrator {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JMXMBeanRegistrator.class);

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
			LOGGER.error("Ошибка при регистрации MBean " + e.getLocalizedMessage());
		}
	}

	private JMXMBeanRegistrator() {
		throw new UnsupportedOperationException();
	}
}
