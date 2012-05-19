package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

/**
 * Класс грида из GXT с результатом загрузки.
 * 
 * @param <Data>
 *            тип данных
 */
@XmlRootElement
@XmlSeeAlso(LiveGridModel.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class LiveGridData<Data> extends PagingLoadResultBean<Data> {

	private static final long serialVersionUID = 3460463555489064222L;

	public LiveGridData(final List<Data> data, final int offset, final int totalLength) {
		super(data, totalLength, offset);
	}

	LiveGridData() {
		super();
	}

	private LiveGridExtradata liveGridExtradata = null;

	public LiveGridExtradata getLiveGridExtradata() {
		return liveGridExtradata;
	}

	public void setLiveGridExtradata(final LiveGridExtradata aLiveGridExtradata) {
		liveGridExtradata = aLiveGridExtradata;
	}

}
