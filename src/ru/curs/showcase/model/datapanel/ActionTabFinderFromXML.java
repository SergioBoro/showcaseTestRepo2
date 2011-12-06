package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.ActionTabFinder;
import ru.curs.showcase.model.primelements.PrimElementsGateway;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.*;

/**
 * Реализация интерфейса поиска вкладки для действия в инф. панели, хранимой в
 * XML.
 * 
 * @author den
 * 
 */
public class ActionTabFinderFromXML extends ActionTabFinder {

	private static final String NO_TABS_ERROR = "Панель '%s' не содержит вкладок";

	private String firstTabId = null;

	@Override
	public String getFirstTabId(final CompositeContext context, final DataPanelLink link) {
		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (firstTabId == null) {
					firstTabId = attrs.getValue(ID_TAG);
				}
			}
		};
		reset();
		DataFile<InputStream> file = getFile(context, link);

		SimpleSAX sax = new SimpleSAX(file.getData(), myHandler, link.getDataPanelId());
		sax.parse();

		if (firstTabId == null) {
			throw new XMLFormatException(String.format(NO_TABS_ERROR, link.getDataPanelId()));
		}
		return firstTabId;
	}

	/**
	 * Spring IoC засоряет мусором firstTabId.
	 */
	private void reset() {
		firstTabId = null;
	}

	@Override
	public boolean tabExists(final CompositeContext context, final DataPanelLink link,
			final String tabValue) {
		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (TAB_TAG.equalsIgnoreCase(qname)) {
					if (attrs.getValue(ID_TAG).equals(tabValue)) {
						throw new BreakSAXLoopException();
					}
				}
			}
		};
		DataFile<InputStream> file = getFile(context, link);

		SimpleSAX sax = new SimpleSAX(file.getData(), myHandler, link.getDataPanelId());
		return !sax.parse();
	}

	private DataFile<InputStream>
			getFile(final CompositeContext context, final DataPanelLink link) {
		DataPanelSelector selector = new DataPanelSelector(link);
		PrimElementsGateway gateway = selector.getGateway();
		DataFile<InputStream> file = gateway.getRawData(context, link.getDataPanelId());
		return file;
	}

}
