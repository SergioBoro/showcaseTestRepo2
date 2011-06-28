package ru.curs.showcase.util;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.exception.NotXMLException;
import ru.curs.showcase.model.DataFile;

/**
 * Класс для трансформации пользовательских XML c проверкой схемы.
 * 
 * @author den
 * 
 */
public final class UserXMLTransformer {

	/**
	 * Исходный поток.
	 */
	private DataFile<ByteArrayOutputStream> subject;

	/**
	 * Исходный файл в виде строки.
	 */
	private String strSubject;

	/**
	 * Процедура с описанием трансформации и схемы.
	 */
	private DataPanelElementProc proc;

	/**
	 * Результат преобразования (если оно произошло).
	 */
	private InputStream result = null;

	/**
	 * Проверяет исходник и трансформирует его если в процедуре трансформации
	 * описаны соответствующие схема и файл трансформации.
	 * 
	 * @throws IOException
	 */
	public void checkAndTransform() throws IOException {
		if ((subject == null) || (proc == null)) {
			return;
		}

		if (proc.getSchemaName() != null) {
			Document doc = checkForXMLandCreateDoc();
			XMLValidator validator = new XMLValidator(new UserDataXSDSource());
			validator.validate(new XMLSource(doc, subject.getName(), proc.getSchemaName()));
		}
		if (proc.getTransformName() != null) {
			InputStream is = StreamConvertor.outputToInputStream(subject.getData());
			String res = XMLUtils.xsltTransform(is, proc.getTransformName());
			result = TextUtils.stringToStream(res);
		}
	}

	private Document checkForXMLandCreateDoc() throws IOException {
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc = null;
		InputStream is = StreamConvertor.outputToInputStream(subject.getData());
		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			throw new NotXMLException(e, subject.getName());
		} catch (IOException e) {
			throw e;
		}
		return doc;
	}

	public DataFile<ByteArrayOutputStream> getSubject() {
		return subject;
	}

	public UserXMLTransformer(final DataFile<ByteArrayOutputStream> aSubject,
			final DataPanelElementProc aProc) {
		super();
		subject = aSubject;
		proc = aProc;
	}

	public UserXMLTransformer(final String aSubject, final DataPanelElementProc aProc)
			throws IOException {
		super();
		strSubject = aSubject;
		// некрасиво, зато меньше кода, по сравнению с вариантом преобразования
		// в Document
		if (aSubject != null) {
			ByteArrayOutputStream os =
				StreamConvertor.inputToOutputStream(TextUtils.stringToStream(aSubject));
			subject = new DataFile<ByteArrayOutputStream>(os, "данные формы");
		}

		proc = aProc;
	}

	public void setSubject(final DataFile<ByteArrayOutputStream> aSubject) {
		subject = aSubject;
	}

	public DataPanelElementProc getProc() {
		return proc;
	}

	public void setProc(final DataPanelElementProc aProc) {
		proc = aProc;
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public DataFile<InputStream> getInputStreamResult() {
		if (result != null) {
			return new DataFile<InputStream>(result, subject.getName());
		} else {
			return new DataFile<InputStream>(
					StreamConvertor.outputToInputStream(subject.getData()), subject.getName());
		}
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public DataFile<ByteArrayOutputStream> getOutputStreamResult() throws IOException {
		if (result != null) {
			return new DataFile<ByteArrayOutputStream>(
					StreamConvertor.inputToOutputStream(result), subject.getName());
		} else {
			return subject;
		}
	}

	/**
	 * Возвращает результат преобразования файла или конвертирует в нужный
	 * формат входной файл в случае, если преобразование не задано.
	 * 
	 * @return - выходной файл.
	 */
	public String getStringResult() throws IOException {
		if (result != null) {
			return TextUtils.streamToString(result);
		} else {
			return strSubject;
		}
	}
}
