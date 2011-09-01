package ru.curs.showcase.runtime;

import java.util.*;

/**
 * Очередь для последних событий, записанных в лог. Длина очереди фиксируется в
 * файле настроек.
 * 
 * @author den
 * 
 */
public class LastLogEvents extends TreeSet<LoggingEventDecorator> {

	public static final String INTERNAL_LOG_SIZE = "internal.log.size";
	public static final int DEF_MAX_RECORDS = 50;
	private static final long serialVersionUID = 9039619678848110139L;

	public static int getMaxRecords() {
		String res = null;
		if (AppInfoSingleton.getAppInfo().initializedUserdata()) {
			res = AppProps.getOptionalValueByName(INTERNAL_LOG_SIZE);
		}
		return res == null ? DEF_MAX_RECORDS : Integer.parseInt(res);
	}

	public LastLogEvents() {
		this(DEF_MAX_RECORDS, new Comparator<LoggingEventDecorator>() {

			@Override
			public int compare(final LoggingEventDecorator event1,
					final LoggingEventDecorator event2) {
				return (int) (event1.getOriginal().timeStamp - event2.getOriginal().timeStamp);
			}
		});

	}

	private LastLogEvents(final int aInitialCapacity,
			final Comparator<? super LoggingEventDecorator> aComparator) {
		super(aComparator);

	}

	@Override
	public boolean add(final LoggingEventDecorator event) {
		boolean res = super.add(event);
		if (size() > getMaxRecords()) {
			pollFirst();
		}
		return res;

	}

}
