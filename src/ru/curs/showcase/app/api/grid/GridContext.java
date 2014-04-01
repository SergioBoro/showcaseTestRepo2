package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementSubType;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Класс, содержащий детальный контекст грида. Включает в себя основной контекст
 * плюс настройки, которые интерактивно могут изменять пользователи в процессе
 * работы с гридом. Пользовательские настройки должны восстанавливаться после
 * обновления элемента. Замечание: @XmlRootElement не может указывать на то же
 * имя context, что и CompositeContext!
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "gridContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class GridContext extends CompositeContext {

	/**
	 * Размер страницы с данными грида по умолчанию.
	 */
	public static final int DEF_PAGE_SIZE_VAL = 20;

	/**
	 * Номер страницы в гриде по умолчанию (нумерация с 1).
	 */
	private static final int DEF_PAGE_NUMBER = 1;

	private static final int DEF_OFFSET = 0;
	private static final int DEF_LIMIT = 50;

	public GridContext(final CompositeContext aContext) {
		super();
		assignNullValues(aContext);
	}

	@Override
	public String toString() {
		return "GridContext [sortedColumns=" + sortedColumns + ", pageInfo=" + pageInfo
				+ ", liveInfo=" + liveInfo + ", currentRecordId=" + currentRecordId
				+ ", currentColumnId=" + currentColumnId + ", selectedRecordIds="
				+ selectedRecordIds + ", isFirstLoad=" + isFirstLoad + ", parentId=" + parentId
				+ ", toString()=" + super.toString() + "]";
	}

	private static final long serialVersionUID = 2005065362465664382L;

	/**
	 * Набор столбцов, на которых установлена сортировка. Если null - сортировка
	 * не задана.
	 */
	@XmlElement(name = "sortedColumn")
	private List<Column> sortedColumns = new ArrayList<Column>();

	private PageInfo pageInfo = new PageInfo(DEF_PAGE_NUMBER, DEF_PAGE_SIZE_VAL);

	private LiveInfo liveInfo = new LiveInfo(DEF_OFFSET, DEF_LIMIT);

	private GridFilterInfo gridFilterInfo = new GridFilterInfo();

	@XmlTransient
	private GridListOfValuesInfo gridListOfValuesInfo = new GridListOfValuesInfo();

	private DataPanelElementSubType subtype = null;

	/**
	 * Идентификатор parent-записи. Имеет смысл только для tree-грида.
	 */
	private String parentId = null;

	/**
	 * Идентификатор выделенной по клику в гриде записи.
	 */
	private String currentRecordId = null;

	/**
	 * Предлагаемая ширина грида, соответствующая ширине доступного
	 * пространства.
	 */
	private Integer currentDatapanelWidth = 0;

	/**
	 * Предлагаемая высота грида, соответствующая высоте доступного
	 * пространства.
	 */
	private Integer currentDatapanelHeight = 0;

	/**
	 * Идентификатор выделенного по клику в гриде столбца. Имеет смысл только в
	 * режиме выделения ячеек.
	 */
	private String currentColumnId = null;

	/**
	 * Массив идентификаторов выделенных с помощью селектора записей в гриде.
	 */
	@XmlElement(name = "selectedRecordId")
	private List<String> selectedRecordIds = new ArrayList<String>();

	/**
	 * Признак того, как нужно обновлять элемент. Если isFirstLoad == true - то
	 * полностью, в противном случае - введенные пользователем данные должны
	 * сохраниться.
	 */
	@XmlTransient
	private Boolean isFirstLoad = false;

	/**
	 * Сбрасывает настройки таким образом, чтобы сервер вернул все записи на
	 * первой странице.
	 */
	public void resetForReturnAllRecords() {
		setPageNumber(1);
		setPageSize(Integer.MAX_VALUE - 1);

		liveInfo.setOffset(0);
		liveInfo.setLimit(Integer.MAX_VALUE - 1);
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
			for (Column col : sortedColumns) {
				if (col.getId().equals(aCurColumn.getId())) {
					return col.getSorting();
				}
			}
		}
		return null;
	}

	public void setPageNumber(final int pageNumber) {
		pageInfo.setNumber(pageNumber);
	}

	public int getPageNumber() {
		return pageInfo.getNumber();
	}

	public void setPageSize(final int pageSize) {
		pageInfo.setSize(pageSize);
	}

	public int getPageSize() {
		return pageInfo.getSize();
	}

	public final List<Column> getSortedColumns() {
		return sortedColumns;
	}

	public final void setSortedColumns(final List<Column> aSortedColumns) {
		this.sortedColumns = aSortedColumns;
	}

	/**
	 * Функция нормализации настроек - т.е. приведения их в вид, необходимый для
	 * правильной работы шлюза и фабрики.
	 */
	public void normalize() {
		List<Column> source = getSortedColumns();
		Map<Integer, Column> orderedByIndex = new TreeMap<Integer, Column>();
		for (Column col : source) {
			orderedByIndex.put(col.getIndex(), col);
		}
		setSortedColumns(new ArrayList<Column>(orderedByIndex.values()));
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

	/**
	 * Проверка на то, что сортировка присутствует при данных настройках.
	 */
	public boolean sortingEnabled() {
		return (sortedColumns != null) && (!sortedColumns.isEmpty());
	}

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static GridContext createFirstLoadDefault() {
		GridContext result = new GridContext();
		result.isFirstLoad = true;
		return result;
	}

	public GridContext() {
		super();
	}

	public Boolean isFirstLoad() {
		return isFirstLoad;
	}

	public void setIsFirstLoad(final Boolean value) {
		isFirstLoad = value;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(final PageInfo aPageInfo) {
		pageInfo = aPageInfo;
	}

	public LiveInfo getLiveInfo() {
		return liveInfo;
	}

	public void setLiveInfo(final LiveInfo aLiveInfo) {
		liveInfo = aLiveInfo;
	}

	public GridFilterInfo getGridFilterInfo() {
		return gridFilterInfo;
	}

	public void setGridFilterInfo(final GridFilterInfo aGridFilterInfo) {
		gridFilterInfo = aGridFilterInfo;
	}

	public GridListOfValuesInfo getGridListOfValuesInfo() {
		return gridListOfValuesInfo;
	}

	public void setGridListOfValuesInfo(final GridListOfValuesInfo aGridListOfValuesInfo) {
		gridListOfValuesInfo = aGridListOfValuesInfo;
	}

	public DataPanelElementSubType getSubtype() {
		return subtype;
	}

	public void setSubtype(final DataPanelElementSubType aSubtype) {
		subtype = aSubtype;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(final String aParentId) {
		parentId = aParentId;
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public GridContext gwtClone() {
		GridContext res = (GridContext) super.gwtClone();

		res.currentColumnId = currentColumnId;
		res.currentRecordId = currentRecordId;
		res.isFirstLoad = isFirstLoad.booleanValue();
		res.parentId = parentId;
		res.currentDatapanelWidth = currentDatapanelWidth;
		res.currentDatapanelHeight = currentDatapanelHeight;

		res.pageInfo.setNumber(pageInfo.getNumber());
		res.pageInfo.setSize(pageInfo.getSize());

		res.liveInfo.setOffset(liveInfo.getOffset());
		res.liveInfo.setLimit(liveInfo.getLimit());
		res.liveInfo.setTotalCount(liveInfo.getTotalCount());

		for (Column col : sortedColumns) {
			res.sortedColumns.add(col); // TODO глубокое клонирование
		}
		for (String id : selectedRecordIds) {
			res.selectedRecordIds.add(id);
		}

		res.gridFilterInfo.setMaxId(gridFilterInfo.getMaxId());
		res.gridFilterInfo.getFilters().clear();
		res.gridFilterInfo.getFilters().addAll(gridFilterInfo.getFilters());

		res.gridListOfValuesInfo.setMaxId(gridListOfValuesInfo.getMaxId());
		res.gridListOfValuesInfo.getFilters().clear();
		res.gridListOfValuesInfo.getFilters().addAll(gridListOfValuesInfo.getFilters());

		return res;
	}

	@Override
	protected GridContext newInstance() {
		return new GridContext();
	}

	public void applyCompositeContext(final CompositeContext aContext) {
		if (aContext == null) {
			return;
		}
		setMain(aContext.getMain());
		setAdditional(aContext.getAdditional());
		setFilter(aContext.getFilter());
		setSession(aContext.getSession());
		setSessionParamsMap(aContext.getSessionParamsMap());
		setRelated(aContext.getRelated());

	}

	public Integer getCurrentDatapanelWidth() {
		return currentDatapanelWidth;
	}

	public void setCurrentDatapanelWidth(final Integer acurrentDatapanelWidth) {
		this.currentDatapanelWidth = acurrentDatapanelWidth;
	}

	public Integer getCurrentDatapanelHeight() {
		return currentDatapanelHeight;
	}

	public void setCurrentDatapanelHeight(final Integer acurrentDatapanelHeight) {
		this.currentDatapanelHeight = acurrentDatapanelHeight;
	}

}
