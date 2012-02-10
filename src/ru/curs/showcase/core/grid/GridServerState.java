package ru.curs.showcase.core.grid;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.event.Action;

/**
 * Динамические настройки грида, загружаемые из БД, которые требуются для
 * формирования грида на сервере.
 * 
 * @author den
 * 
 */
public class GridServerState implements SerializableElement {
	public static final String GRID_DEFAULT_PROFILE = "default.properties";

	private static final long serialVersionUID = -1419798447839020679L;

	/**
	 * Имя используемого профайла настроек грида.
	 */
	private String profile = GRID_DEFAULT_PROFILE;

	/**
	 * Число записей в таблице, удовлетворяющих условию выборки. Может быть
	 * получено из БД или вычислено после продвижения курсора в конец датасета.
	 */
	private Integer totalCount;

	/**
	 * Идентификатор записи, которая должна быть выбрана автоматически после
	 * загрузки данных. По идентификатору можно определить autoSelectRecord.
	 */
	private Integer autoSelectRecordId = null;

	/**
	 * Идентификатор столбца, который должен быть выбран автоматически после
	 * загрузки данных. По идентификатору можно определить autoSelectColumn.
	 */
	private String autoSelectColumnId = null;

	/**
	 * Указание на то, как именно учитывать autoSelectRecordId - как
	 * относительный или абсолютный номер записи.
	 */
	private Boolean autoSelectRelativeRecord = true;

	private ColumnSet columnSet = null;

	private Action defaultAction = null;

	public String getProfile() {
		return profile;
	}

	public void setProfile(final String aProfile) {
		profile = aProfile;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(final Integer aTotalCount) {
		totalCount = aTotalCount;
	}

	public Integer getAutoSelectRecordId() {
		return autoSelectRecordId;
	}

	public void setAutoSelectRecordId(final Integer aAutoSelectRecordId) {
		autoSelectRecordId = aAutoSelectRecordId;
	}

	public String getAutoSelectColumnId() {
		return autoSelectColumnId;
	}

	public void setAutoSelectColumnId(final String aAutoSelectColumnId) {
		autoSelectColumnId = aAutoSelectColumnId;
	}

	public Boolean getAutoSelectRelativeRecord() {
		return autoSelectRelativeRecord;
	}

	public void setAutoSelectRelativeRecord(final Boolean aAutoSelectRelativeRecord) {
		autoSelectRelativeRecord = aAutoSelectRelativeRecord;
	}

	public ColumnSet getColumnSet() {
		return columnSet;
	}

	public void setColumnSet(final ColumnSet aColumnSet) {
		columnSet = aColumnSet;
	}

	public Action getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(final Action aDefaultAction) {
		defaultAction = aDefaultAction;
	}
}
