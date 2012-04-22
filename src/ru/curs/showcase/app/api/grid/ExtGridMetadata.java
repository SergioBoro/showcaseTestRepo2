package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;

/**
 * Класс грида из ExtGWT с метаданными.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtGridMetadata extends DataPanelCompBasedElement implements SizeEstimate {

	private static final long serialVersionUID = 2492137452715570464L;

	private List<ExtGridColumnConfig> columns = null;

	private ExtGridLiveInfo liveInfo = new ExtGridLiveInfo();

	private ColumnSet originalColumnSet = null;

	private String textColor = null;
	private String backgroundColor = null;
	private String fontSize = null;
	private Set<FontModifier> fontModifiers = null;

	/**
	 * Настройки UI для грида. Как правило, задаются по умолчанию для всех
	 * гридов в файле настроек приложения.
	 */
	private DataGridSettings uiSettings = new DataGridSettings();

	public ExtGridMetadata() {
		super();
	}

	public ExtGridMetadata(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	public final DataGridSettings getUISettings() {
		return uiSettings;
	}

	public final void setUISettings(final DataGridSettings aSettings) {
		uiSettings = aSettings;
	}

	@Override
	protected GridEventManager initEventManager() {
		return null;
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		return result;
	}

	public List<ExtGridColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(final List<ExtGridColumnConfig> aColumns) {
		columns = aColumns;
	}

	public ExtGridLiveInfo getLiveInfo() {
		return liveInfo;
	}

	public void setLiveInfo(final ExtGridLiveInfo aLiveInfo) {
		liveInfo = aLiveInfo;
	}

	public ColumnSet getOriginalColumnSet() {
		return originalColumnSet;
	}

	public void setOriginalColumnSet(final ColumnSet aOriginalColumnSet) {
		originalColumnSet = aOriginalColumnSet;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(final String aTextColor) {
		textColor = aTextColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final String aBackgroundColor) {
		backgroundColor = aBackgroundColor;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(final String aFontSize) {
		fontSize = aFontSize;
	}

	public Set<FontModifier> getFontModifiers() {
		return fontModifiers;
	}

	public void setFontModifiers(final Set<FontModifier> aFontModifiers) {
		fontModifiers = aFontModifiers;
	}

}
