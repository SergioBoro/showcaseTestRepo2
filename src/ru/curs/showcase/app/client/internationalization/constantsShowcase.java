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

	@DefaultMessage("Show at most")
	String pageGridShowAtMost();

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

	@DefaultMessage("An error occurred during downloading files")
	String uploadError();

	@DefaultMessage("Error during serialization parameters for Http-request plug.")
	String jsGridSerializationError();

	@DefaultMessage("An error occurred while deserializing an object")
	String jsGridDeserializationError();

	@DefaultMessage("Loading...")
	String jsGridLoadingMessage();

	@DefaultMessage("Export to Excel descendants of the current record")
	String jsTreeGridExportToExcelChilds();

	@DefaultMessage("Export to Excel records 0-level")
	String jsTreeGridExportToExcel0Level();

	@DefaultMessage("list of values")
	String conditionListOfValues();

	@DefaultMessage("equal")
	String conditionEqual();

	@DefaultMessage("does not equal")
	String conditionNotEqual();

	@DefaultMessage("is less than")
	String conditionLess();

	@DefaultMessage("less than or equal")
	String conditionLessEqual();

	@DefaultMessage("is greater than")
	String conditionGreater();

	@DefaultMessage("greater than or equal")
	String conditionGreaterEqual();

	@DefaultMessage("contains")
	String conditionContain();

	@DefaultMessage("starts with")
	String conditionStartWith();

	@DefaultMessage("ends with")
	String conditionEndWith();

	@DefaultMessage("does not contain")
	String conditionNotContain();

	@DefaultMessage("does not start with")
	String conditionNotStartWith();

	@DefaultMessage("does not end with")
	String conditionNotEndWith();

	@DefaultMessage("is empty")
	String conditionIsEmpty();

	@DefaultMessage("Mapping:")
	String jsFilterLink();

	@DefaultMessage("Column:")
	String jsFilterColumn();

	@DefaultMessage("Condition:")
	String jsFilterCondition();

	@DefaultMessage("Value:")
	String jsFilterValue();

	@DefaultMessage("Select values:")
	String jsFilterSelectValues();

	@DefaultMessage("Update filter condition")
	String jsFilterUpdate();

	@DefaultMessage("Add")
	String jsFilterAdd();

	@DefaultMessage("Delete")
	String jsFilterDel();

	@DefaultMessage("Clear")
	String jsFilterClear();

	@DefaultMessage("Cancel")
	String jsFilterCancel();

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

	@DefaultMessage("Фильтр")
	String grid_caption_filter();

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
