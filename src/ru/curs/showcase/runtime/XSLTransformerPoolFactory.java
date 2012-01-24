package ru.curs.showcase.runtime;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;

import ru.curs.showcase.model.html.xform.XFormProducer;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Пул фабрик XSL трансформации (они не thread-safe, использовать одну нельзя).
 * 
 * @author den
 * 
 */
public final class XSLTransformerPoolFactory extends Pool<Transformer> {

	private static XSLTransformerPoolFactory instance;

	private XSLTransformerPoolFactory() {
		super();
	}

	public static XSLTransformerPoolFactory getInstance() {
		if (instance == null) {
			instance = new XSLTransformerPoolFactory();
		}
		return instance;
	}

	@Override
	protected void cleanReusable(final Transformer aReusable) {
		super.cleanReusable(aReusable);
		aReusable.reset();
	}

	@Override
	protected Transformer createReusableItem(final String xsltFileName)
			throws TransformerConfigurationException, IOException {
		if (xsltFileName == null) {
			return XMLUtils.getTransformerFactory().newTransformer();
		} else {
			InputStream is = getInputStream(xsltFileName);
			return XMLUtils.getTransformerFactory().newTransformer(new StreamSource(is));
		}
	}

	private InputStream getInputStream(final String xsltFileName) throws IOException {
		InputStream is = null;
		switch (xsltFileName) {
		case XFormProducer.XSLTFORMS_XSL:
			is = XFormProducer.class.getResourceAsStream(xsltFileName);
			break;
		case AppProps.GRIDDATAXSL:
			is =
				AppProps.loadUserDataToStream(AppProps.XSLTTRANSFORMSFORGRIDDIR + "/"
						+ xsltFileName);
			break;
		default:
			is =
				AppProps.loadUserDataToStream(SettingsFileType.XSLT.getFileDir() + "/"
						+ xsltFileName);
		}
		return is;
	}

	@Override
	protected Pool<Transformer> getLock() {
		return XSLTransformerPoolFactory.getInstance();
	}

}
