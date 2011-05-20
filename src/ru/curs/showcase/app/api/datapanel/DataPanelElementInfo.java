package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс с описанием элемента информационной панели. Примечание: свойство tab в
 * классе носит справочный характер и поэтому не учитывается при сравнении и не
 * сериализуется в XML!
 * 
 * @author den
 * 
 */
public class DataPanelElementInfo extends TransferableElement implements SerializableElement,
		Assignable {
	static final String UNKNOWN_ELEMENT_TYPE = "Неизвестный тип элемента информационной панели";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6461216659708261808L;

	/**
	 * Идентификатор элемента.
	 */
	private String id;

	/**
	 * Позиция элемента на вкладке.
	 */
	private Integer position;

	/**
	 * Тип элемента панели управления.
	 */
	private DataPanelElementType type;

	/**
	 * Наименование хранимой процедуры, которая загружает данные в элемент. Для
	 * тестовых файловых шлюзов - имя файла, из которого загружаются данные.
	 */
	private String procName;

	/**
	 * Наименование файла XSLT трансформации, используемой для преобразования
	 * данных, полученных из БД. Требуется только для элементов типа "WEBTEXT".
	 * 
	 */
	private String transformName;

	/**
	 * Наименование файла шаблона, используемого для отображения элемента.
	 * Шаблон преобразуется в HTML с помощью XSLT. Требуется только для
	 * элементов типа "XFORMS", причем является обязательным для этих элементов.
	 */
	private String templateName;

	/**
	 * Признак того, что при первой отрисовке элемента он прячется и не
	 * наполняется реальными данными. Необходим для оптимизации быстродействия.
	 */
	private Boolean hideOnLoad = false;

	/**
	 * Признак того, что элемент никогда не будет показываться внутри вкладки, а
	 * будет отрываться в внешнем по отношению к вкладке окне.
	 */
	private Boolean neverShowInPanel = false;

	/**
	 * Номер родительской вкладки.
	 */
	private Integer tabPosition;

	/**
	 * Дополнительные процедуры для элемента панели управления. Используются для
	 * элементов XForms.
	 */
	private Map<String, DataPanelElementProc> procs = new TreeMap<String, DataPanelElementProc>();

	public DataPanelElementInfo(final Integer aPosition, final DataPanelTab aTab) {
		super();
		position = aPosition;
		tabPosition = aTab.getPosition();
	}

	public DataPanelElementInfo() {
		super();
	}

	/**
	 * Проверка на то, что данные для элемента заданы корректно.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCorrect() {
		Boolean baseCheckRes = (id != null);
		switch (type) {
		case WEBTEXT:
			return baseCheckRes && ((procName != null) || (transformName != null));
		case GRID:
		case CHART:
		case GEOMAP:
			return baseCheckRes && (procName != null);
		case XFORMS:
			return baseCheckRes && (templateName != null);
		default:
			throw new Error(UNKNOWN_ELEMENT_TYPE);
		}
	}

	public DataPanelElementInfo(final String aId, final DataPanelElementType aType) {
		super();
		id = aId;
		type = aType;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		this.id = aId;
	}

	public final DataPanelElementType getType() {
		return type;
	}

	public final void setType(final DataPanelElementType aType) {
		this.type = aType;
	}

	public final String getProcName() {
		return procName;
	}

	public final void setProcName(final String aProcName) {
		this.procName = aProcName;
	}

	public final String getTransformName() {
		return transformName;
	}

	public final void setTransformName(final String aTransformName) {
		this.transformName = aTransformName;
	}

	public final Integer getPosition() {
		return position;
	}

	public final void setPosition(final Integer aPosition) {
		position = aPosition;
	}

	public Integer getTabPosition() {
		return tabPosition;
	}

	public final void setTab(final Integer aTabPosition) {
		tabPosition = aTabPosition;
	}

	/**
	 * Определяет, нужна ли XSL трансформация для формирования элемента в UI.
	 * 
	 * @return - результат проверки.
	 */
	public boolean needTransform() {
		return (type == DataPanelElementType.WEBTEXT);
	}

	/**
	 * Проверка того, что используется простое сохранение данных с XForms через
	 * GWT.
	 * 
	 * @return - результат проверки.
	 */
	public boolean enabledSimpleSave() {
		return (getSaveProcName() != null);
	}

	public final Boolean getHideOnLoad() {
		return hideOnLoad;
	}

	public final void setHideOnLoad(final Boolean aHideOnLoad) {
		hideOnLoad = aHideOnLoad;
	}

	/**
	 * Возвращает имя процедуры для сохранения данных. Такая процедура может
	 * быть только одна.
	 * 
	 * @return - имя процедуры.
	 */
	public String getSaveProcName() {
		Iterator<DataPanelElementProc> iterator = procs.values().iterator();
		while (iterator.hasNext()) {
			DataPanelElementProc cur = iterator.next();
			if (cur.getType() == DataPanelElementProcType.SAVE) {
				return cur.getName();
			}
		}
		return null;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(final String aTemplateName) {
		templateName = aTemplateName;
	}

	public Map<String, DataPanelElementProc> getProcs() {
		return procs;
	}

	public void setProcs(final Map<String, DataPanelElementProc> aProcs) {
		procs = aProcs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hideOnLoad == null) ? 0 : hideOnLoad.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((neverShowInPanel == null) ? 0 : neverShowInPanel.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((procName == null) ? 0 : procName.hashCode());
		result = prime * result + ((procs == null) ? 0 : procs.hashCode());
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		result = prime * result + ((transformName == null) ? 0 : transformName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataPanelElementInfo)) {
			return false;
		}
		DataPanelElementInfo other = (DataPanelElementInfo) obj;
		if (hideOnLoad == null) {
			if (other.hideOnLoad != null) {
				return false;
			}
		} else if (!hideOnLoad.equals(other.hideOnLoad)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (neverShowInPanel == null) {
			if (other.neverShowInPanel != null) {
				return false;
			}
		} else if (!neverShowInPanel.equals(other.neverShowInPanel)) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (procName == null) {
			if (other.procName != null) {
				return false;
			}
		} else if (!procName.equals(other.procName)) {
			return false;
		}
		if (procs == null) {
			if (other.procs != null) {
				return false;
			}
		} else if (!procs.equals(other.procs)) {
			return false;
		}
		if (templateName == null) {
			if (other.templateName != null) {
				return false;
			}
		} else if (!templateName.equals(other.templateName)) {
			return false;
		}
		if (transformName == null) {
			if (other.transformName != null) {
				return false;
			}
		} else if (!transformName.equals(other.transformName)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public void assignNullValues(final Object source) {
		if (source instanceof DataPanelElementInfo) {
			DataPanelElementInfo sourceInfo = (DataPanelElementInfo) source;
			if (id == null) {
				id = sourceInfo.id;
			}
			if (position == null) {
				position = sourceInfo.position;
			}
			if (procName == null) {
				procName = sourceInfo.procName;
			}
			if (templateName == null) {
				templateName = sourceInfo.templateName;
			}
			if (transformName == null) {
				transformName = sourceInfo.transformName;
			}
			if (tabPosition == null) {
				tabPosition = sourceInfo.tabPosition;
			}
			if (hideOnLoad == null) {
				hideOnLoad = sourceInfo.hideOnLoad;
			}
			if (neverShowInPanel == null) {
				neverShowInPanel = sourceInfo.neverShowInPanel;
			}
			if (type == null) {
				type = sourceInfo.type;
			}
			if (procs.size() == 0) {
				procs.putAll(sourceInfo.procs);
			}
		}
	}

	public Boolean getNeverShowInPanel() {
		return neverShowInPanel;
	}

	public void setNeverShowInPanel(final Boolean aNeverShowInPanel) {
		neverShowInPanel = aNeverShowInPanel;
	}

	@Override
	public String toString() {
		return "DataPanelElementInfo [id=" + id + ", position=" + position + ", type=" + type
				+ ", procName=" + procName + ", transformName=" + transformName
				+ ", templateName=" + templateName + ", hideOnLoad=" + hideOnLoad
				+ ", neverShowInPanel=" + neverShowInPanel + ", procs=" + procs + "]";
	}

	/**
	 * Возвращает значение keepUserSettings, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - keepUserSettings.
	 */
	public Boolean getKeepUserSettings(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			Iterator<DataPanelElementLink> iterator =
				ac.getDataPanelLink().getElementLinks().iterator();
			while (iterator.hasNext()) {
				DataPanelElementLink link = iterator.next();
				if (link.getId().equals(id)) {
					return link.getKeepUserSettings();
				}
			}

			return ac.getKeepUserSettings();
		}
		return null;
	}

	/**
	 * Возвращает текущий контекст для элемента из переданного действия.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - контекст.
	 */
	public CompositeContext getContext(final Action ac) {
		DataPanelLink dpLink = ac.getDataPanelLink();
		if (dpLink != null) {
			Iterator<DataPanelElementLink> iterator = dpLink.getElementLinks().iterator();
			while (iterator.hasNext()) {
				DataPanelElementLink link = iterator.next();
				if (link.getId().equals(id)) {
					return link.getContext();
				}
			}
			return dpLink.getContext();
		}
		return null;
	}
}
