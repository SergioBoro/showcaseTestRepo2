package ru.curs.showcase.model;

/**
 * Интерфейс для выбора источника документа - БД или файла по его имени.
 * 
 * 
 * @param <T>
 *            - интерфейс шлюза.
 */
public abstract class SourceSelector<T> {
	private String sourceName;

	public String getSourceName() {
		return sourceName;
	}

	public SourceSelector(final String aSourceName) {
		super();
		sourceName = aSourceName;
	}

	public abstract T getGateway();

	public void setSourceName(final String aSourceName) {
		sourceName = aSourceName;
	}

	public SourceSelector() {
		super();
	}

	/**
	 * Возвращает зафиксированное расширение для файлов, содержащих документ
	 * данного типа.
	 */
	protected abstract String getFileExt();

	protected boolean isFile() {
		return sourceName.endsWith("." + getFileExt());
	}
}
