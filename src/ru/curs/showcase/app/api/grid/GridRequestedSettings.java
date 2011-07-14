package ru.curs.showcase.app.api.grid;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.*;

/**
 * Класс, содержащий настройки грида, которые интерактивно могут изменять
 * пользователи в процессе работы с гридом. В частности, содержит информацию о
 * выделении в гриде. Информация о выделении может быть использована при печати,
 * а также выделение должно восстанавливаться в некоторых случаях - в частности,
 * при возврате из карточки.
 * 
 * @author den
 * 
 */
public class GridRequestedSettings extends TransferableElement implements SerializableElement {

	/**
	 * Размер страницы с данными грида по умолчанию.
	 */
	public static final int DEF_PAGE_SIZE_VAL = 20;

	/**
	 * Номер страницы в гриде по умолчанию (нумерация с 1).
	 */
	private static final int DEF_PAGE_NUMBER = 1;

	@Override
	public String toString() {
		return "GridRequestedSettings.sortedColumns=" + sortedColumns
				+ "&GridRequestedSettings.pageNumber=" + pageNumber
				+ "&GridRequestedSettings.pageSize=" + pageSize;
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2005065362465664382L;

	/**
	 * Набор столбцов, на которых установлена сортировка. Если null - сортировка
	 * не задана.
	 */
	private Collection<Column> sortedColumns = new ArrayList<Column>();

	/**
	 * Номер текущей страницы с данными. По-умолчанию - первая страница.
	 */
	private Integer pageNumber = DEF_PAGE_NUMBER;

	/**
	 * Установленный размер страницы. Если null - размер считывается из файла
	 * настроек.
	 */
	private Integer pageSize = DEF_PAGE_SIZE_VAL;

	/**
	 * Признак того, что нужно применять форматирование для дат и чисел при
	 * формировании грида. По умолчанию - нужно. Отключать эту опцию необходимо
	 * при экспорте в Excel.
	 */
	private Boolean applyLocalFormatting = true;

	/**
	 * Идентификатор выделенной по клику в гриде записи.
	 */
	private String currentRecordId = null;

	/**
	 * Идентификатор выделенного по клику в гриде столбца. Имеет смысл только в
	 * режиме выделения ячеек.
	 */
	private String currentColumnId = null;

	/**
	 * Массив идентификаторов выделенных с помощью селектора записей в гриде.
	 */
	private List<String> selectedRecordIds = new ArrayList<String>();

	/**
	 * Признак того, что грид обновляется после взаимодействия с ним
	 * пользователя.
	 */
	private Boolean isFirstLoad = false;

	/**
	 * Сбрасывает настройки таким образом, чтобы сервер вернул все записи на
	 * первой странице.
	 */
	public void resetForReturnAllRecords() {
		setPageNumber(1);
		setPageSize(Integer.MAX_VALUE - 1);
	}

	/**
	 * Получение сортировки из запрашиваемых параметров.
	 * 
	 * @param aCurColumn
	 *            - столбец.
	 * @return - сортировка.
	 */
	public Sorting getSortingForColumn(final Column aCurColumn) {
		if (sortedColumns != null) {
			Iterator<Column> iterator = sortedColumns.iterator();
			while (iterator.hasNext()) {
				Column col = iterator.next();
				if (col.getId().equals(aCurColumn.getId())) {
					return col.getSorting();
				}
			}
		}
		return null;
	}

	public final Integer getPageSize() {
		return pageSize;
	}

	public final void setPageSize(final Integer aPageSize) {
		this.pageSize = aPageSize;
	}

	public final Collection<Column> getSortedColumns() {
		return sortedColumns;
	}

	public final Integer getPageNumber() {
		return pageNumber;
	}

	public final void setSortedColumns(final Collection<Column> aSortedColumns) {
		this.sortedColumns = aSortedColumns;
	}

	public final void setPageNumber(final Integer aPageNumber) {
		this.pageNumber = aPageNumber;
	}

	/**
	 * Функция нормализации настроек - т.е. приведения их в вид, необходимый для
	 * правильной работы шлюза и фабрики.
	 */
	public void normalize() {
		Collection<Column> source = getSortedColumns();
		SortedMap<Integer, Column> orderedByIndex = new TreeMap<Integer, Column>();
		Iterator<Column> iterator = source.iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			orderedByIndex.put(col.getIndex(), col);
		}
		setSortedColumns(orderedByIndex.values());
	}

	public final Boolean getApplyLocalFormatting() {
		return applyLocalFormatting;
	}

	public final void setApplyLocalFormatting(final Boolean aApplyFormatting) {
		applyLocalFormatting = aApplyFormatting;
	}

	public String getCurrentRecordId() {
		return currentRecordId;
	}

	public void setCurrentRecordId(final String aCurrentRecordId) {
		currentRecordId = aCurrentRecordId;
	}

	public String getCurrentColumnId() {
		return currentColumnId;
	}

	public void setCurrentColumnId(final String aCurrentColumnId) {
		currentColumnId = aCurrentColumnId;
	}

	public List<String> getSelectedRecordIds() {
		return selectedRecordIds;
	}

	public void setSelectedRecordIds(final List<String> aSelectedRecordIds) {
		selectedRecordIds = aSelectedRecordIds;
	}

	public int getFirstRecord() {
		return pageSize * (pageNumber - 1) + 1;
	}

	/**
	 * Проверка на то, что сортировка присутствует при данных настройках.
	 */
	public boolean sortingEnabled() {
		return (sortedColumns != null) && (sortedColumns.size() > 0);
	}

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static GridRequestedSettings createDefault() {
		GridRequestedSettings result = new GridRequestedSettings();
		result.isFirstLoad = true;
		return result;
	}

	public Boolean isFirstLoad() {
		return isFirstLoad;
	}

	public void setIsFirstLoad(final Boolean value) {
		isFirstLoad = value;
	}
}
