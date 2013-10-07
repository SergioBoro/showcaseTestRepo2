package ru.curs.showcase.core.primelements.navigator;

import java.io.InputStream;

import org.python.core.PyObject;
import org.slf4j.*;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.server.AppAndSessionEventsListener;
import ru.curs.showcase.core.primelements.PrimElementsGateway;
import ru.curs.showcase.runtime.SessionUtils;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз навегатора, источник данных для которого является Celesta.
 * 
 * @author bogatov
 * 
 */
public class NavigatorCelestaGataway implements PrimElementsGateway {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);
	private String procName;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext) {
		String userSID = SessionUtils.getCurrentUserSID();
		String json = XMLUtils.convertXmlToJson(aContext.getSession());
		try {
			PyObject pyObject = Celesta.getInstance().runPython(userSID,
					procName, new Object[] { json });
			if (pyObject != null) {
				InputStream stream = TextUtils.stringToStream(pyObject
						.asString());

				return new DataFile<InputStream>(stream, procName);
			}
		} catch (CelestaException ex) {
			LOGGER.error("Error run celesta python script", ex);
		}
		return null;
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final String aSourceName) {
		setProcName(aSourceName);
		return getRawData(aContext);
	}

	@Override
	public void setSourceName(final String aSourceName) {
		setProcName(aSourceName);
	}

	@Override
	public void close() {
	}

	private void setProcName(final String sProcName) {
		if (sProcName != null) {
			this.procName = sProcName.substring(0, sProcName.lastIndexOf("."));
		} else {
			this.procName = sProcName;
		}
	}
}
