package ru.curs.showcase.app.server;

import java.util.*;

import javax.management.*;

/**
 * Реализация интерфейса для мониторинга состояния Showcase.
 * 
 * @author den
 * 
 */
public class JMXMonitorBeanImpl implements JMXMonitorBean, DynamicMBean {

	public JMXMonitorBeanImpl() {
		super();
	}

	@Override
	public void setDebugLevel(final String aLevel) {
		System.out.println("Заглушка для JMXMonitorBeanImpl.setDebugLevel");

	}

	@Override
	public String getSessionCount() {
		return String.valueOf(AppInfoSingleton.getAppInfo().getSessionInfoMap().size());
	}

	@Override
	public void clearSessions() {
		AppInfoSingleton.getAppInfo().clearSessions();
	}

	@Override
	public Object getAttribute(final String aArg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		if (aArg0.equals("SessionCount")) {
			return getSessionCount();
		}
		return null;
	}

	@Override
	public AttributeList getAttributes(final String[] aArg0) {
		AttributeList list = new AttributeList();
		list.add(new Attribute("SessionCount", getSessionCount()));
		return list;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		SortedSet<String> names = new TreeSet<String>();
		names.add("SessionCount");
		MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size()];
		Iterator<String> it = names.iterator();
		for (int i = 0; i < attrs.length; i++) {
			String name = it.next();
			attrs[i] = new MBeanAttributeInfo(name, "java.lang.String", "Property " + name, true, // isReadable
					false, // isWritable
					false); // isIs
		}
		MBeanOperationInfo[] opers =
			{ new MBeanOperationInfo("clearSessions", "Очистка списка пользовательских сессий",
					null, // no
					// parameters
					"void", MBeanOperationInfo.ACTION) };
		return new MBeanInfo(this.getClass().getName(), "Showcase monitor bean", attrs, null, // constructors
				opers, null); // notifications
	}

	@Override
	public Object invoke(final String name, final Object[] args, final String[] sig)
			throws MBeanException, ReflectionException {
		if (name.equals("clearSessions") && (args == null || args.length == 0)
				&& (sig == null || sig.length == 0)) {
			clearSessions();
			return null;
		}
		return null;
	}

	@Override
	public void setAttribute(final Attribute aArg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public AttributeList setAttributes(final AttributeList aArg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
