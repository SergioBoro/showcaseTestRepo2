package ru.curs.showcase.runtime;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;

import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Пул фабрик XSL трансформации (они не thread-safe, использовать одну нельзя).
 * 
 * @author den
 * 
 */
public final class XSLTransformerPoolFactory extends Pool<Transformer> {

	private static final ThreadLocal<TransformerFactory> TF = new ThreadLocal<>();
	public static final String XSLTFORMS_XSL = "xsltforms.xsl";

	private static final XSLTransformerPoolFactory INSTANCE = new XSLTransformerPoolFactory();

	private XSLTransformerPoolFactory() {
		super();
	}

	public static XSLTransformerPoolFactory getInstance() {
		return INSTANCE;
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
			return XSLTransformerPoolFactory.getTransformerFactory().newTransformer();
		} else {
			InputStream is = getInputStream(xsltFileName);
			return XSLTransformerPoolFactory.getTransformerFactory().newTransformer(
					new StreamSource(is));
		}
	}

	private InputStream getInputStream(final String xsltFileName) throws IOException {
		InputStream is = null;
		switch (xsltFileName) {
		case XSLTFORMS_XSL:
			is = XSLTransformerPoolFactory.class.getResourceAsStream(xsltFileName);
			break;
		case UserdataUtils.GRIDDATAXSL:
			is =
				UserdataUtils.loadUserDataToStream(UserdataUtils.XSLTTRANSFORMSFORGRIDDIR + "/"
						+ xsltFileName);
			break;
		default:
			is =
				UserdataUtils.loadUserDataToStream(SettingsFileType.XSLT.getFileDir() + "/"
						+ xsltFileName);
		}
		return is;
	}

	@Override
	protected Pool<Transformer> getLock() {
		return XSLTransformerPoolFactory.getInstance();
	}

	public static void cleanup() {
		if (TF.get() != null) {
			TF.remove();
		}
	}

	public static TransformerFactory getTransformerFactory() {
		if (TF.get() == null) {
			TF.set(TransformerFactory.newInstance());
		}
		return TF.get();
	}

}
