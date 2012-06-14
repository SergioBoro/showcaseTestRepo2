package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

/**
 * Класс tree-грида из GXT с результатом загрузки.
 * 
 * @param <Data>
 *            тип данных
 */
@XmlRootElement
@XmlSeeAlso(TreeGridModel.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeGridData<Data> extends ArrayList<Data> {

	private static final long serialVersionUID = -7289102607626253393L;

	public TreeGridData() {
		super();
	}

	@XmlElement
	public List<Data> getTreeGridData() {
		return this;
	}

	private LiveGridExtradata liveGridExtradata = null;

	public LiveGridExtradata getLiveGridExtradata() {
		return liveGridExtradata;
	}

	public void setLiveGridExtradata(final LiveGridExtradata aLiveGridExtradata) {
		liveGridExtradata = aLiveGridExtradata;
	}

}
