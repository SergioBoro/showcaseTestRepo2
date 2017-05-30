package ru.curs.showcase.security.logging;

import java.util.Random;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.security.logging.Event.TypeEvent;

public class SecurityLoggingCelestaGateway implements SecurityLoggingGateway {

	private final String procName;

	public SecurityLoggingCelestaGateway(final String sProcName) {
		super();
		this.procName = sProcName;
	}

	@Override
	public void doLogging(final Event event) throws Exception {
		CelestaHelper<String> helper =
			new CelestaHelper<String>(event.getContext(), String.class) {
				@Override
				protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
						final Object[] additionalParams) {
					return additionalParams;
				}
			};

		if (event.getTypeEvent() == TypeEvent.LOGINERROR
				|| event.getTypeEvent() == TypeEvent.SESSIONTIMEOUT) {
			String tempSesId = String.format("Logging%08X", (new Random()).nextInt());
			try {
				Celesta.getInstance().login(tempSesId, "userCelestaSid");
			} catch (CelestaException e) {
				e.printStackTrace();
			}
			helper.runPythonWithSessionSet(tempSesId, procName, new Object[] {
					// event.getContext(),
					event.getXml(), event.getTypeEvent().toString() });

		} else {
			helper.runPython(procName, new Object[] {
					// event.getContext(),
					event.getXml(), event.getTypeEvent().toString() });
		}
	}
}
