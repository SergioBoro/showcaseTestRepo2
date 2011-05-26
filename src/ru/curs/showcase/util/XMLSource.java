package ru.curs.showcase.util;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.w3c.dom.Document;

/**
 * Источник для XML документов. Может содержать данные в InputStream, в DOM
 * Document или в виде, пригодном для работы с SAX парсером.
 * 
 * @author den
 * 
 */
public class XMLSource {
	/**
	 * Поток с XML данными.
	 */
	private InputStream inputStream;
	/**
	 * XML документ.
	 */
	private Document document;
	/**
	 * Экземпляр парсера для работы с InputStream.
	 */
	private SAXParser saxParser;

	/**
	 * Возвращает правильный преобразователь, анализируя свое содержимое.
	 * 
	 * @return - преобразователь.
	 */
	public XMLExtractor getExtractor() {
		if (document != null) {
			return new DocumentXMLExtractor();
		} else if (saxParser != null) {
			return new SAXXMLExtractor();
		} else if (inputStream != null) {
			return new InputStreamXMLExtractor();
		}
		return null;
	}

	public XMLSource(final InputStream aInputStream, final SAXParser aSaxParser) {
		super();
		inputStream = aInputStream;
		saxParser = aSaxParser;
	}

	public XMLSource(final Document aDocument) {
		super();
		document = aDocument;
	}

	public XMLSource(final InputStream aInputStream) {
		super();
		inputStream = aInputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(final InputStream aInputStream) {
		inputStream = aInputStream;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(final Document aDocument) {
		document = aDocument;
	}

	public SAXParser getSaxParser() {
		return saxParser;
	}

	public void setSaxParser(final SAXParser aSaxParser) {
		saxParser = aSaxParser;
	}
}
