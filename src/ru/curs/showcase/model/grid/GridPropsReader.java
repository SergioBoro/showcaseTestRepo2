package ru.curs.showcase.model.grid;

import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Получает настройки грида из профайла.
 * 
 */
public class GridPropsReader extends ProfileReader {

	public GridPropsReader(final String aProfile) {
		super(aProfile);
	}

	@Override
	protected String getProfileCatalog() {
		return "gridproperties";
	}

	@Override
	protected SettingsFileType getSettingsType() {
		return SettingsFileType.GRID_PROPERTIES;
	}

}
