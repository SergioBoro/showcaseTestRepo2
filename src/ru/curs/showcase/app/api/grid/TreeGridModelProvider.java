package ru.curs.showcase.app.api.grid;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * Класс провайдера данных tree-грида из GXT.
 */
public class TreeGridModelProvider implements ValueProvider<TreeGridModel, String> {

	private final String key;

	public TreeGridModelProvider(final String aKey) {
		key = aKey;
	}

	@Override
	public String getPath() {
		return key;
	}

	@Override
	public String getValue(final TreeGridModel object) {
		return (String) object.get(key);
	}

	@Override
	public void setValue(final TreeGridModel object, final String value) {
		object.set(key, value);
	}

}
