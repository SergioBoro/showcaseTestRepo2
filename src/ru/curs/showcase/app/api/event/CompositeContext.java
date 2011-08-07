package ru.curs.showcase.app.api.event;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.*;

/**
 * Класс составного контекста. Контекст определяет условия фильтрации данных,
 * которые будут получены из БД для отображения элементов информационной панели.
 * Контекст может быть задан как для панели в целом, так и для отдельных ее
 * элементов. Массивы related и sessionParamsMap не участвуют в проверке на
 * идентичность и в клонировании.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = Action.CONTEXT_TAG)
public class CompositeContext extends TransferableElement implements CanBeCurrent,
		SerializableElement, Assignable, GWTClonable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6956997088646193138L;

	/**
	 * Признак того, что идентификатор указывает на hide context - т.е. контекст
	 * для сокрытия элемента с информационной панели. Может быть задан только у
	 * параметра additional context.
	 */
	private static final String HIDE_ID = "hide";

	/**
	 * Основной контекст.
	 */
	private String main;

	/**
	 * Дополнительный контекст. Задается для отдельных элементов информационной
	 * панели.
	 */
	private String additional;

	/**
	 * Контекст пользовательской сессии. Имеет формат XML. В случае, если явно
	 * задан "пользовательский контекст", он включается в контекст сессии.
	 * Содержит серверный данные и поэтому в целях экономии трафика не
	 * передается на клиента.
	 */
	private String session;

	/**
	 * Параметры URL, полученные из клиентской части. На основе их создается
	 * session context для БД.
	 */
	private Map<String, ArrayList<String>> sessionParamsMap;

	/**
	 * Фильтрующий контекст. Задается с помощью компонента XForms.
	 */
	private String filter;

	/**
	 * Контексты связанных элементов. Перед передачей в БД их содержимое
	 * включается в session.
	 */
	private Map<String, CompositeContext> related = new TreeMap<String, CompositeContext>();

	public CompositeContext(final Map<String, List<String>> aParams) {
		super();
		addSessionParams(aParams);
	}

	public CompositeContext() {
		super();
	}

	public String getSession() {
		return session;
	}

	public void setSession(final String aSession) {
		session = aSession;
	}

	@Override
	public String toString() {
		return "mainContext=" + main + ExchangeConstants.LINE_SEPARATOR + "additionalContext="
				+ additional + ExchangeConstants.LINE_SEPARATOR + "sessionContext=" + session
				+ ExchangeConstants.LINE_SEPARATOR + "filterContext=" + filter;
	}

	public final String getMain() {
		return main;
	}

	public final void setMain(final String aMain) {
		this.main = aMain;
	}

	public final String getAdditional() {
		return additional;
	}

	public final void setAdditional(final String aAdditional) {
		this.additional = aAdditional;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String aFilter) {
		filter = aFilter;
	}

	/**
	 * Проверяет, задан ли основной контекст как текущий.
	 * 
	 * @return результат проверки.
	 */
	public boolean mainIsCurrent() {
		return CURRENT_ID.equals(main);
	}

	/**
	 * Проверяет, задан ли дополнительный контекст как текущий.
	 * 
	 * @return результат проверки.
	 */
	public boolean addIsCurrent() {
		return CURRENT_ID.equals(additional);
	}

	@Override
	public void assignNullValues(final Object source) {
		if (source instanceof CompositeContext) {
			CompositeContext sourceContext = (CompositeContext) source;
			if (main == null) {
				main = sourceContext.main;
			}
			if (additional == null) {
				additional = sourceContext.additional;
			}
			if (filter == null) {
				filter = sourceContext.filter;
			}
			if (session == null) {
				session = sourceContext.session;
			}
		}
	}

	/**
	 * Актуализирует контекст на основе переданного.
	 * 
	 * @param aCallContext
	 *            - актуальный контекст.
	 * @return - измененный контекст.
	 */
	public CompositeContext actualizeBy(final CompositeContext aCallContext) {
		if (mainIsCurrent()) {
			main = aCallContext.main;
		}
		if (addIsCurrent()) {
			additional = aCallContext.additional;
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additional == null) ? 0 : additional.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((main == null) ? 0 : main.hashCode());
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		return result;
	}

	// CHECKSTYLE:OFF
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CompositeContext)) {
			return false;
		}
		CompositeContext other = (CompositeContext) obj;
		if (additional == null) {
			if (other.additional != null) {
				return false;
			}
		} else if (!additional.equals(other.additional)) {
			return false;
		}
		if (filter == null) {
			if (other.filter != null) {
				return false;
			}
		} else if (!filter.equals(other.filter)) {
			return false;
		}
		if (main == null) {
			if (other.main != null) {
				return false;
			}
		} else if (!main.equals(other.main)) {
			return false;
		}
		if (session == null) {
			if (other.session != null) {
				return false;
			}
		} else if (!session.equals(other.session)) {
			return false;
		}
		return true;
	}

	// CHECKSTYLE:ON

	/**
	 * Определяет, являются ли контекст скрывающим (элемент).
	 * 
	 * @return результат проверки.
	 */
	public boolean doHiding() {
		return HIDE_ID.equalsIgnoreCase(additional);
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public CompositeContext gwtClone() {
		CompositeContext res = new CompositeContext();
		res.setMain(getMain());
		res.setAdditional(getAdditional());
		res.setSession(getSession());
		res.setFilter(getFilter());
		return res;
	}

	/**
	 * Возвращает строку фильтра на основе переданного дополнительного
	 * контекста.
	 * 
	 * @param aAdditional
	 *            - значение дополнительного контекста.
	 * @return - строка фильтра.
	 */
	public static String generateFilterContextLine(final String aAdditional) {
		return "<" + Action.CONTEXT_TAG + ">" + aAdditional + "</" + Action.CONTEXT_TAG + ">";
	}

	/**
	 * Генерирует общую часть фильтра использую переменную часть, зависящую от
	 * выделенных строк.
	 * 
	 * @param aMutableFilterPart
	 *            - переменная часть фильтра.
	 * @return - строка с фильтром, готовая к использованию в хранимой
	 *         процедуре.
	 */
	public static String generateFilterContextGeneralPart(final String aMutableFilterPart) {
		return "<" + Action.FILTER_TAG + ">" + aMutableFilterPart + "</" + Action.FILTER_TAG + ">";
	}

	/**
	 * Функция, создающая "текущий" контекст.
	 * 
	 * @return - контекст.
	 */
	public static CompositeContext createCurrent() {
		CompositeContext res = new CompositeContext();
		res.main = CURRENT_ID;
		res.additional = CURRENT_ID;
		return res;
	}

	/**
	 * Функция добавления строки фильтра в контекст.
	 * 
	 * @param source
	 *            - контекст источник для фильтра.
	 */
	public void addFilterLine(final CompositeContext source) {
		filter = filter + generateFilterContextLine(source.additional);
	}

	/**
	 * Заканчивает построение фильтра контекста добавляя общую часть.
	 */
	public void finishFilter() {
		filter = generateFilterContextGeneralPart(filter);
	}

	public Map<String, ArrayList<String>> getSessionParamsMap() {
		return sessionParamsMap;
	}

	/**
	 * Функция для установки параметров URL в формате Map<String, List<String>>.
	 * 
	 * @param aData
	 *            - параметры.
	 */
	public final void addSessionParams(final Map<String, List<String>> aData) {
		if (sessionParamsMap == null) {
			sessionParamsMap = new TreeMap<String, ArrayList<String>>();
		}
		sessionParamsMap.clear();
		if (aData == null) {
			return;
		}
		for (Map.Entry<String, List<String>> entry : aData.entrySet()) {
			ArrayList<String> values = new ArrayList<String>();
			values.addAll(entry.getValue());
			sessionParamsMap.put(entry.getKey(), values);
		}
	}

	public void setSessionParamsMap(final Map<String, ArrayList<String>> aSessionParamsMap) {
		sessionParamsMap = aSessionParamsMap;
	}

	public void addRelated(final String aId, final CompositeContext aContext) {
		aContext.setSession(null);
		aContext.setSessionParamsMap(null);
		aContext.getRelated().clear();
		related.put(aId, aContext);
	}

	public Map<String, CompositeContext> getRelated() {
		return related;
	}

	public void setRelated(final Map<String, CompositeContext> aRelated) {
		related = aRelated;
	}
}
