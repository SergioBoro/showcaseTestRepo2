package ru.curs.showcase.app.server;

import org.slf4j.*;

import ru.beta2.extra.gwt.ui.selector.api.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.grid.GridTransformer;
import ru.curs.showcase.core.selector.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Реализация сервиса для селектора.
 */
public class SelectorDataServiceImpl extends RemoteServiceServlet implements SelectorDataService {

	private static final String SELECTOR_ERROR =
		"При получении данных для селектора возникла ошибка: ";

	private static final long serialVersionUID = 8719830458845626545L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SelectorDataServiceImpl.class);

	@Override
	public DataSet getSelectorData(final DataRequest req) {
		DataSet ds = new DataSet();
		try {

			if (req.getAddData().getContext() instanceof GridContext) {
				GridTransformer.fillFilterContextByListOfValuesInfo((GridContext) req.getAddData()
						.getContext());
			}

			SelectorGetCommand command = new SelectorGetCommand(req);
			ResultSelectorData result = command.execute();

			ds.setFirstRecord(req.getFirstRecord());
			ds.setRecords(result.getDataRecordList());
			ds.setTotalCount(result.getCount());

		} catch (Exception e) {
			// вернётся пустой датасет.
			ds.setTotalCount(0);

			LOGGER.error(SELECTOR_ERROR + e.getMessage());

			throw new ru.beta2.extra.gwt.ui.selector.api.SelectorDataServiceException(
					e.getMessage());
		}

		return ds;
	}
}
