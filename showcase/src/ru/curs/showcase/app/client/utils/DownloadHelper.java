package ru.curs.showcase.app.client.utils;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * Компонент для скачивания файлов с сервера.
 * 
 * @author den
 * 
 */
public final class DownloadHelper extends RunServletByFormHelper {

	/**
	 * A static instance of this class.
	 * 
	 * @autogenerated by CodeHaggis (http://sourceforge.net/projects/haggis)
	 */
	private static DownloadHelper instance;

	/**
	 * Constructor of this class.
	 * 
	 * @autogenerated by CodeHaggis (http://sourceforge.net/projects/haggis)
	 */
	public DownloadHelper() {
		DownloadHelper.instance = this;
	}

	/**
	 * Returns a static instance of this class. If the instance does not already
	 * exist, a new instance is created.
	 * 
	 * @return instance The static instance of this class.
	 * @autogenerated by CodeHaggis (http://sourceforge.net/projects/haggis)
	 */
	public static DownloadHelper getInstance() {
		if (instance == null) {
			instance = new DownloadHelper();
		}
		return instance;
	}

	@Override
	protected void initFormProps() {
		super.initFormProps();
		setEncoding(FormPanel.ENCODING_URLENCODED);
	}

	/**
	 * Настройка внешнего вида формы.
	 */
	@Override
	protected void initFormView() {
		setPixelSize(1, 1);
		setVisible(false);
	}

}
