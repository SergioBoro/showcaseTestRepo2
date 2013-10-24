/**
 * 
 */
package ru.curs.showcase.app.client.internationalization;

import com.google.gwt.i18n.client.Messages;

/**
 * @author a.lugovtsov
 * 
 */

public interface constantsShowcase extends Messages {

	@DefaultMessage("Cancel")
	String selectorCancelText();

	@DefaultMessage("Starts with (Ctrl+B)")
	String selectorStartsWithText();

	@DefaultMessage("Not Found")
	String multySelectorStringNotFound();

	@DefaultMessage("when receiving data from the server to the table")
	String gridErrorGetTable();

	@DefaultMessage("Export to Excel")
	String gridExportToExcelCaption();

	@DefaultMessage("The table is empty. Export to Excel will not run.")
	String gridExportToExcelEmptyTable();

	@DefaultMessage("Loading data")
	String treeGridLoadingData();

	@DefaultMessage("Downloadable entry with ID")
	String treeGridLoadingDataDuplicateRecord1();

	@DefaultMessage("already present in the grid. Entries are not loaded.")
	String treeGridLoadingDataDuplicateRecord2();

	@DefaultMessage("when receiving data from the server XForm")
	String xformsErrorGetData();

	@DefaultMessage("when receiving data from the server to the main XForm")
	String xformsErrorGetMainData();

	@DefaultMessage("Пожалуйста, подождите...Идет загрузка данных")
	String please_wait_data_are_loading();

	@DefaultMessage("Добро пожаловать")
	String welcome_tab_caption();

	@DefaultMessage("Ошибка")
	String error();

	@DefaultMessage("Пусто")
	String empty();

	@DefaultMessage("при получении данных графика с сервера")
	String error_of_chart_data_retrieving_from_server();

	@DefaultMessage("при получении данных навигатора с сервера")
	String error_of_navigator_data_retrieving_from_server();

	@DefaultMessage("при получении данных карты с сервера")
	String error_of_map_data_retrieving_from_server();

	@DefaultMessage("при получении данных внешнего плагина с сервера")
	String error_of_plugin_data_retrieving_from_server();

	@DefaultMessage("при получении текстовых данных с сервера")
	String error_of_webtext_data_retrieving_from_server();

	@DefaultMessage("при получении данных о текущем состоянии приложения")
	String error_of_server_current_state_retrieving_from_server();

	@DefaultMessage("при получении данных о главной странице приложения")
	String error_of_main_page_retrieving_from_server();

	@DefaultMessage("Ошибка при экспорте в Excel")
	String grid_error_caption_export_excel();

	@DefaultMessage("Ошибка при скачивании файла")
	String grid_error_caption_file_download();

	@DefaultMessage("Ошибка при построении карты")
	String error_of_map_painting();

	@DefaultMessage("Ошибка при построении внешнего плагина")
	String error_of_plugin_painting();

	@DefaultMessage("Ошибка при получение данных плагина")
	String error_of_plugin_getdata();

	@DefaultMessage("Ошибка при построении графика")
	String error_of_chart_painting();

	@DefaultMessage("Экспорт в Excel текущей страницы")
	String grid_caption_export_to_excel_current_page();

	@DefaultMessage("Экспорт в Excel всей таблицы")
	String grid_caption_export_to_excel_all();

	@DefaultMessage("Копировать в буфер обмена")
	String grid_caption_copy_to_clipboard();

	@DefaultMessage("Начат экспорт в Excel. Это может занять несколько минут. Кликните сюда, чтобы скрыть сообщение")
			String grid_message_popup_export_to_excel();

	@DefaultMessage("Загрузка файла")
	String xform_upload_caption();

	@DefaultMessage("Ошибка при сохранении данных XForms на сервере")
	String xform_save_data_error();

	@DefaultMessage("Ошибка при скачивании файл")
	String xforms_download_error();

	@DefaultMessage("Ошибка при PNG скачивании файла")
	String export_to_png_error();

	@DefaultMessage("Ошибка при загрузке файла(ов) на сервер")
	String xforms_upload_error();

	@DefaultMessage("Ошибка преобразования значения ширины навигатора")
	String transformation_navigator_width_error();

	@DefaultMessage("Ошибка преобразования значения высоты верхнего или нижнего колонтитула")
	String transformation_header_or_footer_width_error();

	@DefaultMessage("при выполнении действия на сервере")
	String error_in_server_activity();

}
