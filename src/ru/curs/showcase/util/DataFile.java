package ru.curs.showcase.util;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Базовый класс для обмена файлами между сервером и клиентами. Содержимое файла
 * может храниться как в виде OutputStream, так и InputStream или даже строки -
 * чтобы избежать цепочки преобразований.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип для хранения содержимого - OutputStream или InputStream.
 */
public class DataFile<T> {

	private static final String WEB_CONSOLE_ADD_TEXT_FILES_PARAM = "web.console.add.text.files";

	/**
	 * Данные файла.
	 */
	private T data;

	/**
	 * Имя файла. При использовании русского имени возможны проблемы!!!
	 */
	private String name;

	/**
	 * Кодировка, используемая для данных.
	 */
	private String encoding = TextUtils.DEF_ENCODING;

	public DataFile() {
		super();
	}

	public DataFile(final T aData, final String fileName) {
		name = fileName;
		data = aData;
	}

	@XmlTransient
	public final T getData() {
		return data;
	}

	public final void setData(final T aData) {
		data = aData;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String aName) {
		name = aName;
	}

	/**
	 * Возвращает id файла, в качестве которого выступает имя файла без пути и
	 * расширения.
	 * 
	 * @return - id.
	 */
	public String getId() {
		return TextUtils.extractFileName(name);
	}

	public boolean isTextFile() {
		String[] stdTextExtensions =
			{
					"txt", "xml", "xsd", "xsl", "sql", "ini", "properties", "htm", "html", "java",
					"cmd", "py", "svg" };
		if (checkExtensionsArray(stdTextExtensions)) {
			return true;
		}

		String fromAppProps = FileUtils.getGeneralOptionalParam(WEB_CONSOLE_ADD_TEXT_FILES_PARAM);
		if (fromAppProps != null) {
			String[] userTextExtensions = fromAppProps.split(":");
			return checkExtensionsArray(userTextExtensions);
		}
		return false;
	}

	public boolean isXMLFile() {
		String[] stdXMLExtensions = { "xml", "xsd", "xsl", "xslt", "htm", "html" };
		if (checkExtensionsArray(stdXMLExtensions)) {
			return true;
		}

		return false;
	}

	private boolean checkExtensionsArray(final String[] aExtensions) {
		for (String ext : aExtensions) {
			if (name.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(final String aEncoding) {
		encoding = aEncoding;
	}

}