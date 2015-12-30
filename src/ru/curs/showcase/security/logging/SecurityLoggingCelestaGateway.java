package ru.curs.showcase.security.logging;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;

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

		helper.runPython(procName, new Object[] {
				// event.getContext(),
				event.getXml(), event.getTypeEvent().toString() });
	}

}
