package ru.curs.showcase.app.api.grid;

import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;

/**
 * Класс грида из ExtGWT с результатом загрузки.
 * 
 * @param <Data>
 *            тип данных
 */
public class ExtGridPagingLoadResult<Data> extends BasePagingLoadResult<Data> {

	private static final long serialVersionUID = 3460463555489064222L;

	public ExtGridPagingLoadResult(final List<Data> data) {
		super(data);

	}

	public ExtGridPagingLoadResult(final List<Data> data, final int offset, final int totalLength) {
		super(data, offset, totalLength);

	}

	ExtGridPagingLoadResult() {
		this(null);
	}

	private ExtGridExtradata extGridExtradata = null;

	public ExtGridExtradata getExtGridExtradata() {
		return extGridExtradata;
	}

	public void setExtGridExtradata(final ExtGridExtradata aExtGridExtradata) {
		extGridExtradata = aExtGridExtradata;
	}

}
