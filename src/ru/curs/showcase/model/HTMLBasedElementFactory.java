package ru.curs.showcase.model;

import java.util.Collection;

import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.model.event.EventFactory;

/**
 * Базовый класс фабрики для элементов, основанных на HTML.
 * 
 * @author den
 * 
 */
public abstract class HTMLBasedElementFactory extends TemplateMethodFactory {

	public HTMLBasedElementFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	/**
	 * Метод для чтения настроек элемента.
	 * 
	 */
	private void readProperties() {
		if (getSource().getProperties() == null) {
			return;
		}

		EventFactory<HTMLEvent> factory = new EventFactory<HTMLEvent>(HTMLEvent.class);
		factory.intiForGetSimpleEvents(LINK_ID_TAG, null);
		Collection<HTMLEvent> events = factory.getSimpleEvents(getSource().getProperties());
		((HTMLEventManager) getResult().getEventManager()).getEvents().addAll(events);

		getResult().setDefaultAction(factory.getDefaultAction());
	}

	@Override
	protected void setupDynamicSettings() {
		readProperties();
	}

	@Override
	public HTMLBasedElementRawData getSource() {
		return (HTMLBasedElementRawData) super.getSource();
	}

	@Override
	protected void fillResultByData() {
		transformData();
	}

	/**
	 * Метод, трансформирующий данные из БД в HTML код.
	 * 
	 */
	protected abstract void transformData();

	@Override
	protected void prepareData() {
		// не используется для HTML элементов
	}

	@Override
	protected void prepareSettings() {
		// не используется для HTML элементов
	}

	@Override
	protected void releaseResources() {
		// не используется для HTML элементов
	}
}
