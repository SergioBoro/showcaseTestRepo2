package ru.curs.showcase.app.api.grid;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * Класс провайдера данных грида из GXT.
 */
public class LiveGridModelProvider implements ValueProvider<LiveGridModel, String> {

	private final String key;

	public LiveGridModelProvider(final String aKey) {
		key = aKey;
	}

	@Override
	public String getPath() {
		return key;
	}

	@Override
	public String getValue(final LiveGridModel object) {
		return (String) object.get(key);
	}

	@Override
	public void setValue(final LiveGridModel object, final String value) {
		object.set(key, value);
	}

}
