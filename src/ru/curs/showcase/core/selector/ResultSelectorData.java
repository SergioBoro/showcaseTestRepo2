package ru.curs.showcase.core.selector;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.beta2.extra.gwt.ui.selector.api.DataRecord;

/**
 * Результат получения данных селектора.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultSelectorData implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private final List<DataRecord> dataRecordList;
	private final int count;

	public ResultSelectorData() {
		super();
		this.dataRecordList = new ArrayList<>();
		this.count = 0;
	}

	public ResultSelectorData(final List<DataRecord> aDataRecordList, final int aCount) {
		super();
		this.dataRecordList = aDataRecordList;
		this.count = aCount;
	}

	public ResultSelectorData(final DataRecord[] aDataRecordArray, final int aCount) {
		super();
		if (aDataRecordArray != null) {
			this.dataRecordList = Arrays.asList(aDataRecordArray);
		} else {
			this.dataRecordList = null;
		}
		this.count = aCount;
	}

	public List<DataRecord> getDataRecordList() {
		return dataRecordList;
	}

	public int getCount() {
		return count;
	}

}
