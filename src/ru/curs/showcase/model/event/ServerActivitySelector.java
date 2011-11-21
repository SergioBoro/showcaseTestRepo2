package ru.curs.showcase.model.event;

import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.SourceSelector;

/**
 * Выбор способа выполнения серверного действия в зависимости от расширения.
 * 
 * @author den
 * 
 */
public class ServerActivitySelector extends SourceSelector<ActivityGateway> {

	public ServerActivitySelector(final Activity aAct) {
		super(aAct.getName());
	}

	@Override
	public ActivityGateway getGateway() {
		ActivityGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new ActivityJythonGateway();
			break;
		case FILE:
			throw new RuntimeException("Нет естественного способа реализации");
		default:
			res = new ActivityDBGateway();
		}
		return res;
	}
}
