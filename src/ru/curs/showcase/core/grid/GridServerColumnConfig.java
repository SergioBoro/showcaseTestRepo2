package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.GridValueType;

/**
 * Столбец.
 */

public class GridServerColumnConfig {

	private String id = null;
	private GridValueType valueType = null;
	private String format = null;

	public GridServerColumnConfig(final String aId, final GridValueType aValueType,
			final String aFormat) {
		id = aId;
		valueType = aValueType;
		format = aFormat;
	}

	public GridValueType getValueType() {
		return valueType;
	}

	public void setValueType(final GridValueType aValueType) {
		valueType = aValueType;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId1) {
		id = aId1;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(final String aFormat) {
		format = aFormat;
	}

}
