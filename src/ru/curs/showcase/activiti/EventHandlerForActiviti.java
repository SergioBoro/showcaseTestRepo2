package ru.curs.showcase.activiti;

import java.util.Random;

import org.activiti.engine.delegate.event.*;
import org.python.core.PyObject;
import org.slf4j.*;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.BaseException;

/**
 * 
 * @author s.borodanev
 * 
 *         Класс-обработчик событий Activiti.
 * 
 */
public class EventHandlerForActiviti implements ActivitiEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerForActiviti.class);

	private class MyException extends BaseException {
		private static final long serialVersionUID = 6725288887082284411L;

		MyException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	public EventHandlerForActiviti() {
		LOGGER.info("Using Activiti Event Handler...");
	}

	@Override
	public void onEvent(final ActivitiEvent event) {
		LOGGER.info("Event received: " + event.getType());
		LOGGER.info("ExecutionId = " + event.getExecutionId());
		LOGGER.info("ProcessDefinitionId = " + event.getProcessDefinitionId());
		LOGGER.info("ProcessInstanceId = " + event.getProcessInstanceId());

		if (AppInfoSingleton.getAppInfo().getActivitiEventScriptDictionary().isEmpty()) {
			return;
		}

		for (ActivitiEventType aet : AppInfoSingleton.getAppInfo()
				.getActivitiEventScriptDictionary().keySet()) {
			if (aet == event.getType()) {
				String procName =
					AppInfoSingleton.getAppInfo().getActivitiEventScriptDictionary().get(aet);

				final int i3 = 3;
				final int i8 = 8;
				if (procName.endsWith(".cl")) {
					procName = procName.substring(0, procName.length() - i3);

				}
				if (procName.endsWith(".celesta")) {
					procName = procName.substring(0, procName.length() - i8);
				}

				String tempSesId = String.format("Celesta%08X", (new Random()).nextInt());
				try {
					Celesta.getInstance().login(tempSesId, "userCelestaSid");
					PyObject pObj =
						Celesta.getInstance().runPython(tempSesId, procName, (Object[]) null);
					Object obj = pObj.__tojava__(Object.class);
				} catch (CelestaException e) {
					if (e.getMessage().contains("Traceback")) {
						int ind = e.getMessage().indexOf("Traceback");
						String ex = e.getMessage().substring(0, ind - 1).trim();
						throw new MyException(ExceptionType.SOLUTION,
								"При запуске процедуры Celesta произошла ошибка: " + ex);
					} else {
						throw new MyException(ExceptionType.SOLUTION,
								"При запуске процедуры Celesta произошла ошибка: "
										+ e.getMessage());
					}

				} finally {
					try {
						Celesta.getInstance().logout(tempSesId, false);
					} catch (CelestaException e) {
						if (e.getMessage().contains("Traceback")) {
							int ind = e.getMessage().indexOf("Traceback");
							String ex = e.getMessage().substring(0, ind - 1).trim();
							throw new MyException(ExceptionType.SOLUTION,
									"При запуске процедуры Celesta произошла ошибка: " + ex);
						} else {
							throw new MyException(ExceptionType.SOLUTION,
									"При запуске процедуры Celesta произошла ошибка: "
											+ e.getMessage());
						}
					}
				}
			}
		}

	}

	@Override
	public boolean isFailOnException() {
		return false;
	}
}
