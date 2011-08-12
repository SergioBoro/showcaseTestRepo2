package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralException;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Обработчик запроса на получение Excel файла по данным в гриде. Вызывается из
 * сервлета. Отдельный класс (и один объект на один запрос) нужны для того,
 * чтобы избежать проблем многопоточности.
 * 
 * @author den
 * 
 */
public class GridToExcelHandler extends AbstractDownloadHandler {
	/**
	 * Тип экспорта в Excel.
	 */
	private GridToExcelExportType exportType;
	/**
	 * Описание настроенного пользователем набора столбцов.
	 */
	private ColumnSet columnSet;

	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return GridContext.class;
	}

	@Override
	protected void processFiles() throws GeneralException {
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(getRequest().getSession().getId());
		setOutputFile(serviceLayer.generateExcelFromGrid(exportType, getContext(),
				getElementInfo(), columnSet));
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		exportType = GridToExcelExportType.valueOf(getParam(GridToExcelExportType.class));
		columnSet = (ColumnSet) deserializeObject(getParam(ColumnSet.class));
	}
}