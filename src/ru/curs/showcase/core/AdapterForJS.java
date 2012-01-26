package ru.curs.showcase.core;

import java.lang.reflect.Modifier;

import ru.curs.showcase.app.api.JSONObject;

import com.google.gson.*;

/**
 * Класс адаптера для использования объектов в JS коде.
 * 
 * @author den
 * 
 */
public class AdapterForJS {

	/**
	 * Функция подготовки Java-объекта для использования в JS коде. Подготовка
	 * заключается в построении JSON объекта по Java объекту.
	 * 
	 * @param source
	 *            - Java-объект для обработки.
	 */
	public void adapt(final JSONObject source) {
		GsonBuilder builder =
			new GsonBuilder().serializeNulls().excludeFieldsWithModifiers(
					Modifier.TRANSIENT + Modifier.STATIC);
		Gson gson = builder.create();

		String json = gson.toJson(source.getJavaDynamicData());
		source.setJsDynamicData(json);
	}
}
