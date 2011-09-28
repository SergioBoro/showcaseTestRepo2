package ru.curs.showcase.app.api.event;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Обобщенный класс события в UI, от которого наследуются события грида,
 * графика... Событие служит для привязки действия к экранному элементу. Событие
 * идентифицируется по типу взаимодействия и нескольким (на данный момент двум)
 * идентификаторам. Например, для грида эти идентификаторы - это идентификаторы
 * строки и столбца. Смысл идентификаторов разъяснен в их описании.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Event implements SerializableElement {

	private static final long serialVersionUID = 3610656112304171914L;

	/**
	 * Первый идентификатор для события. Является обязательным!
	 */
	private String id1;

	/**
	 * Второй идентификатор для события. Не является обязательным - если он не
	 * задан, событие определяется одной "координатой" либо событие может
	 * происходить при любом значении второй "координаты". Координатами
	 * являются, к примеру, строка и столбец в гриде.
	 */
	private String id2;

	/**
	 * Тип взаимодействия пользователя с элементом UI.
	 */
	private InteractionType interactionType;

	/**
	 * Действие, вызываемое по наступлению события.
	 */
	private Action action;

	public Event(final String aId1, final String aId2, final InteractionType aInteractionType) {
		super();
		interactionType = aInteractionType;
		id1 = aId1;
		id2 = aId2;
	}

	public final InteractionType getInteractionType() {
		return interactionType;
	}

	public final void setInteractionType(final InteractionType aInteractionType) {
		interactionType = aInteractionType;
	}

	public final Action getAction() {
		return action;
	}

	public final void setAction(final Action aAction) {
		action = aAction;
	}

	public final String getId1() {
		return id1;
	}

	public final void setId1(final String aId1) {
		id1 = aId1;
	}

	public final String getId2() {
		return id2;
	}

	public final void setId2(final String aId2) {
		id2 = aId2;
	}

	/**
	 * Проверка на совместимость - т.е. на то, что данное событие подходит на
	 * роль переданного функции тестового события.
	 * 
	 * @param testEvent
	 *            - тестовое событие.
	 */
	public boolean isCompatible(final Event testEvent) {
		if (interactionType == testEvent.interactionType) {
			if (id1.equals(testEvent.id1)) {
				return isGeneral() || (id2.equals(testEvent.id2));
			}
		}
		return false;
	}

	/**
	 * Проверка на то, что события является общим - т.е. заданным для некоторого
	 * диапазона элементов, на которых это событие может произойти (пример:
	 * строка в гриде).
	 */
	public boolean isGeneral() {
		return id2 == null;
	}

	public Event() {
		super();
	}
}
