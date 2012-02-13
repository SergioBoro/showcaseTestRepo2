package ru.curs.showcase.app.server;

import java.lang.management.ManagementFactory;

import javax.management.*;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Регистрация локальных JMX bean.
 * 
 * @author den
 * 
 */
public final class JMXBeanRegistrator {

	private static final String REGISTER_ERROR = "Ошибка при регистрации MBean Showcase ";

	private static final Logger LOGGER = LoggerFactory.getLogger(JMXBeanRegistrator.class);

	/**
	 * Сервер JMX Bean.
	 */
	private static MBeanServer mbs = null;

	private static MBeanServer getMBeanServer() {
		if (mbs == null) {
			mbs = ManagementFactory.getPlatformMBeanServer();
		}
		return mbs;
	}

	/**
	 * Функция регистрации JMX bean.
	 */
	public static void register() {
		registerEncacheMBean();
		registerShowcaseMBean();
	}

	/**
	 * Для модульных тестов нужна проверка на то, что bean уже зарегистрирован.
	 * Если это так, то повторно не регистрируем - для простоты.
	 */
	private static void registerEncacheMBean() {
		CacheManager manager = AppInfoSingleton.getAppInfo().getCacheManager();
		try {
			if (getMBeanServer().isRegistered(getEhcasheMBeanName(manager))) {
				return;
			}
		} catch (MalformedObjectNameException e) {
			LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
		}
		ManagementService.registerMBeans(manager, getMBeanServer(), true, false, false, true);
	}

	private static void registerShowcaseMBean() {
		JMXMonitorBean monBean = new JMXMonitorBeanImpl();
		ObjectName beanName = null;
		try {
			beanName = getShowcaseMBeanName();
			if (getMBeanServer().isRegistered(beanName)) {
				getMBeanServer().unregisterMBean(beanName);
			}
			getMBeanServer().registerMBean(monBean, beanName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | InstanceNotFoundException
				| MalformedObjectNameException e) {
			LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
		}
	}

	private static ObjectName getShowcaseMBeanName() throws MalformedObjectNameException {
		return new ObjectName("Showcase:name=Showcase.Monitor");
	}

	private static ObjectName getEhcasheMBeanName(final CacheManager manager)
			throws MalformedObjectNameException {
		return new ObjectName("net.sf.ehcache:type=CacheManager,name=" + manager.toString());
	}

	public static void unRegister() {
		try {
			getMBeanServer().unregisterMBean(getShowcaseMBeanName());
		} catch (InstanceNotFoundException | MBeanRegistrationException
				| MalformedObjectNameException e) {
			LOGGER.error(REGISTER_ERROR + e.getLocalizedMessage());
		}
	}

	private JMXBeanRegistrator() {
		throw new UnsupportedOperationException();
	}
}
