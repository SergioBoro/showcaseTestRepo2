package ru.curs.showcase.model.geomap;

import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Получает настройки грида из профайла.
 * 
 */
public class GeoMapPropsReader extends ProfileReader {

	public GeoMapPropsReader(final String aProfile) {
		super(aProfile);
	}

	@Override
	protected String getProfileCatalog() {
		return "geomapproperties";
	}

	@Override
	protected SettingsFileType getSettingsType() {
		return SettingsFileType.GEOMAP_PROPERTIES;
	}

}
